# Credit Card Bill Tracker

This repository hosts both the Spring Boot backend and the React frontend used to manage credit card finances. The backend exposes REST APIs secured with JWT and persists data in PostgreSQL, while the frontend is a Vite-powered single page application written in TypeScript.

---

## Backend (`/server`)

### 🔧 Features

- User registration and authentication (JWT + Google OAuth2)
- Management of bank accounts, credit cards, expenses, and billing cycles
- Bill payment tracking and automated bill summary suggestions
- Soft deletion with auditing timestamps
- Liquibase-managed database schema with versioned changelogs

---

### 🚀 Getting Started

#### Prerequisites

- **Java 21**
- **PostgreSQL**

#### 1. Clone and Setup Dev Environment

```bash
bash server/scripts/setup-dev.sh
```

This script will:

- Link the Git pre-commit hook
- Ensure it is executable
- Create a `.env` file in `/server` (if missing) with placeholders for DB credentials

> 📂 The `.env` file is used for Liquibase CLI scripts. It should not be committed.

#### 2. Configure Spring Boot

Configure your application-local.properties. You can first copy the contents from:

```bash
server/src/main/resources/application-template.properties
```

Edit the file to match your environment:

- `spring.datasource.*`: PostgreSQL URL, user, password
- `jwt.secret`: your JWT signing key
- `spring.security.oauth2.client.registration.google.*`: your Google client credentials
- `app.frontend-url`: the React frontend base URL

---

### 🧬 Liquibase (Database Migrations)

- Liquibase is used to manage schema changes via versioned changelog files.
- Changesets are committed to `server/src/main/resources/db/changelog/records/`.
- The master changelog is at `db.changelog-master.xml`.

#### Generating a Changelog

After modifying any `@Entity`, generate a changelog:

```bash
bash server/scripts/generate-liquibase-changelog.sh
```

This script will:

- Use your `.env` credentials to connect to the DB
- Compare your JPA model to the DB
- Generate a versioned changelog in `db/changelog/records/`
- Automatically append it to `db.changelog-master.xml`

#### Git Hook Automation

- A pre-commit Git hook detects when you change an entity and prompts:

  - ❓ "Run changelog generator? (y/n)"
  - ✅ If yes, it runs the script and re-stages the changelog
  - ❌ If no, it blocks the commit

---

### 🔧 Running the App Locally

From the `/server` directory:

```bash
./mvnw spring-boot:run
```

or if you already have Maven in your machine:

```bash
mvn spring-boot:run
```

The backend starts on `http://localhost:9000`.

---

### 🧪 Running Tests

```bash
./mvnw test
```

---

### 📦 Packaging for Deployment

```bash
./mvnw clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

Use the same property keys as in `application-template.properties` for production configs.

---

### 📁 Repository Structure

```
/server
├── setup-dev.sh             # One-time setup: links hook, creates .env
├── scripts/
│   └── hooks/pre-commit     # Detects @Entity changes and prompts changelog
├── generate-liquibase-changelog.sh    # Liquibase changelog generator
├── .env                     # Local-only: DB_URL, DB_USER, DB_PASS
├── src/main/resources/db/changelog/
│   ├── db.changelog-master.xml
│   └── records/             # Auto-generated changelogs
└── src/main/java/com/...    # Domain-driven modules
```

---

## Frontend (`/client`)

### 💻 Development

1. Copy and configure environment variables:

```bash
cp client/.env.example client/.env
```

2. Install and start:

```bash
cd client
npm install
npm run dev
```

Runs by default on `http://localhost:5173`.

---

### 🏗️ Production Build

```bash
npm run build
```

Output will be in `client/dist`.

---

## 🧠 Tips

- All backend schema changes **must go through Liquibase** (`generate-liquibase-changelog.sh`)
- **Never** edit already-applied changelogs
- Commit the generated changelog XML files and updated master changelog XML file
- Use `setup-dev.sh` after cloning to get up and running fast
