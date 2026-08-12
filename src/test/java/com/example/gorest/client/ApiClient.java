package com.example.gorest.client;

import com.example.gorest.config.TestConfig;
import io.restassured.RestAssured;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class ApiClient {
    public RequestSpecification request() {
        return RestAssured
                .given()
                .baseUri(TestConfig.baseUrl())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .filter(new ErrorLoggingFilter())
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter());
    }

    public RequestSpecification authorizedRequest() {
        return request().header("Authorization", "Bearer " + TestConfig.token());
    }
}
