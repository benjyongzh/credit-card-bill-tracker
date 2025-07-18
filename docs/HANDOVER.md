# Credit Card Bill Tracker - Architecture & Handover

## 1. Project Overview
This project manages personal credit card finances. It contains:
- **Backend**: Spring Boot application with REST APIs for bank accounts, credit cards, expenses, bill payments and billing cycles.
- **Frontend**: React SPA built with Vite and TypeScript.
- **Database**: PostgreSQL persisted via JPA and Liquibase migrations.

Primary user role is an authenticated individual tracking expenses and payments across multiple cards and bank accounts.

## 2. High-Level Architecture
```mermaid
flowchart TD
  subgraph Frontend
    A[React App]
  end
  subgraph Backend
    B[Spring Boot API]
  end
  subgraph Database
    C[(PostgreSQL)]
  end
  subgraph External
    D[Google OAuth2]
  end
  A <--->|HTTP| B
  B <-->|JPA| C
  B <--->|OAuth2 Login| D
```

## 3. Component Breakdown
### Frontend
- React + Vite SPA with routes defined in `App.tsx`.
- State managed with Zustand (`src/store/auth.ts`).
- API wrapper uses Axios (`src/lib/api.ts`).

### Backend
- Spring Boot 3, Java 21 and JPA. Main entry in `BackendApplication.java`.
- Modules: bankaccount, creditcard, expense, billpayment, billingcycle, expensesummary, spendingprofile, user, auth.
- Controllers expose REST endpoints under `/api/*` paths.
- Security uses JWT with cookies and Google OAuth2 for login.

### Database
- PostgreSQL with entities such as User, BankAccount, CreditCard, Expense, BillPayment, BillingCycle etc.
- Schema managed by Liquibase (`db.changelog-master.xml`).

```mermaid
erDiagram
    users {
        UUID id PK
        string username UNIQUE
        string email UNIQUE
        string password_hash
        string role
        datetime created_at
        datetime updated_at
        datetime deleted_at
    }
    bank_accounts {
        UUID id PK
        UUID user_id FK
        string name
        boolean is_default
        UUID default_card_id FK
        datetime created_at
        datetime updated_at
        datetime deleted_at
    }
    credit_cards {
        UUID id PK
        UUID user_id FK
        string card_name
        string last_four_digits
        datetime created_at
        datetime updated_at
        datetime deleted_at
    }
    expenses {
        UUID id PK
        UUID user_id FK
        UUID credit_card_id FK
        date date
        double amount
        string description
        datetime created_at
        datetime updated_at
        datetime deleted_at
    }
    bill_payments {
        UUID id PK
        UUID user_id FK
        UUID from_account_id FK
        UUID to_card_id FK
        UUID to_account_id FK
        double amount
        date date
        boolean completed
        datetime created_at
        datetime updated_at
        datetime deleted_at
    }
    billing_cycles {
        UUID id PK
        UUID user_id FK
        string label
        date completed_date
        datetime created_at
        datetime updated_at
        datetime deleted_at
    }
    expense_summaries {
        UUID id PK
        UUID user_id FK
        UUID from_account_id FK
        UUID to_id
        string to_type
        double total_expense
        double total_paid
        datetime created_at
        datetime updated_at
        datetime deleted_at
    }
    spending_profiles {
        UUID id PK
        UUID user_id FK
        string name
        datetime created_at
        datetime updated_at
        datetime deleted_at
    }
    users ||--o{ bank_accounts : owns
    users ||--o{ credit_cards : owns
    users ||--o{ expenses : logs
    users ||--o{ bill_payments : schedules
    users ||--o{ billing_cycles : groups
    users ||--o{ expense_summaries : summarizes
    users ||--o{ spending_profiles : manages
    bank_accounts }o--o{ expenses : "paid by"
    credit_cards ||--o{ expenses : charges
    bank_accounts ||--o{ bill_payments : sends
    credit_cards ||--o{ bill_payments : receives
    billing_cycles ||--o{ bill_payments : contains
    bank_accounts }o--o{ spending_profiles : "belongs to"
```

#### Data Dictionary

##### users
| Column | Type | Notes |
| --- | --- | --- |
| id | UUID | primary key |
| username | string | unique |
| email | string | unique |
| password_hash | string | hashed password |
| role | string | user role |
| created_at | datetime | record creation |
| updated_at | datetime | last update |
| deleted_at | datetime | soft delete timestamp |

##### bank_accounts
| Column | Type | Notes |
| --- | --- | --- |
| id | UUID | primary key |
| user_id | UUID | FK to users |
| name | string | account label |
| is_default | boolean | marks default account |
| default_card_id | UUID | FK to credit_cards |
| created_at | datetime | record creation |
| updated_at | datetime | last update |
| deleted_at | datetime | soft delete timestamp |

##### credit_cards
| Column | Type | Notes |
| --- | --- | --- |
| id | UUID | primary key |
| user_id | UUID | FK to users |
| card_name | string | card label |
| last_four_digits | string | card ending |
| created_at | datetime | record creation |
| updated_at | datetime | last update |
| deleted_at | datetime | soft delete timestamp |

