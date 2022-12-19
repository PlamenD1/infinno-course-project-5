package org.example.Server;

import org.example.Server.Servlet.HttpServlet;
import org.example.Server.Servlet.HttpServletRequest;
import org.example.Server.Servlet.RequestDispatcher;
import org.example.Server.Servlet.ServletContext;

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
            ServletContext context = ServletContext.getInstance();
            System.out.println("GOT CONTEXT");
            context.patternServletPairs = Server.getInstance().patternServletPairs;
            System.out.println("GOT SERVLET PAIRS");

            HttpServletRequest request = parseRequest();

            System.out.println(request.getMethod() + " " + request.getPathInfo());
            RequestDispatcher dispatcher = new RequestDispatcher(socket);
            dispatcher.dispatch(request);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage() + " IN ServerTask.java");
            throw new RuntimeException(e);
        }
    }

    HttpServletRequest parseRequest() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ServletContext context = ServletContext.getInstance();
        String line = reader.readLine();
        String[] queryArgs = line.split(" ");

        String method = queryArgs[0];
        Matcher matcher = null;
        String fullPath = queryArgs[1];
        HttpServlet servlet = null;

        if (!fullPath.endsWith("/")) {
            fullPath = fullPath.concat("/");
        }

        for (var entry : context.patternServletPairs.entrySet()) {
            Pattern pattern = Pattern.compile(entry.getKey());
            matcher = pattern.matcher(fullPath);
            if (matcher.find()) {
                servlet = entry.getValue();
                break;
            }
        }

        if (servlet == null) {
            servlet = context.patternServletPairs.get("static-content");
        }

        int endOfFirstPathPart = fullPath.indexOf("/");
        String pathInfo = fullPath.substring(endOfFirstPathPart + 1);


        Map<String, String> headers = getHeaders(reader);

        return new HttpServletRequest(servlet, method, pathInfo, reader, headers, fullPath);
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
