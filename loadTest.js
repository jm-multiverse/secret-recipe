import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  vus: 10, // Number of virtual users
  duration: '30m', // Duration of the test
};

let userIds = [1, 2, 3]
let recipeIds = [1]
let reviewIds = [1]

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
  userIds.push(user["id"])

  sleep(1);

  // Login
  let loginPayload = JSON.stringify({
    email: email,
    password: password,
  });

  let loginResponse = http.post('http://localhost:8100/api/auth/login', loginPayload, {
    headers: { 'Content-Type': 'application/json' },
  });

  let authToken = loginResponse.json('accessToken');

  // Create Recipe
  let recipePayload = JSON.stringify({
    title: `Recipe ${Math.random()}`,
    content: 'Random recipe content',
    tags: ["tag1", "tag2"]
  });

  let createRecipeResponse = http.post('http://localhost:8100/api/recipes', recipePayload, {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${authToken}`
    },
  });

  let recipe = createRecipeResponse.json('data')
  recipeIds.push(recipe['id'])
  sleep(1);

  // Create Review
  let reviewPayload = JSON.stringify({
    title: 'Review title',
    content: 'Random review content',
    rating: 4
  });

  let randomRecipeId = recipeIds[0]

  let createReviewResponse = http.post(`http://localhost:8100/api/recipes/${randomRecipeId}`, reviewPayload, {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${authToken}`
    },
  });

  let review = createRecipeResponse.json('data')
  reviewIds.push(review['id'])
  sleep(1)

}