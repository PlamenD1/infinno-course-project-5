package org.example.Server.Servlet;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.sql.ShardingKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestDispatcher {
    final List<String> VALID_METHODS = List.of("GET", "POST", "PUT", "DELETE");
    ServletContext.Context staticContentContext;
    Socket socket;

    public RequestDispatcher(Socket socket) {
        this.socket = socket;
    }

    public void dispatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String sessionValue = request.getHeader("Session");
        response.outputStream = socket.getOutputStream();

        if (sessionValue != null)
            response.addHeader("Session", sessionValue);

        if (!VALID_METHODS.contains(request.method) || request.pathInfo == null || request.pathInfo.equals("")) {
            System.out.println("BAD REQUEST - INVALID METHOD");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.addHeader("Content-Type", "text/plain");
            response.getOutputStream().write("Bad Request - Invalid request syntax!".getBytes());

            return;
        }

        if (request.servlet == null) {
            System.out.println("BAD REQUEST - MISSING SERVLET");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.addHeader("Content-Type", "text/plain");
            response.getOutputStream().write("Bad Request - Missing servlet!".getBytes());

            return;
        }

        if (request.staticContentContext == null) {
            System.out.println("BAD REQUEST - NON-EXISTING CONTEXT");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.addHeader("Content-Type", "text/plain");
            response.getOutputStream().write("Bad Request - Non-existing context!".getBytes());

            return;
        }

        switch (request.method) {
            case "GET": {
                System.out.println("DO GET DISPATCHER");
                request.servlet.doGet(request, response);
                break;
            }
            case "POST":
                request.servlet.doPost(request, response);
                break;
            case "PUT":
                request.servlet.doPut(request, response);
                break;
            case "DELETE":
                request.servlet.doDelete(request, response);
                break;
        }

        socket.close();
        System.out.println("SOCKET CLOSED");
    }

    public void dispatch(HttpServletRequest request) throws IOException {
        dispatch(request, new HttpServletResponse());
    }
}
