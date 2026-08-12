# GoREST API Automation Framework

API test automation project for the GoREST public API using **Java 17+**, **REST Assured**, **JUnit 5**, and **Maven**.

This README is written for new joiners who want to understand the framework structure, run the tests, and implement new API scenarios correctly.

---

## 1. Project Overview

This project validates the GoREST API:

```text
https://gorest.co.in/public/v2
```

The framework demonstrates common API automation practices:

- Reusable API client layer
- Service layer pattern
- Request and response model handling
- Dynamic test data generation
- Centralized assertion helpers
- Failure logging
- Environment configuration with `.env`
- CRUD test coverage
- Negative and edge case validation

The current implementation focuses on the **Users API** and includes more than 10 scenarios covering create, read, update, delete, error handling, and edge cases.

---

## Available Endpoints

The current framework is implemented around the GoREST **Users API** endpoints below. New joiners should start from these endpoints when learning or extending the project.

| Method | Endpoint | Description | Implemented In |
| --- | --- | --- | --- |
| GET | `/users` | List users | `UserService.listUsers(page, perPage)` |
| GET | `/users/{id}` | Get user details by user ID | `UserService.getUser(userId)` |
| POST | `/users` | Create a new user | `UserService.createUser(user)` |
| PUT | `/users/{id}` | Fully update an existing user | `UserService.updateUserPut(userId, user)` |
| PATCH | `/users/{id}` | Partially update an existing user | `UserService.updateUserPatch(userId, user)` |
| DELETE | `/users/{id}` | Delete an existing user | `UserService.deleteUser(userId)` |

Example endpoint flow used by the tests:

```text
1. GET /users
   - Verify the API can return a list of users.

2. GET /users/{id}
   - Select an existing user ID from the list response.
   - Verify the user detail response.

3. POST /users
   - Create a user with dynamic test data.
   - Verify the response returns HTTP 201 and the created user data.

4. PUT /users/{id}
   - Create a user first.
   - Fully update the user data.
   - Verify the updated response.

5. PATCH /users/{id}
   - Create a user first.
   - Partially update user status.
   - Verify only the intended field is changed.

6. DELETE /users/{id}
   - Create a user first.
   - Delete the user.
   - Verify the API returns HTTP 204.
   - Verify GET /users/{id} returns HTTP 404 after deletion.
```

---

## 2. Technology Stack

| Tool | Purpose |
| --- | --- |
| Java 17+ | Programming language |
| Maven | Build and dependency management |
| REST Assured | API request and response testing |
| JUnit 5 | Test framework |
| Java Faker | Dynamic test data generation |
| dotenv-java | Reads values from `.env` file |
| SLF4J Simple | Logging |

---

## 3. Prerequisites

Before running the project, make sure you have:

- Java 17 or higher
- Maven installed
- GoREST API token for write operations

Check your local versions:

```bash
java -version
mvn -version
```

The tests can run read-only scenarios without a token, but POST, PUT, PATCH, and DELETE tests require a valid GoREST token.

---

## 4. Environment Setup

Create a `.env` file at the project root.

```env
GOREST_API_TOKEN=your_token_here
```

Optional base URL override:

```env
BASE_URL=https://gorest.co.in/public/v2
```

Example project root:

```text
gorest-api-tests-java
├── .env
├── .env.example
├── pom.xml
├── README.md
└── src
```

> Important: `.env` is ignored by Git because it may contain sensitive credentials. Do not commit your real token.

---

## 5. How to Run Tests

### Run all tests

```bash
mvn clean test
```

### Run one test class

```bash
mvn test -Dtest=ListUsersApiTest
```

### Run one specific test method

```bash
mvn test -Dtest=ListUsersApiTest#createUserSuccessfully
```

### Pass token directly without `.env`

```bash
mvn clean test -DgorestApiToken="your-token-here"
```

### Pass base URL directly

```bash
mvn clean test -DbaseUrl="https://gorest.co.in/public/v2"
```

