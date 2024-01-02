# Secret Recipe API

## Table of Contents

- [Introduction](#introduction)
  - [Project Overview](#project-overview)
  - [Purpose and Functionality](#purpose-and-functionality)
  - [Key Features](#key-features)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Usage](#usage)
- [API Documentation](#api-documentation)
  - [Endpoints](#endpoints)
- [Testing](#testing)
- [Architecture and Design](#architecture-and-design)
  - [Overview](#overview)
  - [Transfer Objects](#transfer-objects)
  - [Service Utilization of `Result<T>`](#service-utilization-of-resultt)
  - [Controller Utilization of `ApiResponse<T>`](#controller-utilization-of-apiresponset)
- [Future Enhancements](#future-enhancements)
- [Contributing](#contributing)
  - [Reporting Issues](#reporting-issues)
  - [Submitting Changes](#submitting-changes)
  - [Code of Conduct](#code-of-conduct)
- [License](#license)
- [Contact Information](#contact-information)
- [Acknowledgements](#acknowledgements)

## Introduction

### Project Overview
Welcome to the GitHub repository for the Secret Recipe API, a RESTful CRUD API designed to simulate an online community of foodies. This API uses Kotlin and Spring Boot, and was built over the period of about 6 months for the Back-End and Elective modules of my learning journey with the Multiverse/Expedia Group Software Engineering Apprenticeship.

### Purpose and Functionality
At its core, the Secret Recipe API is designed as a social platform for food enthusiasts to publish their recipes, browse others' recipes, save, like, and review recipes, and follow and be followed by other users. It also allows users to manage the privacy of their recipes, allowing them to choose which recipes are available to the public and which recipes can remain secret. 

### Key Features
- **User Authentication and Authorization**: Utilizing JSON Web Tokens (JWTs) for secure user authentication. Features include account creation, login/logout, and role-based access control to ensure only authorized users can modify their own recipes.
- **User Profile Management**: Users can customize their online presence. Features includes uploading profile pictures, adding bios, and managing usernames and passwords.
- **Recipe Creation, Editing, and Deletion**: Provides users with comprehensive tools for managing their recipes. Users can upload images, add descriptions and tags, and list ingredients and instructions.
- **Recipe Privacy Management**: Enables users to manage the visibility of their recipes. options include sharing publicly, or restricting access to their followers or only themselves.
- **Recipe Browsing, Saving, Liking, and Reviewing**: Users can discover new recipes, save their favorites, express their appreciation through likes, and provide insight through reviews.
- **User Following and Followers**: Users can follow and be followed by other users as well as manage their following list, enabling social connections and enriching the community experience.

## Getting Started
To get started with the Secret Recipe API, follow these instructions to copy the project and run it locally.

### Prerequisites
- **Java JDK 11 or Higher**: The application is built using Kotlin, which runs on the Java Virtual Machine (JVM). JDK 11 or higher is required to compile and run the Kotlin code.
- **Docker**: Docker is used for containerizing the application, making it easy to build, deploy, and run the API consistently across any environment. Docker Compose is utilized to manage multi-container Docker applications, streamlining the setup process.

### Installation
A step-by-step guide to getting the project running locally.

```sh
# Clone the repository
git clone https://github.com/jm-multiverse/secret-recipe

# Navigate to the project directory
cd secret-recipe

# Run the project using the Makefile
make all
```

The project should now be running locally on port 8080. 

### Usage
The Secret Recipe API is best explored using the Postman API client. Follow these steps to get started:

1. Install Postman from [Postman's official website](https://www.postman.com/downloads/).
2. Import the provided Postman collection and environment files located in the project's `postman` directory. This collection contains pre-configured requests to various endpoints of the API.
3. Ensure the API is running locally (should be on port 8080), then send requests using Postman to interact with the API.

For those preferring direct API interaction via command line, here's an example using `curl`:

```sh
# Example: Request to get all public recipes
curl -X GET http://localhost:8080/api/recipes/public
```

## API Documentation
The Secret Recipe API is documented using Postman. The full documentation can be found here: [API Documentation](https://documenter.getpostman.com/view/15910732/2s93sc4Chu).

### Endpoints
The Secret Recipe API offers a variety of endpoints for interacting with the API. The following is a brief overview of the available endpoints.

- **Authentication**: For user registration, authentication, and authorization.
- **Users**: For managing users profiles, following other users, and account settings.
- **Recipes**: To create, edit, delete, and view recipes.
- **Reviews**: To create, edit, delete, and view reviews.

*Note: For a full list of available endpoints, including request and response formats, refer to the [API Documentation]().*

## Testing
The Secret Recipe API is tested using the JUnit 5 testing framework. The tests are located in the `src/test/kotlin` directory. To run the tests, use the following command:

```sh
# Run the tests using the Makefile
make test
```

## Architecture and Design

### Overview
The Secret Recipe API uses a layered architecture, promoting separation of concerns and scalability. The following is a brief overview of the project's architecture and design.

- **Entity**: Entities represent the application's data model, mapped to the database using Spring JPA. These data classes not only facilitate CRUD operations but also support conversion to and from Data Transfer Objects (DTOs). Builders and mappers associated with these entities are also included in this layer for streamlined data handling.
- **Repository**: The repository layer consists of interfaces defining data access and manipulation methods. These interfaces are extended by Spring Data JPA to automatically generate SQL queries. Repository classes, marked with `@Repository` and `@Transactional`, interact with the database and provide a level of abstraction over data persistence operations.
- **Service**: The service layer encapsulates the business logic of the application. Annotated with `@Service`, these classes handle data validation, business rule enforcement, and perform operations by interfacing with the repository layer. They play a crucial role in transforming entities into DTOs for further processing or API responses.
- **Controller**: Controllers, marked with `@RestController`, handle incoming HTTP requests and generate responses. They validate request data, invoke appropriate service layer methods, and return responses encapsulated in `ApiResponse<T>` objects.


### Transfer Objects
Located in the `/transfer` directory, request and response data transfer objects (DTOs) facilitate data exchange between the client and server. They are used in the controller and service layers to receive client data and send back responses. DTOs typically include user information, recipe details, and other user interactions, distinctly separating these data aspects from the entity layer.

Model DTOs can be found in the `/transfer/model` directory. These DTOs are used to represent the data model of the application, including user profiles, recipes, and reviews. They are used primarily from the service layer outward, and they are preffered because they allow control over the information being sent to the client.

### Service Utilization of `Result<T>`
The service layer utilizes `Result<T>` to convey operation outcomes, encapsulating either successful data (`Success<T>`) or error messages (`Error`). This structure simplifies error handling and response generation.

```kotlin
sealed class Result<out T> {
  abstract val status: HttpStatus

  data class Success<T>(
    override val status: HttpStatus,
    val data: T
  ) : Result<T>() {
    constructor(data: T) : this(HttpStatus.OK, data)
  }

  data class Error(
    override val status: HttpStatus,
    val message: String
  ) : Result<Nothing>() {
    constructor(message: String) : this(HttpStatus.BAD_REQUEST, message)
  }
}
```

*Note the constructor overloading in the `Success` and `Error` classes. This allows for the omission of the `status` parameter when creating a new `Result` object, defaulting to `HttpStatus.OK` and `HttpStatus.BAD_REQUEST` respectively.*

### Controller Utilization of `ApiResponse<T>`
Controllers leverage `ApiResponse<T>` for consistent API responses, containing either data or error details. The ResponseBuilder constructs these responses, ensuring uniformity across different API endpoints.

```kotlin
data class ApiResponse<out T : Any>(
    val data: T? = null,
    val error: String? = null
)

object ResponseBuilder {
  fun <T : Any> respond(result: Result<T>): ResponseEntity<ApiResponse<T>> {

    val apiResponse = when (result) {
      is Result.Success -> ApiResponse(data = result.data)
      is Result.Error -> ApiResponse(error = result.message)
    }

    return ResponseEntity.status(result.status).body(apiResponse)
  }
}
```

The use of both `Result<T>` and `ApiResponse<T>` allows for a clear separation of concerns between the service and controller layers and simplifies error handling and response generation. The following is an example of how you can call respond with different types of `Result`:

```kotlin
val successResult: Result<String> = Result.Success("Hello")
val errorResult: Result<Nothing> = Result.Error("Something went wrong")

val successResponse: ResponseEntity<ApiResponse<String>> = respond(successResult)
val errorResponse: ResponseEntity<ApiResponse<Nothing>> = respond(errorResult)
```

The `T` in `ApiResponse<T>` gets replaced with the actual type of the data in the Result. The respond function is flexible and can work with different types, thanks to the use of generics.

## Future Enhancements
There are many opportunities for growth and new features with this project. The following is a list of enhancements to further enrich the user experience and expand the functionality of the application:
- **Enhanced User Data and Authentication Roles**: Enhance the User model to include more personal details like bios and profile pictures, creating a richer and more personalized user experience. Additionally, introducing Admin roles for effective monitoring and moderation of online communities, ensuring a consistent and fair user experience. 
- **Enhanced Recipe Data**: Updating the Recipe model to ecompass more information about the recipe, such as ingredients and nutrition facts, to allow for more comprehensive searching and filtering. 
- **Advanced Search and Filtering**: Develop sophisticated search capabilities, including filters for dietary preferences, nutritional information, and user ratings, allowing users to discover recipes that are more tailored to their preferences.
- **Database Integration**: Migrating to a more robust database system like MySql would enable persistant data storage across application instances, ensuring the data is accurate and consistant.
- **Logging and Metrics Collection**: Implementing logging and metrics collection would pave the way for utilization of services such as Splunk and Datadog. This would significantly improve the monitoring of application traffic, health, and performance.
- **Seed Data**: Creating a comprehensive set of seed data would greatly enhance the initial user experience and provide a richer context for development and testing.
- **K6 Load Testing**: Incorporating load testing to simulate real-world traffic and usage scenarios would help to ensure the application is functioning optimally. This enhancement, along with the aforementioned metrics collection, would provide a holistic view of the application's status and the impact of any changes.
- **Multilingual Support**: Expanding the app's accessibility by supporting multiple languages, making it more inclusive for a global user base.
- **SwaggerUI Integration**: Finalizing and publishing comprehensive API documentation using a tool like SwaggerUI for better developer engagement and ease of use would simplify the process and understanding of using the API.

## Contributing
Contributions to the Secret Recipe API are welcome and valued. Whether it's bug fixes, feature enhancements, or documentation improvements, your input helps in building a better project. Here’s how to contribute:

### Reporting Issues
Found a bug or have an idea for improvement? First, please check the [Issues tab](https://github.com/jm-multiverse/secret-recipe/issues) to see if it has already been reported. If not, feel free to open a new issue. Please provide as much information as possible to help us understand and address the issue quickly.

### Submitting Changes
If you'd like to contribute code or documentation, you can follow these steps:

1. Fork the repository.
2. Create a new branch for your changes. (`git checkout -b my-new-feature`)
3. Make your changes in the new branch.
4. Commit your changes with a clear and descriptive commit message.
5. Push the changes to your fork and submit a pull request to the main repository.

For all submissions:
- Please ensure your code adheres to the existing coding standards and conventions.
- Verify that all existing tests pass and add tests for any new or changed functionality.
- Update the documentation to reflect any changes.

Once submitted, your pull request will be reviewed by a maintainer to ensure it meets the project's standards of code quality, documentation, and test coverage. If any further changes are requested, please address them and push to your branch for review again. Once approved, your pull request will be merged into the main repository.

### Code of Conduct
We value all contributions and want to ensure a positive experience for all contributors and users. Please review and abide by the [Code of Conduct](CODE_OF_CONDUCT.md) when contributing to this project.

## License
The Secret Recipe API is licensed under the [GNU General Public License v3.0](LICENSE) - see the [LICENSE](LICENSE) file for details.

## Contact Information
For any questions, support requests, or to discuss potential collaboration opportunities, feel free to reach out:
- LinkedIn: [https://www.linkedin.com/in/jonathan-mantello/](https://www.linkedin.com/in/jonathan-mantello/)

## Acknowledgements
A special thanks to:
- **Multiverse/Expedia Group** for the apprenticeship program and providing me with the opportunity to learn and grow as a software engineer.
- My colleagues and fellow apprentices for their support and encouragement throughout the program.
- The developers of the tools and libraries used in this project, including Kotlin, Spring Boot, Docker, and many others.

