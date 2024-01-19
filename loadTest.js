import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  vus: 1, // Number of virtual users
  duration: '30m', // Duration of the test
};

// In the API when we create users, recipes, and reviews, the id automatically increments from 1.
// We store the total amount of 'ids' by incrementing the counters upon creation.
// This allows us to randomly get an id, knowing that it has been created, without storing the ids themselves.
let userIdCounter = 1
let recipeIdCounter = 1
let reviewIdCounter = 1

function randomId(ids) {
  return Math.floor(Math.random() * ids) + 1;
}

function prettyLog(name, object) {
  console.log(`${name}`, JSON.stringify(object, null, 2));
}

export default function () {
  userFlow()
}

function userFlow() {
  // Register
  let uniqueId = Math.random()
  let email = `testuser${uniqueId}@example.com`
  let password = "password123"

  let registerPayload = JSON.stringify({
    email: email,
    password: password,
  });

  let registerResponse = http.post('http://localhost:8100/api/auth/register', registerPayload, {
    headers: { 'Content-Type': 'application/json' },
  });

  let user = registerResponse.json('data')
  userIdCounter++
  prettyLog("User", user)
  sleep(1);

  // Login
  let loginPayload = JSON.stringify({
    email: email,
    password: password,
  });

  let loginResponse = http.post('http://localhost:8100/api/auth/login', loginPayload, {
    headers: { 'Content-Type': 'application/json' },
  });

  let cookies = loginResponse.cookies;
  let accessToken;

  if (cookies && cookies.accessToken && cookies.accessToken.length > 0) {
    accessToken = cookies.accessToken[0].value;
  } else {
    console.log("Access token not found in cookies");
  }

  prettyLog("accessToken", accessToken)
  sleep(1)

  // Create Recipe
  let recipePayload = JSON.stringify({
    title: `Recipe ${Math.random()}`,
    content: 'Random recipe content',
    tags: ["tag1", "tag2"]
  });

  let createRecipeResponse = http.post('http://localhost:8100/api/recipes', recipePayload, {
    headers: {
      'Content-Type': 'application/json',
      'Cookie': `accessToken=${accessToken}`
    },
  });

  let recipe = createRecipeResponse.json('data')
  recipeIdCounter++
  prettyLog("Recipe", recipe)
  sleep(1);

  // Create Review
  let reviewPayload = JSON.stringify({
    title: 'Review title',
    content: 'Random review content',
    rating: Math.floor(Math.random() * 5) + 1 // Random number between 1 - 5
  });

  let createReviewResponse = http.post(`http://localhost:8100/api/recipes/${randomId(recipeIdCounter)}/reviews`, reviewPayload, {
    headers: {
      'Content-Type': 'application/json',
      'Cookie': `accessToken=${accessToken}`
    },
  });

  let review = createReviewResponse.json('data')
  reviewIdCounter++
  prettyLog("Review", review)
  sleep(1)

  // Save Random Recipe
  let saveRecipeResponse = http.post(`http://localhost:8100/api/recipes/${randomId(recipeIdCounter)}/save`, null, {
    headers: {
      'Content-Type': 'application/json',
      'Cookie': `accessToken=${accessToken}`
    },
  });
  let savedRecipe = saveRecipeResponse.json('data')
  prettyLog("Saved Recipe: ", savedRecipe)
  sleep(1)

  // Like Random Review
  let likeReviewResponse = http.post(`http://localhost:8100/api/reviews/${randomId(reviewIdCounter)}/like`, null, {
    headers: {
      'Content-Type': 'application/json',
      'Cookie': `accessToken=${accessToken}`
    },
  });
  let likedReview = likeReviewResponse.json('data')
  prettyLog("Liked Review: ", likedReview)
  sleep(1)

  // Follow Random User
  let followUserResponse = http.post(`http://localhost:8100/api/users/${randomId(userIdCounter)}/follow`, null, {
    headers: {
      'Content-Type': 'application/json',
      'Cookie': `accessToken=${accessToken}`
    },
  });
  let followingList = followUserResponse.json('data')
  prettyLog("Followed User. User's Following List: ", followingList)
  sleep(1)

  // Unfollow Random User
  let unfollowUserResponse = http.post(`http://localhost:8100/api/users/${randomId(userIdCounter)}/unfollow`, null, {
    headers: {
      'Content-Type': 'application/json',
      'Cookie': `accessToken=${accessToken}`
    },
  });
  let followingList2 = unfollowUserResponse.json('data')
  prettyLog("Unfollowed User (Try). User's Following List: ", followingList2)
  sleep(1)

  // Logout
  let logoutResponse = http.post(`http://localhost:8100/api/auth/logout`, null, {
    headers: {
      'Content-Type': 'application/json',
      'Cookie': `accessToken=${accessToken}`
    },
  });
  let logoutMessage = logoutResponse.json('data')
  prettyLog("Logout Message: ", logoutMessage)
  sleep(1)

  // Call Unauthorized Route
  // Call Not Found Route
}