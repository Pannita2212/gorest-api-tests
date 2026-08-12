package com.example.gorest.data;

import com.example.gorest.model.User;
import com.github.javafaker.Faker;

import java.time.Instant;
import java.util.Locale;
import java.util.Random;

public final class TestDataFactory {
    private static final Faker FAKER = new Faker(Locale.ENGLISH);
    private static final Random RANDOM = new Random();

    private TestDataFactory() {}

    public static User validUser() {
        String unique = Instant.now().toEpochMilli() + "." + RANDOM.nextInt(100000);
        return new User(
                FAKER.name().fullName(),
                "api.test." + unique + "@example.com",
                RANDOM.nextBoolean() ? "male" : "female",
                RANDOM.nextBoolean() ? "active" : "inactive"
        );
    }

    public static User invalidEmailUser() {
        User user = validUser();
        user.setEmail("invalid-email-format");
        return user;
    }

    public static User invalidGenderUser() {
        User user = validUser();
        user.setGender("invalid-gender");
        return user;
    }

    public static User invalidStatusUser() {
        User user = validUser();
        user.setStatus("invalid-status");
        return user;
    }

    public static User invalidNameUser() {
        User user = validUser();
        user.setName("a".repeat(201)); // Assuming max length is 100
        return user;
    }

    public static User emptyNameUser() {
        User user = validUser();
        user.setName("");
        return user;
    }

    public static User nullStatusUser() {
        User user = validUser();
        user.setStatus(null);
        return user;
    }

    public static User partialStatusUpdate(String status) {
        User user = new User();
        user.setStatus(status);
        return user;
    }
}
