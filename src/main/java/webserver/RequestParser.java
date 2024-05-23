package webserver;

import model.User;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RequestParser {
    public static User parseUserFromQuery(String query) {
        Map<String, String> queryParams = parseQueryString(query);

        if (!queryParams.containsKey("userId") ||
                !queryParams.containsKey("password") ||
                !queryParams.containsKey("name") ||
                !queryParams.containsKey("email")) {
            throw new IllegalArgumentException("Missing required query parameters");
        }

        return new User(
                queryParams.get("userId"),
                queryParams.get("password"),
                queryParams.get("name"),
                queryParams.get("email")
        );
    }

    private static Map<String, String> parseQueryString(String query) {
        Map<String, String> queryPairs = new HashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                try {
                    String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8.toString());
                    String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8.toString());
                    queryPairs.put(key, value);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace(); // 에러 로그 출력
                }
            }
        }
        return queryPairs;
    }
}