package org.example.Server;

import org.example.Server.Filters.FilterChain;
import org.example.Server.Filters.Interfaces.Filter;
import org.example.Server.Servlet.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerTask implements Runnable {
    Socket socket;
    public ServerTask(Socket socket) {
        System.out.println("NEW SERVER TASK CREATED!");
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("IN RUN METHOD!");
        try {
            HttpServletRequest request = parseRequest();
            ServletContext context = ServletContext.getInstance();


            if (context.patternFilterPairs.size() == 0) {
                RequestDispatcher dispatcher = new RequestDispatcher(socket);
                dispatcher.dispatch(request);
                return;
            }

            FilterChain chain = new FilterChain(socket);
            System.out.println("CHAIN INIT");

            buildFilterList(chain, request.fullPath);
            System.out.println("GOT " + chain.filters.size() + " FILTERS");

            chain.doFilter(request, new HttpServletResponse());
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage() + " IN ServerTask.java");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    void buildFilterList(FilterChain chain, String path) {
        ServletContext context = ServletContext.getInstance();
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

    HttpServletRequest parseRequest() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line = reader.readLine();
        String[] queryArgs = line.split(" ");

        String method = queryArgs[0];
        String fullPath = queryArgs[1];
        HttpServlet servlet = null;

        if (!fullPath.endsWith("/")) {
            fullPath = fullPath.concat("/");
        }

        int endIndexOfContextName = fullPath.substring(1).indexOf("/");
        ServletContext.Context staticContentContext = getStaticContentContext(fullPath, endIndexOfContextName);

        fullPath = fullPath.substring(endIndexOfContextName + 1);

        servlet = getServlet(fullPath);

        String pathInfo = getPathInfo(fullPath);

        Map<String, String> headers = getHeaders(reader);

        return new HttpServletRequest(servlet, method, pathInfo, reader, headers, fullPath, staticContentContext);
    }

    ServletContext.Context getStaticContentContext(String path, int index) {
        ServletContext context = ServletContext.getInstance();
        String contextPattern = path.substring(1, index + 1);

        for (var entry : context.pathContextPairs.entrySet()) {
            if (entry.getKey().equals(contextPattern))
                return entry.getValue();
        }

        return null;
    }

    String getPathInfo(String path) {
        if (path.contains("?"))
            path = path.substring(0, path.length() - 1);

        System.out.println("PATH 1: " + path);

        int indexOfParamQuery = path.indexOf("?");

        if (indexOfParamQuery != -1) {
            path = path.substring(0, indexOfParamQuery) + "/";
        }

        System.out.println("PATH 2: " + path);


        String pathInfo;
        int endOfFirstPathPart = path.substring(1).indexOf("/");
        pathInfo = path.substring(endOfFirstPathPart + 1);
        System.out.println("PATHINFO 1: " + pathInfo);

        System.out.println("GOT PATHINFO: " + pathInfo);

        return pathInfo;
    }

    HttpServlet getServlet(String path) {
        ServletContext context = ServletContext.getInstance();
        Matcher matcher = null;
        HttpServlet servlet = null;

        for (var entry : context.patternServletPairs.entrySet()) {
            Pattern pattern = Pattern.compile(entry.getKey());
            matcher = pattern.matcher(path);
            if (matcher.find()) {
                servlet = entry.getValue();
                break;
            }
        }

        if (servlet == null) {
            servlet = context.patternServletPairs.get("static-content");
        }
        System.out.println(context.patternServletPairs);
        System.out.println("servlet: " + servlet);

        return servlet;
    }

    private Map<String, String> getHeaders(BufferedReader reader) throws IOException {
        Map<String, String> result = new HashMap<>();

        String line = reader.readLine();
        while (!line.equals("")) {
            String[] header = line.split(":");

            result.put(header[0], header[1]);

            line = reader.readLine();
        }

        return result;
    }
}