---

## 6. Test Reports

After running tests, Maven Surefire reports are generated here:

```text
target/surefire-reports
```

You can list the report files with:

```bash
ls target/surefire-reports
```

---

## 7. Project Structure

```text
src/test
├── java/com/example/gorest
│   ├── client
│   │   ├── ApiClient.java
│   │   └── UserService.java
│   ├── config
│   │   └── TestConfig.java
│   ├── data
│   │   └── TestDataFactory.java
│   ├── extensions
│   │   └── TestFailureLogger.java
│   ├── model
│   │   ├── ErrorResponse.java
│   │   └── User.java
│   ├── support
│   │   ├── ApiAssertions.java
│   │   └── AuthAssumptions.java
│   └── tests
│       ├── BaseApiTest.java
│       ├── CreateUserApiTest.java
│       ├── DeleteUserApiTest.java
│       ├── GetUserApiTest.java
│       ├── ListUsersApiTest.java
│       └── UpdateUserApiTest.java
└── resources
    ├── schemas
    │   ├── error-message-schema.json
    │   ├── user-schema.json
    │   ├── users-list-schema.json
    │   └── validation-error-schema.json
    └── simplelogger.properties
```

---

## 8. Package Responsibilities

| Package | Responsibility |
| --- | --- |
| `client` | Contains reusable API client and endpoint service classes |
| `config` | Loads base URL and token from system properties, environment variables, or `.env` |
| `data` | Generates dynamic test data |
| `extensions` | Adds JUnit test logging behavior |
| `model` | Contains request and response POJOs |
| `support` | Contains assertion helpers and test assumptions |
| `tests` | Contains actual API test scenarios |

---

## Test File Separation by Endpoint

Tests are separated by endpoint responsibility so new joiners can quickly find the relevant scenarios and add new coverage without editing one large test file.

Current test files:

```text
src/test/java/com/example/gorest/tests
├── BaseApiTest.java
├── ListUsersApiTest.java      # GET /users
├── GetUserApiTest.java        # GET /users/{id}
├── CreateUserApiTest.java     # POST /users
├── UpdateUserApiTest.java     # PUT /users/{id}, PATCH /users/{id}
└── DeleteUserApiTest.java     # DELETE /users/{id}
```

Guideline:

- Add list/search scenarios into `ListUsersApiTest.java`.
- Add get-detail and not-found scenarios into `GetUserApiTest.java`.
- Add create and create-validation scenarios into `CreateUserApiTest.java`.
- Add full or partial update scenarios into `UpdateUserApiTest.java`.
- Add delete and post-delete verification scenarios into `DeleteUserApiTest.java`.
- Keep shared constants, schema paths, and common setup in `BaseApiTest.java`.

---

## 9. Framework Design

### 9.1 API Client Layer

`ApiClient.java` centralizes request configuration.

Responsibilities:

- Set base URI
- Set JSON content type
- Set JSON accept header
- Add authorization header when required
- Add request/response/error logging filters

Example usage:

```java
apiClient.request();
apiClient.authorizedRequest();
```

Use `request()` for public endpoints and `authorizedRequest()` for endpoints requiring token authentication.

---

### 9.2 Service Layer

`UserService.java` encapsulates endpoint implementation.

Instead of writing REST Assured request code directly inside every test, tests call service methods such as:

```java
users.listUsers(1, 10);
users.getUser(userId);
users.createUser(payload);
users.updateUserPatch(userId, payload);
users.deleteUser(userId);
```

Benefits:

- Test cases are easier to read
- Endpoint changes are maintained in one place
- Request logic is reusable
- Code duplication is reduced

---

### 9.3 Models

Models represent API request and response payloads.

Current models:

- `User.java`
- `ErrorResponse.java`

Example:

```java
User user = new User(
    "John Doe",
    "john.doe@example.com",
    "male",
    "active"
);
```

Benefits:

