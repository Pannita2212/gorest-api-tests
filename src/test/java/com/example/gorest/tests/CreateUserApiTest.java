package com.example.gorest.tests;

import com.example.gorest.client.UserService;
import com.example.gorest.data.TestDataFactory;
import com.example.gorest.model.User;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.example.gorest.support.ApiAssertions.*;
import static com.example.gorest.support.AuthAssumptions.requireToken;

class CreateUserApiTest extends BaseApiTest {
    private final UserService users = new UserService();

    @Test
    @DisplayName("POST /users - Create user successfully")
    void createUserSuccessfully() {
        requireToken();
        User payload = TestDataFactory.validUser();

        Response response = users.createUser(payload);

        assertStatusCode(response, 201);
        assertMatchesJsonSchema(response, USER_SCHEMA);
        User created = response.as(User.class);
        assertUserMatches(created, payload);
        users.deleteUser(created.getId());
    }

    @Test
    @DisplayName("POST /users - Create user with duplicate email returns validation error")
    void createDuplicateEmailReturnsValidationError() {
        requireToken();
        User payload = TestDataFactory.validUser();
        Response firstCreate = users.createUser(payload);
        assertStatusCode(firstCreate, 201);
        int userId = firstCreate.as(User.class).getId();

        Response duplicateCreate = users.createUser(payload);

        assertStatusCode(duplicateCreate, 422);
        assertMatchesJsonSchema(duplicateCreate, VALIDATION_ERROR_SCHEMA);
        assertValidationContains(duplicateCreate, "email");
        users.deleteUser(userId);
    }

    @Test
    @DisplayName("POST /users - Create user without token returns unauthorized or forbidden")
    void createUserWithoutTokenIsRejected() {
        User payload = TestDataFactory.validUser();

        Response response = users.createUserWithoutToken(payload);

        assertStatusCodeIsOneOf(response, 401, 403);
        assertMatchesJsonSchema(response, ERROR_MESSAGE_SCHEMA);
    }

    @Test
    @DisplayName("POST /users - Create user with invalid email returns validation error")
    void createUserWithInvalidEmailReturnsValidationError() {
        requireToken();
        User payload = TestDataFactory.invalidEmailUser();

        Response response = users.createUser(payload);

        assertStatusCode(response, 422);
        assertMatchesJsonSchema(response, VALIDATION_ERROR_SCHEMA);
        assertValidationContains(response, "email");
    }
}
