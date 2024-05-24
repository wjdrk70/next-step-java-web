package webserver;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.Map;
import model.User;
import util.HttpRequestUtils;
import util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final String BASE_ROOT = "webapp";
    private static final String DEFAULT_FILE = "index.html";

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("connection : {}", connection);
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            String requestLine = bufferedReader.readLine();
            log.debug("Request Line= {}", requestLine);

            String path = HttpRequestUtils.extractPath(requestLine);
            String method = HttpRequestUtils.extractMethod(requestLine);
            log.debug("Path= {}", path);
            log.debug("Method= {}", method);

            DataOutputStream dataOutputStream = new DataOutputStream(out);

            if (path == null || path.equals("/")) {
                path = "/" + DEFAULT_FILE;
            }

            if (method.equals("POST")) {
                log.debug("method : {}", method);
                if (path.equals("/user/create")) {
                    handleUserCreateRequest(bufferedReader, dataOutputStream);
                } else {
                    handleFileRequest(path, dataOutputStream);
                }
            } else if (method.equals("GET")) {
                handleFileRequest(path, dataOutputStream);
            } else {
                // Other methods can be handled here
                response404Header(dataOutputStream);
                responseBody(dataOutputStream, "404 Not Found".getBytes());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void handleUserCreateRequest(BufferedReader bufferedReader, DataOutputStream dos) {
        try {
            String line;
            int contentLength = 0;

            // 헤더 읽기
            while (!(line = bufferedReader.readLine()).equals("")) {
                if (line.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(line.split(":")[1].trim());
                }
            }

            // 본문 읽기
            String body = IOUtils.readData(bufferedReader, contentLength);
            log.debug("body user : {}",body);
            Map<String, String> parameters = HttpRequestUtils.parseQueryString(URLDecoder.decode(body, "UTF-8"));
            User user = new User(parameters.get("userId"), parameters.get("password"), parameters.get("name"), parameters.get("email"));
            log.debug("User Created: {}", user);

            String responseBody = "User Created Successfully";
            response200Header(dos, responseBody.length());
            responseBody(dos, responseBody.getBytes());
        } catch (Exception e) {
            log.error("Error in handleUserCreateRequest", e);
            response500Header(dos);
            responseBody(dos, "500 Internal Server Error".getBytes());
        }
    }

    private void handleFileRequest(String path, DataOutputStream dos) throws IOException {
        File file = new File(BASE_ROOT, path);
        log.debug("Requested file: {}", file.getAbsolutePath());
        if (file.exists() && !file.isDirectory()) {
            byte[] body = Files.readAllBytes(file.toPath());
            String contentType = Files.probeContentType(file.toPath());
            response200Header(dos, body.length);
            responseBody(dos, body);
        } else {
            response404Header(dos);
            responseBody(dos, "404 Not Found".getBytes());
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response404Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 404 Not Found \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response500Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 500 Internal Server Error \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}