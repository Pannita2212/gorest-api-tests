package com.example.gorest.tests;

import com.example.gorest.client.UserService;
import com.example.gorest.model.User;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.example.gorest.support.ApiAssertions.assertMatchesJsonSchema;
import static com.example.gorest.support.ApiAssertions.assertStatusCode;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GetUserApiTest extends BaseApiTest {
    private final UserService users = new UserService();

    @Test
    @DisplayName("GET /users/{id} - Get existing user by id")
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
    @DisplayName("GET /users/{id} - Get non-existing user returns 404")
    void getNonExistingUserReturnsNotFound() {
        Response response = users.getUser(NON_EXISTING_USER_ID);

        assertStatusCode(response, 404);
        assertMatchesJsonSchema(response, ERROR_MESSAGE_SCHEMA);
    }
}
