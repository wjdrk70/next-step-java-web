package webserver;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private static final String WEB_ROOT = "webapp"; // 웹 애플리케이션의 루트 디렉토리
    private static final String DEFAULT_FILE = "index.html"; // 기본 파일

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String requestLine = reader.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }

            String[] parts = requestLine.split(" ");
            String method = parts[0];
            String filePath = parts[1];

            log.debug("Request: method={}, filePath={}", method, filePath);

            if (method.equals("GET")) {
                if (filePath.startsWith("/user/create")) {
                    // 쿼리 파라미터가 있는지 확인하고, 파라미터를 추출합니다.
                    String queryString = filePath.contains("?") ? filePath.split("\\?", 2)[1] : "";
                    User user = RequestParser.parseUserFromQuery(queryString);

                    // 유저 정보를 처리하는 로직 추가 (예: 데이터베이스에 저장)

                    // 응답 생성
                    DataOutputStream dos = new DataOutputStream(out);
                    byte[] body = ("User created: " + user.toString()).getBytes();
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                } else {
                    if ("/".equals(filePath)) {
                        filePath = "/" + DEFAULT_FILE;
                    }

                    File file = new File(WEB_ROOT, filePath);
                    if (file.exists() && !file.isDirectory()) {
                        byte[] body = Files.readAllBytes(file.toPath());
                        DataOutputStream dos = new DataOutputStream(out);
                        response200Header(dos, body.length);
                        responseBody(dos, body);
                    } else {
                        DataOutputStream dos = new DataOutputStream(out);
                        byte[] body = "404 Not Found".getBytes();
                        response404Header(dos, body.length);
                        responseBody(dos, body);
                    }
                }
            } else {
                // 다른 메서드에 대한 응답 처리 (필요 시)
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response404Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 404 Not Found\r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}