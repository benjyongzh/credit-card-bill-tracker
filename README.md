# Credit Card Bill Tracker Backend

This repository contains the Spring Boot backend for managing credit card bills and related finances. It provides REST APIs secured with JWT and persists data using PostgreSQL.

## Features
- User registration and authentication
- Management of bank accounts, credit cards, expenses, and billing cycles
- Bill payment tracking and spending summaries
- Soft deletion with auditing timestamps

## Getting Started
1. Install **Java 17** and ensure `java -version` reports it.
2. Copy `server/src/main/resources/application-template.properties` to `server/src/main/resources/application.properties` and edit the database credentials, JWT secret, and cookie settings.
   The JWT cookie defaults are:
   ```properties
   jwt.cookieName=token
   jwt.cookieSecure=false
   ```
3. From the `server` directory run:
   ```bash
   ./mvnw spring-boot:run
   ```
   The server starts on `localhost:8080` by default.

## Running Tests
Execute the tests with:
```bash
./mvnw test
```

## Packaging and Deployment
Build an executable jar via:
```bash
./mvnw clean package
```
Run it with:
```bash
java -jar target/backend-0.0.1-SNAPSHOT.jar
```
Configure your production environment using the same property keys as in `application-template.properties`.

## Repository Layout
Source code lives under `server/` following a domain-driven structure (e.g., `auth`, `bankaccount`, `creditcard`). A zipped copy of the backend (`backend.zip`) is included for reference.

## Module Breakdown

- **auth**: login endpoints, JWT handling, and security config
- **bankaccount**: CRUD operations for user bank accounts
- **billingcycle**: manages billing cycles and generates deferred bills
- **billpayment**: processes bill payments and provides optimization suggestions
- **creditcard**: tracks user credit cards and their limits
- **expense**: handles spending records and categorization
- **expensesummary**: aggregates expenses by account or card
- **spendingprofile**: stores spending goals and profiles
- **user**: user management and registration
- **common**: shared base entities, API responses, and soft deletion helpers
- **config**: JPA configuration and repository setup
