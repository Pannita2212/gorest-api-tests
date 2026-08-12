package com.example.gorest.extensions;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestFailureLogger implements TestWatcher {
    private static final Logger log = LoggerFactory.getLogger(TestFailureLogger.class);

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        log.error("Test failed: {}", context.getDisplayName(), cause);
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        log.info("Test passed: {}", context.getDisplayName());
    }
}