##### expenses
| Column | Type | Notes |
| --- | --- | --- |
| id | UUID | primary key |
| user_id | UUID | FK to users |
| credit_card_id | UUID | FK to credit_cards |
| date | date | transaction date |
| amount | double | amount spent |
| description | string | merchant or notes |
| created_at | datetime | record creation |
| updated_at | datetime | last update |
| deleted_at | datetime | soft delete timestamp |

##### bill_payments
| Column | Type | Notes |
| --- | --- | --- |
| id | UUID | primary key |
| user_id | UUID | FK to users |
| from_account_id | UUID | FK to bank_accounts |
| to_card_id | UUID | FK to credit_cards |
| to_account_id | UUID | FK to bank_accounts |
| amount | double | payment amount |
| date | date | payment date |
| completed | boolean | if payment executed |
| created_at | datetime | record creation |
| updated_at | datetime | last update |
| deleted_at | datetime | soft delete timestamp |

##### billing_cycles
| Column | Type | Notes |
| --- | --- | --- |
| id | UUID | primary key |
| user_id | UUID | FK to users |
| label | string | cycle name |
| completed_date | date | closing date |
| created_at | datetime | record creation |
| updated_at | datetime | last update |
| deleted_at | datetime | soft delete timestamp |

##### expense_summaries
| Column | Type | Notes |
| --- | --- | --- |
| id | UUID | primary key |
| user_id | UUID | FK to users |
| from_account_id | UUID | FK to bank_accounts |
| to_id | UUID | target entity id |
| to_type | string | target type |
| total_expense | double | aggregated spending |
| total_paid | double | aggregated payments |
| created_at | datetime | record creation |
| updated_at | datetime | last update |
| deleted_at | datetime | soft delete timestamp |

##### spending_profiles
| Column | Type | Notes |
| --- | --- | --- |
| id | UUID | primary key |
| user_id | UUID | FK to users |
| name | string | profile label |
| created_at | datetime | record creation |
| updated_at | datetime | last update |
| deleted_at | datetime | soft delete timestamp |

### Authentication & Authorization
```mermaid
sequenceDiagram
    participant User
    participant Frontend
    participant Backend
    participant Google
    alt Local login
        User->>Frontend: submit username & password
        Frontend->>Backend: POST /api/auth/login
        Backend->>Backend: validate credentials
        Backend-->>Frontend: set JWT cookie
        Frontend->>Backend: GET /api/bank-accounts
        Backend->>Backend: verify JWT
        Backend-->>Frontend: account data
    else Google SSO signup/login
        User->>Frontend: click "Sign in with Google"
        Frontend->>Google: OAuth request
        Google-->>Frontend: auth code
        Frontend->>Backend: /oauth2/callback
        Backend->>Google: fetch profile
        Backend->>Backend: create user if new
        Backend-->>Frontend: set JWT cookie
    end
    User->>Frontend: click Logout
    Frontend->>Backend: POST /api/auth/logout
    Backend-->>Frontend: clear cookie
```
- Security configuration registers JWT filter and OAuth2 handlers.

### Third-party Integrations
- **Google OAuth2** for optional login.
- **Liquibase** for database migrations.
- **Springdoc/Swagger** for API documentation.

## 4. Data Flow
```mermaid
sequenceDiagram
    participant User
    participant UI as React UI
    participant API as Spring API
    participant DB as PostgreSQL
    User->>UI: enter expense
    UI->>API: POST /api/expenses
    API->>DB: save Expense
    API->>UI: response
    UI->>User: show updated list
```

## 5. Deployment & Environment Setup
After cloning the repository you can bootstrap a local environment with:

```bash
bash server/scripts/setup-dev.sh
```

This links the Git hook and creates `/server/.env` for Liquibase. Next copy
`server/src/main/resources/application-template.properties` to
`server/src/main/resources/application-local.properties` and fill in your
database URL, credentials and OAuth settings. The backend reads this file when
`spring.profiles.active=local`.

CI is handled by GitHub Actions (`test.yml`) which builds the Maven and npm
projects and validates Liquibase changelogs. Runtime environments include local,
CI (`application-ci.properties`) and production with external config files.

## 6. Monitoring, Logging, & Alerts
- Spring Boot Actuator exposes health endpoint.
- Logging aspect logs all service calls.
- GitHub Actions artifacts provide build outputs.

## 7. Security & Compliance
- JWT stored in HTTP-only cookie; tokens generated and validated via `JwtUtil`.
- Login attempts rate-limited (`LoginAttemptService`).
- Passwords hashed using BCrypt.

## 8. Known Issues & Technical Debt
- Database schema is still empty; changelogs need to be generated as entities evolve.
- No front-end tests; coverage limited.

## 9. Future Enhancements
- Add more integration tests and front-end UI tests.
- Expand Liquibase changelogs and automate migrations.
- Containerize with Docker/Kubernetes for easier deployment.
