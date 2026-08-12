# New Joiner Setup Guide

This guide helps a new joiner clone the repository, prepare the local environment, and run the API tests successfully.

## Quick Start

From the project root:

```bash
chmod +x setup.sh
./setup.sh
mvn clean test
```

The setup script checks Java, checks Maven, creates a local `.env` file, and validates the Maven project configuration.

## Required Tools

You need:

- Java 17 or higher
- Maven
- Git

Check versions:

```bash
java -version
mvn -version
git --version
```

## Environment File

The project uses `.env` for local secrets.

The repository contains:

```text
.env.example
```

The setup script creates:

```text
.env
```

Default `.env` content:

```env
GOREST_API_TOKEN=
# BASE_URL=https://gorest.co.in/public/v2
```

If `GOREST_API_TOKEN` is empty, read-only tests will run and write-operation tests will be skipped.

To run the full CRUD suite, update `.env`:

```env
GOREST_API_TOKEN=your_real_token_here
```

Do not commit `.env` to Git.

## Run Tests

Run all tests:

```bash
mvn clean test
```

Run one endpoint test file:

```bash
mvn test -Dtest=ListUsersApiTest
```

Run one test method:

```bash
mvn test -Dtest=CreateUserApiTest#createUserSuccessfully
```

## Expected Behavior

### Without token

This command should still complete successfully:

```bash
mvn clean test
```

Read-only tests will run. Write-operation tests will be skipped.

### With token

When `.env` has a valid token, the full CRUD suite should run:

```env
GOREST_API_TOKEN=your_real_token_here
```

Then:

```bash
mvn clean test
```

## Troubleshooting

### `zsh: command not found: mvn`

Install Maven:

```bash
brew install maven
```

Then verify:

```bash
mvn -version
```

### `Unable to locate a Java Runtime`

Install Java:

```bash
brew install openjdk
```

Then follow the Homebrew output to add Java to your shell profile.

Verify:

```bash
java -version
```

### Write tests are skipped

This means `GOREST_API_TOKEN` is empty.

Update `.env`:

```env
GOREST_API_TOKEN=your_real_token_here
```

Then rerun:

```bash
mvn clean test
```

### Create, update, or delete tests return 401 or 403

Check that:

- `.env` exists in the project root
- `GOREST_API_TOKEN` is set
- The token is valid
- The token has not been revoked

## Recommended First Commands After Clone

```bash
git clone <repo-url>
cd <project-folder>
chmod +x setup.sh
./setup.sh
mvn clean test
```
