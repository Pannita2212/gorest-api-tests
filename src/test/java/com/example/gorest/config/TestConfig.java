package com.example.gorest.config;

import io.github.cdimascio.dotenv.Dotenv;

public final class TestConfig {
    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .ignoreIfMissing()
            .load();

    private TestConfig() {}

    public static String baseUrl() {
        return firstNonBlank(
                System.getProperty("baseUrl"),
                System.getenv("BASE_URL"),
                dotenv.get("BASE_URL"),
                "https://gorest.co.in/public/v2"
        );
    }

    public static String token() {
        return firstNonBlank(
                System.getProperty("gorestApiToken"),
                System.getProperty("gorestToken"),
                System.getenv("GOREST_API_TOKEN"),
                dotenv.get("GOREST_API_TOKEN"),
                System.getenv("GOREST_TOKEN"),
                dotenv.get("GOREST_TOKEN"),
                ""
        );
    }

    public static boolean hasToken() {
        return !token().isBlank();
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "";
    }
}
