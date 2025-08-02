# AGENTS.md – Spring Boot Test Runner

## Overview

This repository contains a full-stack application with a Java Spring Boot backend located in the `/server` directory.

AI agents working in this workspace are expected to run unit and integration tests against the backend using the Maven wrapper (`./mvnw test`). However, since some agents (like Codex or GitHub Copilot Workspace) may not have internet access or persistent caches, this file provides detailed instructions and constraints to enable test execution.

---

## Directory Structure

```
/
├── client/             # Frontend
├── server/             # Spring Boot backend application
│   ├── pom.xml
│   ├── src/main/java/...
│   └── src/test/java/...
│   ├── .mvn/           # Maven wrapper folder (must be included)
│   ├── mvnw            # Maven wrapper shell script (must be executable)
│   ├── mvnw.cmd        # Maven wrapper for Windows (optional)
│   ├── Dockerfile      # For testing environment for agents
└── AGENTS.md           # This file
```

---

## Agent Objective

**Run the backend tests in `/server` using:**

```bash
cd server
./mvnw test
```

---

## Environment Setup for Agents

To ensure tests run successfully in offline or restricted environments, agents must follow the instructions below.

### ✅ Requirements

- Java 21+ (or compatible with your Spring Boot version)
- `./mvnw` and `.mvn/wrapper/*` must be present
- Valid `pom.xml` in `/server`
- All dependencies must be resolvable locally

### 🧰 Offline Dependency Preparation

If you're preparing the environment **before agent execution**, run the following:

```bash
cd server
./mvnw dependency:go-offline
```

This downloads all dependencies into the local Maven cache so agents can run `./mvnw test` without needing internet access.

If the agent supports using a prebuilt .m2 cache, it may unpack one prepared by the development team (e.g., via tar -xzf m2-cache.tar.gz -C ~/) before running tests.
If `.m2` is not present, agents must rely on the Maven wrapper and a pre-prepared environment (e.g., Docker image or unpacked cache).

---

## Agent Behavior (Reference Implementation)

Agents can use the following behavior logic when testing:

### TestRunnerAgent

```yaml
agent:
  name: TestRunnerAgent
  description: Executes backend unit and integration tests
  working_directory: /server
  steps:
    - description: Ensure working directory is correct
      command: cd /server
    - description: Verify Maven wrapper is present
      command: test -x ./mvnw
    - description: Verify pom.xml exists
      command: test -f pom.xml
    - description: Attempt to run tests
      command: ./mvnw -q test
      error_handling:
        if_failure: |
          If dependency resolution fails, first try:
            ./mvnw dependency:go-offline
          Then retry:
            ./mvnw test
```

---

## Troubleshooting for Agents

| Symptom                             | Possible Fix                                               |
| ----------------------------------- | ---------------------------------------------------------- |
| `failed to fetch dependencies`      | Run `./mvnw dependency:go-offline` before testing          |
| `mvnw: command not found`           | Ensure you're in `/server` and `mvnw` has execute rights   |
| `permission denied: ./mvnw`         | Run `chmod +x mvnw` in `/server`                           |
| `Could not find or load main class` | Make sure dependencies are resolved                        |
| `Build fails with plugin errors`    | Plugins also need to be cached via `dependency:go-offline` |

---

## Example Workflow

### 1. Prepare the environment (if setting up manually)

```bash
cd server
./mvnw dependency:go-offline
```

---

### 2. Running Tests in Docker (Backend Only)

To run backend tests in an offline-compatible container (recommended when Maven wrapper fails):

```bash
docker build -t backend-tester -f server/Dockerfile .
docker run --rm backend-tester
```

This uses a system-installed version of Maven (not `./mvnw`) and is suitable for agents in network-restricted environments.
Ensure the Dockerfile contains an offline-ready `mvn dependency:go-offline` instruction during build time.

---

## ✅ Summary for Agents

To execute backend tests reliably:

- Prefer running `./mvnw dependency:go-offline` before `./mvnw test`
- If Maven wrapper fails due to missing distribution or network issues, fall back to:
  ```bash
  docker build -t backend-tester -f server/Dockerfile .
  docker run --rm backend-tester
  ```
- Agents should not attempt to download new dependencies during test execution
- Maven dependencies and plugins must be available beforehand
