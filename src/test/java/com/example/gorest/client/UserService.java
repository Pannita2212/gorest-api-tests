package com.example.gorest.client;

import com.example.gorest.model.User;
import io.restassured.response.Response;

public class UserService {
    private final ApiClient apiClient = new ApiClient();

    public Response listUsers(int page, int perPage) {
        return apiClient.request()
                .queryParam("page", page)
                .queryParam("per_page", perPage)
                .get("/users");
    }

    public Response getUser(int userId) {
        return apiClient.request().get("/users/{id}", userId);
    }

    public Response createUser(User user) {
        return apiClient.authorizedRequest().body(user).post("/users");
    }

    public Response createUserWithoutToken(User user) {
        return apiClient.request().body(user).post("/users");
    }

    public Response updateUserPut(int userId, User user) {
        return apiClient.authorizedRequest().body(user).put("/users/{id}", userId);
    }

    public Response updateUserPatch(int userId, User user) {
        return apiClient.authorizedRequest().body(user).patch("/users/{id}", userId);
    }

    public Response deleteUser(int userId) {
        return apiClient.authorizedRequest().delete("/users/{id}", userId);
    }
}
