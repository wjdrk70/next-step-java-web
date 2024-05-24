package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

import controller.BaseController;
import controller.ControllerManager;
import http.HttpRequest;
import http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final String BASE_ROOT = "webapp";
    private static final String DEFAULT_FILE = "index.html";

    private Socket connection;
    private ControllerManager controllerManager;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
        this.controllerManager = new ControllerManager();
    }

    public void run() {
        log.debug("connection : {}", connection);
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);

            String method = request.getMethod();
            String path = request.getPath();
            log.debug("Path= {}", path);
            log.debug("Method= {}", method);

            if (path == null || path.equals("/")) {
                path = "/" + DEFAULT_FILE;
            }
            handleFileRequest(path, response);
            BaseController controller = controllerManager.getController(path);
            log.debug("controller = {}",controller);
            if (controller != null) {
                switch (method) {
                    case "GET":
                        controller.doGet(request, response);
                        break;
                    case "POST":
                        controller.doPost(request, response);
                        break;
                    case "PUT":
                        controller.doPut(request, response);
                        break;
                    case "PATCH":
                        controller.doPatch(request, response);
                        break;
                    case "DELETE":
                        controller.doDelete(request, response);
                        break;
                    default:
                        response.send404();
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void handleFileRequest(String path, HttpResponse response) throws IOException {
        File file = new File(BASE_ROOT, path);
        log.debug("Requested file: {}", file.getAbsolutePath());
        if (file.exists() && !file.isDirectory()) {
            byte[] body = Files.readAllBytes(file.toPath());
            String contentType = Files.probeContentType(file.toPath());
            response.send200(contentType != null ? contentType : "text/html;charset=utf-8", body);
        } else {
            response.send404();
        }
    }


}