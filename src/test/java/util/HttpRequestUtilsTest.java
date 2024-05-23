package util;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class HttpRequestUtilsTest {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestUtilsTest.class);

    @Test
    void testExtractPath() {
        // Given
        String requestLine = "GET /index.html HTTP/1.1";

        // When
        String actualPath = HttpRequestUtils.extractPath(requestLine);

        // Then
        String expectedPath = "/index.html";
        assertEquals(expectedPath, actualPath);
        log.debug("testExtractPath - requestLine: {}, expectedPath: {}, actualPath: {}", requestLine, expectedPath, actualPath);
    }

    @Test
    void testExtractPathWithNull() {
        // Given
        String requestLine = null;

        // When
        String actualPath = HttpRequestUtils.extractPath(requestLine);

        // Then
        assertNull(actualPath);
        log.debug("testExtractPathWithNull - requestLine: null, actualPath: {}", actualPath);
    }

    @Test
    void testExtractPathWithEmptyString() {
        // Given
        String requestLine = "";

        // When
        String actualPath = HttpRequestUtils.extractPath(requestLine);

        // Then
        assertNull(actualPath);
        log.debug("testExtractPathWithEmptyString - requestLine: empty, actualPath: {}", actualPath);
    }


}