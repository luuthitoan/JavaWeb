
# Back-End System for E-commerce Platform

## Introduction

This project is a back-end application designed to provide secure and efficient API services for the shop app front-end. It was developed using the Spring Boot framework, along with key modules such as Spring Security for authentication and authorization, and Spring Data JPA for database interactions. The goal is to deliver a reliable, scalable, and secure back-end system that meets the requirements of modern web applications.

### Why was this project created?

This project was created to build a secure and scalable back-end system for managing products, orders, and users in an online store. It aims to simplify inventory management, streamline order processing, and provide a robust authentication system for both customers and administrators

## Install

To set up and run the back-end application on your local environment, follow these steps:

### Clone the repository:

`git clone https://github.com/hcdman/shopapp-backend.git`

### Navigate to the project directory:

`cd shopapp-backend`

### Configure your database:

Update the `application.properties` or `application.yml` file with your database details:

```
spring.datasource.url=jdbc:mysql://localhost:3306/your-database-name
spring.datasource.username=your-username
spring.datasource.password=your-password
spring.jpa.hibernate.ddl-auto=update
```

### Build the project:

Make sure you have Maven or Gradle installed. Run the following command to build the project:

`mvn clean install`

### Run the application:

After building, you can start the application with:

`mvn spring-boot:run`

### Access the API:

The application should be running at `http://localhost:8080`.

## Use

To interact with the back-end services, you can use tools like Postman or curl to make HTTP requests to the available API endpoints. Here’s a basic overview of the main routes:

### Authentication:

- `POST /auth/login`: Authenticate users and obtain a JWT token.
- `POST /auth/register`: Register a new user.

### Protected Endpoints (Require JWT token):

- `GET /api/protected-endpoint`: Example of an endpoint that requires authentication.

### CRUD Operations:

- `GET /api/resources`: Retrieve all records.
- `POST /api/resources`: Create a new record.
- `PUT /api/resources/{id}`: Update an existing record.
- `DELETE /api/resources/{id}`: Delete a record.

### Example API Call (with cURL):

`curl -X GET http://localhost:8080/api/resources -H "Authorization: Bearer your-jwt-token"`

## Technologies Used

- **Spring Boot**: Framework for building the core back-end structure.
- **Spring Security**: Provides authentication and authorization mechanisms (JWT-based and Google login integration).
- **Spring Data JPA**: For database interaction and ORM functionality.
- **MySQL**: Database management system
- **Maven**: For project build and dependency management.
- **JWT (JSON Web Token)**: For secure user sessions and API protection.
