package com.example.gorest.tests;

import com.example.gorest.extensions.TestFailureLogger;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(TestFailureLogger.class)
public abstract class BaseApiTest {
    protected static final String USER_SCHEMA = "schemas/user-schema.json";
    protected static final String USERS_LIST_SCHEMA = "schemas/users-list-schema.json";
    protected static final String VALIDATION_ERROR_SCHEMA = "schemas/validation-error-schema.json";
    protected static final String ERROR_MESSAGE_SCHEMA = "schemas/error-message-schema.json";

    protected static final int NON_EXISTING_USER_ID = 999999999;
}