- Avoids raw JSON strings in tests
- Improves readability
- Supports type-safe request and response handling
- Makes refactoring easier

---

### 9.4 Test Data Factory

`TestDataFactory.java` generates dynamic test data for each run.

Example:

```java
User user = TestDataFactory.validUser();
```

Generated data includes:

- Random name
- Unique email
- Random gender
- Random status

This helps avoid duplicate data problems when tests are run repeatedly.

---

### 9.5 Assertion Helpers

`ApiAssertions.java` provides reusable assertion methods.

Example:

```java
assertStatusCode(response, 200);
assertStatusCodeIsOneOf(response, 401, 403);
assertUserMatches(actualUser, expectedUser);
assertValidationContains(response, "email");
```

Benefits:

- Consistent validation style
- Better failure messages
- Less duplicated assertion code
- Easier maintenance

---

### 9.6 Authentication Handling

Token loading is handled by `TestConfig.java`.

The framework checks token values in this order:

1. JVM property: `-DgorestApiToken=...`
2. JVM property: `-DgorestToken=...`
3. Environment variable: `GOREST_API_TOKEN`
4. `.env` value: `GOREST_API_TOKEN`
5. Environment variable: `GOREST_TOKEN`
6. `.env` value: `GOREST_TOKEN`

Recommended variable:

```env
GOREST_API_TOKEN=your_token_here
```

If no token is provided, write-operation tests are skipped using JUnit assumptions.

---

### 9.7 Logging Strategy

The project includes useful logging for debugging failures.

Logging is implemented through:

- REST Assured request logging filter
- REST Assured response logging filter
- REST Assured error logging filter
- JUnit `TestFailureLogger` extension
- SLF4J Simple logger configuration

When a test fails, the framework logs useful details such as:

- Test name
- Exception
- Stack trace
- Request details
- Response details

---

### 9.8 JSON Schema Validation

The framework validates response body structure using JSON schema files under:

```text
src/test/resources/schemas
```

Current schema files:

```text
error-message-schema.json
user-schema.json
users-list-schema.json
validation-error-schema.json
```

Schema validation is centralized in `ApiAssertions.java`:

```java
public static void assertMatchesJsonSchema(Response response, String schemaPath) {
    response.then().body(matchesJsonSchemaInClasspath(schemaPath));
}
```

Example test usage:

```java
assertStatusCode(response, 200);
assertMatchesJsonSchema(response, USER_SCHEMA);
```

This verifies that the API response structure is correct, including required fields, data types, enum values, and error response shapes.

---

## 10. Current Test Coverage

The project currently includes these scenarios:

1. List users returns 200 and a JSON array
2. List users supports pagination
3. Get existing user by ID
4. Create user successfully
5. Create user with duplicate email returns validation error
6. Create user without token returns unauthorized or forbidden
7. Create user with invalid email returns validation error
8. Full update user using PUT
9. Partial update user using PATCH
10. Delete user successfully
11. Get deleted user returns 404
12. Get non-existing user returns 404

Coverage areas:

- CRUD operations
- Positive scenarios
- Negative scenarios
- Authentication error handling
- Validation error handling
- JSON schema validation
- Edge cases

---

## 11. How to Add a New API Test

Use the following implementation flow when adding new tests.

### Step 1: Create or update a service class

If testing a new endpoint, create a new service class under:

```text
src/test/java/com/example/gorest/client
```

Example for a Posts API:

```java
public class PostService {
    private final ApiClient apiClient = new ApiClient();

    public Response listPosts() {
        return apiClient.request()
                .get("/posts");
    }
}
```

If the endpoint belongs to an existing service, add a new method to that service.

---

### Step 2: Create a model if needed

Create request/response models under:

```text
src/test/java/com/example/gorest/model
```

Example:

```java
public class Post {
    private Integer id;
    private Integer userId;
    private String title;
    private String body;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
```

Create models when the API has structured request or response payloads.

---

