package http;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class HttpResponse {
    private DataOutputStream dos;

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    public void sendResponse(int statusCode, String statusMessage, String contentType, byte[] body) throws IOException {
        dos.writeBytes("HTTP/1.1 " + statusCode + " " + statusMessage + "\r\n");
        dos.writeBytes("Content-Type: " + contentType + "\r\n");
        dos.writeBytes("Content-Length: " + body.length + "\r\n");
        dos.writeBytes("\r\n");
        dos.write(body, 0, body.length);
        dos.flush();
    }

    public void send200(String contentType, byte[] body) throws IOException {
        sendResponse(200, "OK", contentType, body);
    }

    public void send404() throws IOException {
        String body = "404 Not Found";
        sendResponse(404, "Not Found", "text/html;charset=utf-8", body.getBytes());
    }

    public void send500() throws IOException {
        String body = "500 Internal Server Error";
        sendResponse(500, "Internal Server Error", "text/html;charset=utf-8", body.getBytes());
    }
}