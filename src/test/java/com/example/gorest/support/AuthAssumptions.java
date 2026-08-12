package com.example.gorest.support;

import com.example.gorest.config.TestConfig;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

public final class AuthAssumptions {
    private AuthAssumptions() {}

    public static void requireToken() {
        assumeTrue(TestConfig.hasToken(), "Skipping write-operation test because GOREST_TOKEN is not configured.");
    }
}