### Step 3: Add test data generation if needed

Add reusable test data methods in:

```text
src/test/java/com/example/gorest/data/TestDataFactory.java
```

Example:

```java
public static Post validPost(Integer userId) {
    Post post = new Post();
    post.setUserId(userId);
    post.setTitle(FAKER.book().title());
    post.setBody(FAKER.lorem().paragraph());
    return post;
}
```

This keeps test data creation consistent and reusable.

---

### Step 4: Add assertions if reusable

If a validation can be reused across multiple tests, add it to:

```text
src/test/java/com/example/gorest/support/ApiAssertions.java
```

Example:

```java
public static void assertPostCreated(Post actual, Post expected) {
    assertNotNull(actual.getId(), "Post id should not be null");
    assertEquals(expected.getTitle(), actual.getTitle());
    assertEquals(expected.getBody(), actual.getBody());
}
```

---

### Step 5: Write the test

Create a test class under:

```text
src/test/java/com/example/gorest/tests
```

Example:

```java
class PostApiTest extends BaseApiTest {
    private final PostService posts = new PostService();

    @Test
    @DisplayName("List posts returns 200")
    void listPostsReturns200() {
        Response response = posts.listPosts();

        assertStatusCode(response, 200);
    }
}
```

---

## 12. Best Practices for New Joiners

### Do

- Use the service layer for all API calls
- Use models instead of raw JSON when possible
- Use `TestDataFactory` for dynamic test data
- Use `ApiAssertions` for common validations
- Keep test cases independent
- Clean up data created during tests
- Use clear `@DisplayName` values
- Keep test methods focused on one scenario
- Add negative tests for validation and authentication failures

### Do not

- Do not hardcode real tokens in code
- Do not commit `.env`
- Do not hardcode emails that may already exist
- Do not duplicate REST Assured request blocks inside tests
- Do not depend on test execution order
- Do not use fixed IDs unless the API guarantees them
- Do not put endpoint implementation details inside test classes

---

## 13. Troubleshooting

### `zsh: command not found: mvn`

Maven is not installed or not added to your PATH.

Check:

```bash
mvn -version
```

Install Maven with Homebrew:

```bash
brew install maven
```

---

### `Unable to locate a Java Runtime`

Java is not installed or not added to your PATH.

Check:

```bash
java -version
```

Install OpenJDK with Homebrew:

```bash
brew install openjdk
```

Then follow the Homebrew output to add Java to your shell profile.

---

### Write-operation tests are skipped

This means the token is missing.

Check your `.env` file:

```env
GOREST_API_TOKEN=your_real_token_here
```

Then run again:

```bash
mvn clean test
```

---

### 401 or 403 response for create/update/delete tests

Possible causes:

- Token is missing
- Token is invalid
- Token has expired or was revoked
- `.env` file is not located at the project root

---

### 422 response when creating a user

This usually means the API rejected the request due to validation rules.

Common reasons:

- Email already exists
- Email format is invalid
- Required field is missing
- Gender or status value is not accepted

---

## 14. Implementation Checklist

When implementing a new API endpoint, follow this checklist:

- [ ] Add or update service method
- [ ] Add request/response model if needed
- [ ] Add dynamic test data if needed
- [ ] Add reusable assertion helper if useful
- [ ] Add or update JSON schema if response structure is new
- [ ] Add positive test scenarios
- [ ] Add negative test scenarios
- [ ] Add edge case scenarios
- [ ] Add cleanup logic for created data
- [ ] Run `mvn clean test`
- [ ] Review `target/surefire-reports`

---

## 15. Summary for New Joiners

To add a new API scenario, you normally need only four things:

1. A service method for the endpoint
2. A model class if the request or response has structured data
3. A test data factory method if dynamic payloads are required
4. A test method that calls the service and validates the response

Keep the tests simple, readable, independent, and reusable. The goal of this framework is to make API test implementation fast, consistent, and maintainable.
