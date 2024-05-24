package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String method;
    private String path;
    private Map<String, String> queryParams;

    public HttpRequest(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String requestLine = reader.readLine();
        parseRequestLine(requestLine);
    }

    private void parseRequestLine(String requestLine) throws IOException {
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("Invalid request line: " + requestLine);
        }

        String[] tokens = requestLine.split(" ");
        if (tokens.length < 3) {
            throw new IOException("Invalid request line: " + requestLine);
        }

        method = tokens[0];
        String fullPath = tokens[1];
        int queryIndex = fullPath.indexOf("?");
        if (queryIndex != -1) {
            path = fullPath.substring(0, queryIndex);
            queryParams = parseQueryString(fullPath.substring(queryIndex + 1));
        } else {
            path = fullPath;
            queryParams = new HashMap<>();
        }
    }

    private Map<String, String> parseQueryString(String queryString) throws IOException {
        Map<String, String> queryParams = new HashMap<>();
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                queryParams.put(URLDecoder.decode(keyValue[0], "UTF-8"), URLDecoder.decode(keyValue[1], "UTF-8"));
            }
        }
        return queryParams;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}
