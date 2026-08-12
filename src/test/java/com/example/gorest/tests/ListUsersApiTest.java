package com.example.gorest.tests;

import com.example.gorest.client.UserService;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.example.gorest.support.ApiAssertions.assertMatchesJsonSchema;
import static com.example.gorest.support.ApiAssertions.assertStatusCode;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListUsersApiTest extends BaseApiTest {
    private final UserService users = new UserService();

    @Test
    @DisplayName("GET /users - List users returns 200 and a JSON array")
    void listUsersReturnsUsers() {
        Response response = users.listUsers(1, 10);

        assertStatusCode(response, 200);
        assertMatchesJsonSchema(response, USERS_LIST_SCHEMA);
        response.then().body("size()", greaterThan(0));
    }

    @Test
    @DisplayName("GET /users - List users supports pagination")
    void listUsersSupportsPagination() {
        Response response = users.listUsers(1, 5);

        assertStatusCode(response, 200);
        assertMatchesJsonSchema(response, USERS_LIST_SCHEMA);
        List<Integer> ids = response.jsonPath().getList("id");
        assertTrue(ids.size() <= 5, "Response should respect per_page=5");
    }
}
