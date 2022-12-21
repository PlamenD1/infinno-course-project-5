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
        String docPath = request.fullPath.substring(1, endOfDocBase);

        ServletContext context = pathContextPairs.get(docPath);
        request.fullPath = request.fullPath.substring(docPath.length() + 1);
        RequestDispatcher dispatcher = context.getRequestDispatcher(request.fullPath);
        dispatcher.socket = socket;

        if (context.patternServletPairs.isEmpty()) {
            dispatcher.dispatch(request);
            return;
        }

        HttpServletResponse response = new HttpServletResponse();
        response.outputStream = socket.getOutputStream();
        FilterChain chain = new FilterChain(dispatcher);
        buildFilterList(chain, request.fullPath, context);
        chain.doFilter(request, response);
    }

    void buildFilterList(FilterChain chain, String path, ServletContext context) {
        Matcher matcher = null;
        Filter filter = null;

        for (var entry : context.patternFilterPairs.entrySet()) {
            Pattern pattern = Pattern.compile(entry.getKey());
            matcher = pattern.matcher(path);
            if (matcher.find()) {
                filter = entry.getValue();
                break;
            }
        }

        if (filter != null)
            chain.filters.add(filter);
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