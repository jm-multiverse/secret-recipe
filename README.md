# Secret Recipe API

## Table of Contents

- [Introduction](#introduction)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Architecture and Design](#architecture-and-design)
- [Future Enhancements](#future-enhancements)
- [Contributing](#contributing)
- [License](#license)
- [Contact Information](#contact-information)
- [Acknowledgements](#acknowledgements)

## Introduction

Secret Recipe API is a RESTful social platform for food enthusiasts to share and discover recipes. Built with Kotlin and Spring Boot, it features JWT for secure authentication and integrates Grafana/Prometheus for monitoring. It's designed for scalability and flexibility, supporting a broad range of social interactions among users.

## Features
- **User Authentication**: Secure login and role-based access using JWT.
- **Profile Management**: Customize profiles with images, bios, and more.
- **Recipe Handling**: Create, edit, delete, and manage recipe visibility.
- **Community Interaction**: Browse, save, like, and review recipes, and follow other users.

## Documentation
A full list of available endpoints, including request and response formats is available in the [Postman API Documentation](https://documenter.getpostman.com/view/15910732/2s93sc4Chu).

### Endpoints
The following is a brief overview of the available endpoints.

- **Authentication**: For user registration, authentication, and authorization.
- **Users**: For managing users profiles, following other users, and account settings.
- **Recipes**: To create, edit, delete, and view recipes.
- **Reviews**: To create, edit, delete, and view reviews.

## Getting Started
Run the API locally for development/testing using Java JDK 11+, Docker, and Docker Compose.

### Quick Start
**Local Setup**: This is a simplest method for running the API locally, allowing for quick and easy setup, breakpoint debugging, and testing. Clone the repository and navigate to the directory.

```sh
# Run the application using Gradle
./gradlew bootRun
```

The project should now be running locally on port 8080: [http://localhost:8080](http://localhost:8080)

### Docker Setup
The API can also be run using Docker Compose. This method runs the application in a containerized environment. It also allows for the use of Prometheus and Grafana for metrics collection and visualization.

1. Start Docker Desktop.
2. Run the following command:

```sh
# Build the .jar file using Gradle
.\gradlew bootJar --no-daemon

# Run the application using Docker Compose
docker-compose up
```

The services can be accessed at the following URLs:
* Secret Recipe API: [http://localhost:8080](http://localhost:8080)
* Prometheus: [http://localhost:9090](http://localhost:9090)
* Grafana: [http://localhost:3000](http://localhost:3000)

### Observability

**Spring Boot Actuator** provides operational information about the application. Visit the following to view the health, info, and metrics: [http://localhost:8080/actuator](http://localhost:8080/actuator) 

**Prometheus and Grafana** are used for metrics collection and visualization.
There are two dashboards configured to be available  on start up:
* JVM (Micrometer)
* Spring Boot 2.1 System Monitor 

To view the dashboards, visit the Grafana URL and select them from `Dashboards` > `Services`. JSON models of them can be found in `grafana/provisioning/dashbards`. These are dashboards provided by Grafana, and more can be found on their website: [Grafana Dashboards](https://grafana.com/grafana/dashboards/)

Run the load tests with K6 to generate traffic and metrics and view the results in Grafana - See [testing](#testing) for instructions.

### Usage
The Secret Recipe API is best explored using the Postman API client.

1. Install Postman from [Postman's official website](https://www.postman.com/downloads/).
2. Import the provided Postman collection and environment files located in the project's `postman` directory. This collection contains pre-configured requests to various endpoints of the API.
3. Ensure the API is running locally (should be on port 8080), then send requests using Postman to interact with the API.

#### With Curl
For those preferring direct API interaction via command line, here's an example using `curl`:

```sh
# Example: Request to get all public recipes
curl -X GET http://localhost:8080/api/recipes
```

## Testing

### Integration Tests
Integration tests are conducted using the JUnit 5 testing framework and are located in the `src/test/kotlin` directory. 

To run all the test files:
```sh
# Run the tests using Gradle
./gradlew test
```

Alternatively, you can run the individual tests classes located in the `src/test/kotlin` directory. The integration tests will need to be run as a class, not individually, as they are set to simulate user interaction and rely on data from previous tests.

### Load Tests
Load tests are conducted using K6 and are located in the `k6` directory. These tests simulate real-world traffic and usage scenarios, allowing for the evaluation of the application's performance and scalability.

To run the load tests, run the following command:
```sh
# Run the load test using K6
k6 run k6/load-test.js
```

## Architecture and Design

### Overview
The Secret Recipe API is designed with a layered architecture to ensure clean separation of concerns and ease of maintenance. It is structured as follows:

- **Entity Layer**: Data model representation with Spring JPA. The `entity` directory includes entity classes, builders, and mappers. It also includes custom filters for querying, such as the `ActiveUsersFilter`.
- **Repository Layer**: Data access layer using Spring Data JPA. It abstracts data persistence and interactions.
- **Service Layer**: Business logic, DTO transformations, data validation and operations. 
- **Controller Layer**:  Handle HTTP requests and generate `ApiResponse<T>`. It invokes service layer methods, and returns responses, serving as the entry point for API interactions.


### Data Transfer Objects (DTOs)
DTOs, located in the /transfer directory, are used for data exchange between the client and server, ensuring a clean separation from the entity layer. They include user, recipe, and review details, and are extensively used in the controller and service layers.

### Handling Responses
The service layer uses Result<T> to indicate operation outcomes, encapsulating success data or error messages. Controllers utilize ApiResponse<T> to provide consistent API responses, ensuring uniformity and simplifying error handling across various endpoints.

#### Service Layer Outcome Encapsulation
```kotlin
sealed class Result<out T> {
    abstract val status: HttpStatus
    
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}
```
*Note: This is a simplified representation of the result class. The actual class initializes status with HttpStatus.OK or HttpStatus.BAD_REQUEST and provides a constructor override for setting the status.*

#### Controller Layer Response Generation
```kotlin
data class ApiResponse<out T : Any>(
    val data: T? = null,
    val error: String? = null
)

object ResponseBuilder {
    fun <T : Any> respond(result: Result<T>): ResponseEntity<ApiResponse<T>> { ... }
        // Sets the response data or error message based on the result
        // Returns a ResponseEntity with the appropriate status code
}
```

This architecture and design approach promotes a robust, scalable, and maintainable codebase, enabling efficient and effective feature development and enhancements. The following is an example of how `Result<T>` and `ApiResponse<T>` are used to generate responses:
```kotlin
val successResult: Result<String> = Result.Success("Hello")
val errorResult: Result<Nothing> = Result.Error("Something went wrong")

val successResponse: ResponseEntity<ApiResponse<String>> = respond(successResult)
val errorResponse: ResponseEntity<ApiResponse<Nothing>> = respond(errorResult)
```

## Future Enhancements 
There are several enhancements that can be planned to enrich user experience, application functionality, and scalability. Some of these include:
- **OAuth2 Integration**: Introduce OAuth2 for robust and flexible user authentication, allowing sign-in via popular social media platforms.
- **Recipe Model Enrichment**: Expand recipe details to include comprehensive information like ingredients, nutrition facts, and cooking steps, bolstering recipe management, discovery, and advanced search capabilities.
- **Enhanced Authentication Roles**: Introducing block/allow lists for access to resources and define admin roles for effective monitoring and moderation of online communities.
- **User Activity Features**: Introduce features like activity feeds and recipe sharing among specific user groups to enrich the social aspect of the platform. 
- **Advanced Search Filters**: Develop sophisticated search filters based on dietary preferences, nutritional content, and user ratings, enhancing recipe discovery.
- **Database Enhancements**: Migrate to robust systems like MySQL for reliable, persistent data storage, essential for production deployment and scaling to support extensive user bases.
- **Database and Performance Improvements**: Transition to robust database systems like MySQL for reliable data storage. This is also critical for deploying the application to production environments and scaling it to support a large user base.
- **Container Management with Kubernetes**: Employ Kubernetes for efficient container orchestration, ensuring optimal resource utilization and scalability.
- **CI/CD Automation**: Implement CI/CD pipelines using tools like GitHub Actions for streamlined, automated build and deployment processes.
- **Proactive Monitoring and Alerts**: Utilize Prometheus and Grafana for advanced monitoring and alerting.
- **Rich Seed Data**: Populate extensive seed data to enrich the initial application setup, providing a comprehensive environment for development and testing.
- **Language Support**: Enhance accessibility by introducing multilingual support, catering to a global audience.
- **UI/UX Enhancements**: Introduce a modern, responsive UI/UX design to enrich user experience and engagement.

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

## License
The Secret Recipe API is licensed under the [GNU General Public License v3.0](LICENSE) - see the [LICENSE](LICENSE) file for details.

## Contact Information
For any questions/comments about the project or if you want to connect further, feel free to connect on [LinkedIn](https://www.linkedin.com/in/jonathan-mantello/).

## Acknowledgements
Thank you to:
- **Multiverse/Expedia Group** for the apprenticeship program and providing me with an opportunity to learn and grow as a software development engineer.
- My colleagues and fellow apprentices for their encouragement throughout the program.
- The developers of the tools and libraries used in this project, including Kotlin, Spring Boot, Docker, and many more.
