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
            ServletDispatcher dispatcher = ServletDispatcher.getInstance();
            dispatcher.dispatch(request, socket);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage() + " IN ServerTask.java");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    HttpServletRequest parseRequest() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line = reader.readLine();
        String[] queryArgs = line.split(" ");

        String method = queryArgs[0];
        String fullPath = queryArgs[1];

        if (!fullPath.endsWith("/")) {
            fullPath = fullPath.concat("/");
        }

        String pathInfo = getPathInfo(fullPath);

        Map<String, String> headers = getHeaders(reader);

        return new HttpServletRequest(null, method, pathInfo, reader, headers, fullPath, "");
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
