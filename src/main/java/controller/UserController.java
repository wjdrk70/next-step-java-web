package controller;

import java.io.IOException;
import java.util.Map;

import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
        String path = request.getPath();
        if (path.equals("/user/create")) {
            handleUserCreateRequest(request, response);
        } else {
            response.send404();
        }
    }

    private void handleUserCreateRequest(HttpRequest request, HttpResponse response) throws IOException {
        try {
            Map<String, String> parameters = request.getQueryParams();
            User user = new User(parameters.get("userId"), parameters.get("password"), parameters.get("name"), parameters.get("email"));
            log.debug("User Created: {}", user);

            String responseBody = "User Created Successfully";
            response.send200("text/html;charset=utf-8", responseBody.getBytes());
        } catch (Exception e) {
            log.error("Error in handleUserCreateRequest", e);
            response.send500();
        }
    }


}
