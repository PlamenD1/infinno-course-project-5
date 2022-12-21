package org.example.Server.Servlet;

import org.example.Server.Filters.FilterChain;
import org.example.Server.Filters.Interfaces.Filter;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.sql.ShardingKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestDispatcher {
    final List<String> VALID_METHODS = List.of("GET", "POST", "PUT", "DELETE");
    FilterChain chain;
    HttpServlet servlet;
    public Socket socket;

    public RequestDispatcher(HttpServlet servlet, FilterChain chain) {
        this.servlet = servlet;
        this.chain = chain;
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

        chain.doFilter(request, response);

        socket.close();
        System.out.println("SOCKET CLOSED");
    }

    public void dispatch(HttpServletRequest request) throws IOException {
        dispatch(request, new HttpServletResponse());
    }
}
