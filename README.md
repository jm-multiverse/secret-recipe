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
- **Recipe Browsing, Saving, Liking, and Reviewing**: Users can discover new recipes, save their favorites ones, express their appreciation through likes, and provide insight through reviews.
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

## API Endpoints
The Secret Recipe API offers a variety of endpoints for interacting with the API. The following is a brief overview of the available endpoints.

- **Authentication**: For user registration, authentication, and authorization.
- **Users**: For managing users profiles, following other users, and account settings.
- **Recipes**: To create, edit, delete, and view recipes.
- **Reviews**: To create, edit, delete, and view reviews.

*Note: For a full list of available endpoints, including request and response formats, refer to the [API Documentation]().*

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

