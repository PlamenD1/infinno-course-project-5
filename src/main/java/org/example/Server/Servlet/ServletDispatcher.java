package org.example.Server.Servlet;

import org.example.Server.Filters.FilterChain;
import org.example.Server.Filters.Interfaces.Filter;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServletDispatcher {
    private static ServletDispatcher instance;

    Map<String, ServletContext> pathContextPairs = new HashMap<>();
    private ServletDispatcher() {}

    public void dispatch(HttpServletRequest request, Socket socket) throws IOException {
        int endOfDocBase = request.fullPath.substring(1).indexOf("/");
        String docPath = request.fullPath.substring(1, endOfDocBase + 1);

        ServletContext context = pathContextPairs.get(docPath);

        request.fullPath = request.fullPath.substring(docPath.length() + 1);
        request.context = context;

        RequestDispatcher dispatcher = context.getRequestDispatcher(request.fullPath);
        HttpServletResponse response = new HttpServletResponse(socket);

        dispatcher.dispatch(request, response);

        socket.close();
    }

    public void addContext(String path, ServletContext context) {
        pathContextPairs.put(path, context);
    }

    public static ServletDispatcher getInstance() {
        if (instance == null)
            instance = new ServletDispatcher();

        return instance;
    }
}