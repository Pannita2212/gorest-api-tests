package com.example.gorest.tests;

import com.example.gorest.client.UserService;
import com.example.gorest.data.TestDataFactory;
import com.example.gorest.model.User;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.example.gorest.support.ApiAssertions.assertMatchesJsonSchema;
import static com.example.gorest.support.ApiAssertions.assertStatusCode;
import static com.example.gorest.support.ApiAssertions.assertUserMatches;
import static com.example.gorest.support.AuthAssumptions.requireToken;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateUserApiTest extends BaseApiTest {
    private final UserService users = new UserService();

    @Test
    @DisplayName("PUT /users/{id} - Full update user")
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
    @DisplayName("PATCH /users/{id} - Partial update user")
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
}
