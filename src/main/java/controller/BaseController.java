package controller;
import http.HttpRequest;
import http.HttpResponse;

import java.io.IOException;

public abstract class BaseController {
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {

        response.send404();
    }

    public void doPost(HttpRequest request, HttpResponse response) throws IOException {
        response.send404();
    }

    public void doPut(HttpRequest request, HttpResponse response) throws IOException {
        response.send404();
    }

    public void doPatch(HttpRequest request, HttpResponse response) throws IOException {
        response.send404();
    }

    public void doDelete(HttpRequest request, HttpResponse response) throws IOException {
        response.send404();
    }
}