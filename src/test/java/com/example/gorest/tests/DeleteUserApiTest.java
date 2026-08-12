package com.example.gorest.tests;

import com.example.gorest.client.UserService;
import com.example.gorest.data.TestDataFactory;
import com.example.gorest.model.User;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.example.gorest.support.ApiAssertions.assertMatchesJsonSchema;
import static com.example.gorest.support.ApiAssertions.assertStatusCode;
import static com.example.gorest.support.AuthAssumptions.requireToken;

class DeleteUserApiTest extends BaseApiTest {
    private final UserService users = new UserService();

    @Test
    @DisplayName("DELETE /users/{id} - Delete user successfully")
    void deleteUserSuccessfully() {
        requireToken();
        User created = users.createUser(TestDataFactory.validUser()).as(User.class);

        Response deleteResponse = users.deleteUser(created.getId());

        assertStatusCode(deleteResponse, 204);
    }

    @Test
    @DisplayName("DELETE /users/{id} - Get deleted user returns 404")
    void getDeletedUserReturnsNotFound() {
        requireToken();
        User created = users.createUser(TestDataFactory.validUser()).as(User.class);
        assertStatusCode(users.deleteUser(created.getId()), 204);

        Response getResponse = users.getUser(created.getId());

        assertStatusCode(getResponse, 404);
        assertMatchesJsonSchema(getResponse, ERROR_MESSAGE_SCHEMA);
    }
}
