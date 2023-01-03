package org.example.Server.Servlet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class HttpServlet {
    public HttpServlet() {};
    public abstract void init();

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        switch (request.method) {
            case "GET": {
                this.doGet(request, response);
                break;
            }
            case "POST":
                this.doPost(request, response);
                break;
            case "PUT":
                this.doPut(request, response);
                break;
            case "DELETE":
                this.doDelete(request, response);
                break;
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        sendNotSupportedError(resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        sendNotSupportedError(resp);
    }

    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        sendNotSupportedError(resp);
    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        sendNotSupportedError(resp);
    }

    private void sendNotSupportedError(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.addHeader("Content-Type", "text/plain");
        response.getOutputStream().write("METHOD NOT SUPPORTED!".getBytes());
    }
}