package util;

import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class HttpRequestUtilsTest {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestUtilsTest.class);
    private Socket mockSocket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private RequestHandler requestHandler;
    private String queryString;
    private Map<String, String> parameters;

    @BeforeEach
    public void setup() throws IOException {
        mockSocket = Mockito.mock(Socket.class);
        queryString = "userId=wjdrk70&password=test&name=ljj&email=wjdrk70%40gmail.com";
        inputStream = new ByteArrayInputStream(("GET /user/create?" + queryString + " HTTP/1.1\r\n").getBytes());
        outputStream = new ByteArrayOutputStream();

        when(mockSocket.getInputStream()).thenReturn(inputStream);
        when(mockSocket.getOutputStream()).thenReturn(outputStream);

        requestHandler = new RequestHandler(mockSocket);

        parameters = HttpRequestUtils.parseQueryString(URLDecoder.decode(queryString, "UTF-8"));
    }

    @Test
    public void testExtractPath() {
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
    public void testExtractPathWithNull() {
        // Given
        String requestLine = null;

        // When
        String actualPath = HttpRequestUtils.extractPath(requestLine);

        // Then
        assertNull(actualPath);
        log.debug("testExtractPathWithNull - requestLine: null, actualPath: {}", actualPath);
    }

    @Test
    public void testExtractPathWithEmptyString() {
        // Given
        String requestLine = "";

        // When
        String actualPath = HttpRequestUtils.extractPath(requestLine);

        // Then
        assertNull(actualPath);
        log.debug("testExtractPathWithEmptyString - requestLine: empty, actualPath: {}", actualPath);
    }

    @Test
    public void testParseQueryString() {
        // Given (설정은 이미 setup에서 수행됨)

        // When (parameters는 setup에서 이미 설정됨)

        // Then
        assertEquals("wjdrk70", parameters.get("userId"));
        assertEquals("test", parameters.get("password"));
        assertEquals("ljj", parameters.get("name"));
        assertEquals("wjdrk70@gmail.com", parameters.get("email"));

        log.debug("testParseQueryString - User : {}",parameters);
    }

    @Test
    public void testGetCreateUserSetup() {
        // When
        User user = new User(parameters.get("userId"), parameters.get("password"), parameters.get("name"), parameters.get("email"));

        // Then
        assertEquals("wjdrk70", user.getUserId());
        assertEquals("test", user.getPassword());
        assertEquals("ljj", user.getName());
        assertEquals("wjdrk70@gmail.com", user.getEmail());

        log.debug("testGetCreateUserSetup - User: {}", user);
    }
}