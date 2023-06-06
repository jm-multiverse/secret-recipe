# Multiverse Back-End Project 1
## Table of Contents

1. [Introduction](#introduction)
2. [Project requirements](#requirements)
3. [Getting started](#gettingstarted)
4. [Conclusions](#conclusions)

<div id='introduction'/>

## Introduction:

Secret Recipe API is a place where users can register and log in to view, create, edit, and delete, recipes. The tech stack consists of Kotlin Spring Boot and H2 database persistance. 

<div id='requirements'>

## Project requirements:

GOAL: Build a back end, RESTful CRUD application demonstrating proficiency in these key areas
Team Development
Requirements, Tests, and System Architecture 🤓
Agile development and problem solving 🤔
Task assignment and issue tracking 📝
Pair Programming 👫
Code Reviews 🔭
Your tech stack of choice (must be agreed upon by the group). If no preference, you’ll be using the bootcamp stack
SQL, Sequelize
Node, Express
SWE collaboration norms
Version Control w/ Git (branching, PRs) 🌳
Task Management w/ Github Projects 👩‍💼 👨‍💼 🧑‍💼
Deployment w/ Render, Railway, or your platform of choice 🚀
Unit Testing w/ Jest, Postman, and/or your tools of choice 🧪
<div id='gettingstarted'/>

## Getting started:
### Deployed page:

* Navigate to https://secret-recipe-api.onrender.com/api
* Send requests to endpoints:
  
  Recipe fields: 
  id: Long - The id of the recipe stored in the database
  title: String - The title of the recipe
  content: String - The instructions for the recipe
  addedAt: LocalDateTime - The date the recipe was added
  
  GET /recipe - Gets all recipes
  GET /recipe/{id} - Get recipe by id
  POST /recipe - Create a new recipe
  PUT /recipe - Update recipe (include id in body)
  DELETE /recipe/{id} - Delete a recipe
  
  User fields: 
  id: Long - The id of the user stored in the database
  email: String - The users email
  password: String - The users password

  POST /auth/register - Register a new user
  POST /auth/login - Log in

<div id='conclusions'/>

## Conclusions:


