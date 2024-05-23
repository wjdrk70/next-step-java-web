public class HttpRequestUtilsTest {
    @Test
    void testExtractPath() {
        String requestLine = "GET /index.html HTTP/1.1";
        String expectedPath = "/index.html";
        assertEquals(expectedPath, HttpRequestUtils.extractPath(requestLine));
    }

    @Test
    void testExtractPathWithNull() {
        assertNull(HttpRequestUtils.extractPath(null));
    }

    @Test
    void testExtractPathWithEmptyString() {
        assertNull(HttpRequestUtils.extractPath(""));
    }

    @Test
    void testExtractPathWithInvalidRequestLine() {
        String requestLine = "INVALID REQUEST LINE";
        assertNull(HttpRequestUtils.extractPath(requestLine));
    }
}
