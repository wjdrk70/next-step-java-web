package webserver;

import model.User;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RequestParserTest {

    private static final Logger log = LoggerFactory.getLogger(RequestParserTest.class);

    @Test
    public void givenValidQuery_whenParseUserFromQuery_thenUserIsCreated() {
        // Given
        String query = "userId=devmonkey80&password=1234&name=ljj&email=devmonkey80%40gmail.com";

        // When
        User user = RequestParser.parseUserFromQuery(query);

        // Then
        assertEquals("devmonkey80", user.getUserId());
        assertEquals("1234", user.getPassword());
        assertEquals("ljj", user.getName());
        assertEquals("devmonkey80@gmail.com", user.getEmail());
        log.info("회원가입이 완료되었습니다: {}", user);
    }

    @Test
    public void givenInvalidQuery_whenParseUserFromQuery_thenThrowException() {
        // Given
        String query = "userId=devmonkey80&password=1234&name=ljj"; // 이메일 없음

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            RequestParser.parseUserFromQuery(query);
        });
        log.info("회원가입이 실패했습니다: {}", exception.getMessage());
    }
}