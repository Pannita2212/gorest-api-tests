package com.example.gorest.support;

import com.example.gorest.model.User;
import io.restassured.response.Response;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.*;

public final class ApiAssertions {
    private ApiAssertions() {}

    public static void assertStatusCode(Response response, int expectedStatusCode) {
        assertEquals(expectedStatusCode, response.statusCode(),
                () -> "Expected HTTP " + expectedStatusCode + " but got " + response.statusCode()
                        + "\nResponse body:\n" + response.asPrettyString());
    }

    public static void assertStatusCodeIsOneOf(Response response, int... expectedStatusCodes) {
        for (int code : expectedStatusCodes) {
            if (response.statusCode() == code) {
                return;
            }
        }
        fail("Expected one of " + java.util.Arrays.toString(expectedStatusCodes)
                + " but got " + response.statusCode()
                + "\nResponse body:\n" + response.asPrettyString());
    }

    public static void assertMatchesJsonSchema(Response response, String schemaPath) {
        response.then().body(matchesJsonSchemaInClasspath(schemaPath));
    }

    public static void assertUserMatches(User actual, User expected) {
        assertNotNull(actual.getId(), "Created user id should not be null");
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getGender(), actual.getGender());
        assertEquals(expected.getStatus(), actual.getStatus());
    }

    public static void assertValidationContains(Response response, String fieldName) {
        assertTrue(response.asString().contains(fieldName),
                () -> "Expected validation response to contain field: " + fieldName
                        + "\nResponse body:\n" + response.asPrettyString());
    }
}
