package org.example.Server.Servlet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class HttpServlet {
    public HttpServlet() {};
    public abstract void init();

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