package org.example.Server.Servlet;

import org.example.Server.Filters.FilterChain;
import org.example.Server.Filters.Interfaces.Filter;

import java.io.IOException;
import java.io.OutputStream;
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
    final static List<String> VALID_METHODS = List.of("GET", "POST", "PUT", "DELETE");
    FilterChain chain;

    public RequestDispatcher(FilterChain chain) {
        this.chain = chain;
    }

    public void dispatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (isRequestInvalid(request)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.addHeader("Content-Type", "text/plain");
            response.getOutputStream().write("Bad Request - Invalid request syntax!".getBytes());

            return;
        }

        chain.doFilter(request, response);

        if (request.httpSession != null) {
            response.setHeader("Set-Cookie", "SESSION=" + request.httpSession.getId());
        }

        response.sendResponse();
    }

    static boolean isRequestInvalid(HttpServletRequest request) {
        return !VALID_METHODS.contains(request.method) || request.pathInfo == null || request.pathInfo.equals("");
    }
}
