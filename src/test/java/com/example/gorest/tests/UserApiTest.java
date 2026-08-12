package com.example.gorest.tests;

import com.example.gorest.client.UserService;
import com.example.gorest.data.TestDataFactory;
import com.example.gorest.model.User;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.example.gorest.support.ApiAssertions.*;
import static com.example.gorest.support.AuthAssumptions.requireToken;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class UserApiTest extends BaseApiTest {
    private static final String USER_SCHEMA = "schemas/user-schema.json";
    private static final String USERS_LIST_SCHEMA = "schemas/users-list-schema.json";
    private static final String VALIDATION_ERROR_SCHEMA = "schemas/validation-error-schema.json";
    private static final String ERROR_MESSAGE_SCHEMA = "schemas/error-message-schema.json";
    private static final int NON_EXISTING_USER_ID = 999999999;
    private final UserService users = new UserService();

    @Test
    @DisplayName("1. List users returns 200 and a JSON array")
    void listUsersReturnsUsers() {
        Response response = users.listUsers(1, 10);

        assertStatusCode(response, 200);
        assertMatchesJsonSchema(response, USERS_LIST_SCHEMA);
        response.then().body("size()", greaterThan(0));
    }

    @Test
    @DisplayName("2. List users supports pagination")
    void listUsersSupportsPagination() {
        Response response = users.listUsers(1, 5);

        assertStatusCode(response, 200);
        assertMatchesJsonSchema(response, USERS_LIST_SCHEMA);
        List<Integer> ids = response.jsonPath().getList("id");
        assertTrue(ids.size() <= 5, "Response should respect per_page=5");
    }

    @Test
    @DisplayName("3. Get existing user by id")
    void getExistingUserById() {
        Response listResponse = users.listUsers(1, 1);
        assertStatusCode(listResponse, 200);
        Integer userId = listResponse.jsonPath().getInt("[0].id");

        Response getResponse = users.getUser(userId);

        assertStatusCode(getResponse, 200);
        assertMatchesJsonSchema(getResponse, USER_SCHEMA);
        assertEquals(userId, getResponse.as(User.class).getId());
    }

    @Test
    @DisplayName("4. Create user successfully")
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
    @DisplayName("5. Create user with duplicate email returns validation error")
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
    @DisplayName("6. Create user without token returns unauthorized or forbidden")
    void createUserWithoutTokenIsRejected() {
        User payload = TestDataFactory.validUser();

        Response response = users.createUserWithoutToken(payload);

        assertStatusCodeIsOneOf(response, 401, 403);
        assertMatchesJsonSchema(response, ERROR_MESSAGE_SCHEMA);
    }

    @Test
    @DisplayName("7. Create user with invalid email returns validation error")
    void createUserWithInvalidEmailReturnsValidationError() {
        requireToken();
        User payload = TestDataFactory.invalidEmailUser();

        Response response = users.createUser(payload);

        assertStatusCode(response, 422);
        assertMatchesJsonSchema(response, VALIDATION_ERROR_SCHEMA);
        assertValidationContains(response, "email");
    }

    @Test
    @DisplayName("8. Full update user using PUT")
    void updateUserUsingPut() {
        requireToken();
        User createdPayload = TestDataFactory.validUser();
        User created = users.createUser(createdPayload).as(User.class);

        User updatePayload = TestDataFactory.validUser();
        Response response = users.updateUserPut(created.getId(), updatePayload);

        assertStatusCode(response, 200);
        assertMatchesJsonSchema(response, USER_SCHEMA);
        assertUserMatches(response.as(User.class), updatePayload);
        users.deleteUser(created.getId());
    }

    @Test
    @DisplayName("9. Partial update user using PATCH")
    void updateUserUsingPatch() {
        requireToken();
        User createdPayload = TestDataFactory.validUser();
        User created = users.createUser(createdPayload).as(User.class);

        Response response = users.updateUserPatch(created.getId(), TestDataFactory.partialStatusUpdate("inactive"));

        assertStatusCode(response, 200);
        assertMatchesJsonSchema(response, USER_SCHEMA);
        assertEquals("inactive", response.as(User.class).getStatus());
        users.deleteUser(created.getId());
    }

    @Test
    @DisplayName("10. Delete user successfully")
    void deleteUserSuccessfully() {
        requireToken();
        User created = users.createUser(TestDataFactory.validUser()).as(User.class);

        Response deleteResponse = users.deleteUser(created.getId());

        assertStatusCode(deleteResponse, 204);
    }

    @Test
    @DisplayName("11. Get deleted user returns 404")
    void getDeletedUserReturnsNotFound() {
        requireToken();
        User created = users.createUser(TestDataFactory.validUser()).as(User.class);
        assertStatusCode(users.deleteUser(created.getId()), 204);

        Response getResponse = users.getUser(created.getId());

        assertStatusCode(getResponse, 404);
        assertMatchesJsonSchema(getResponse, ERROR_MESSAGE_SCHEMA);
    }

    @Test
    @DisplayName("12. Get non-existing user returns 404")
    void getNonExistingUserReturnsNotFound() {
        Response response = users.getUser(NON_EXISTING_USER_ID);

        assertStatusCode(response, 404);
        assertMatchesJsonSchema(response, ERROR_MESSAGE_SCHEMA);
    }
}
