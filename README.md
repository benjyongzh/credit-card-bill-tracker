# Credit Card Bill Tracker

This repository hosts both the Spring Boot backend and the React frontend used to manage credit card finances. The backend exposes REST APIs secured with JWT and persists data in PostgreSQL, while the frontend is a Vite-powered single page application written in TypeScript.

## Backend

### Features
- User registration and authentication
- Google login via OAuth2
- Management of bank accounts, credit cards, expenses, and billing cycles
- Bill payment tracking and spending summaries
- Soft deletion with auditing timestamps

### Getting Started
1. Install **Java 21** and ensure `java -version` reports it.
2. Copy `server/src/main/resources/application-template.properties` to `server/src/main/resources/application.properties` and edit the database credentials and JWT secret.
   You will also need to provide Google OAuth2 client details and set `app.frontend-url` to your React app base URL.
3. From the `server` directory run:
   ```bash
   ./mvnw spring-boot:run
   ```
   The server starts on `localhost:9000` by default.

### Running Tests
Execute the tests with:
```bash
./mvnw test
```

### Packaging and Deployment
Build an executable jar via:
```bash
./mvnw clean package
```
Run it with:
```bash
java -jar target/backend-0.0.1-SNAPSHOT.jar
```
Configure your production environment using the same property keys as in `application-template.properties`.

### Repository Layout
Source code lives under `server/` following a domain-driven structure (e.g., `auth`, `bankaccount`, `creditcard`). A zipped copy of the backend (`backend.zip`) is included for reference.

### Module Breakdown
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

## Frontend
The `client` directory contains the React SPA.

### Development
1. Copy `client/.env.example` to `client/.env` and adjust `VITE_API_BASE_URL` if the backend is hosted elsewhere.
2. Install dependencies:
   ```bash
   cd client
   npm install
   ```
3. Start the development server:
   ```bash
   npm run dev
   ```
   The app will run on `http://localhost:5173` by default.

### Building for production
Run the build command in the `client` directory:
```bash
npm run build
```
The static files will be generated under `client/dist`.
