package org.example.PostAPI.Filters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Server.Filters.FilterChain;
import org.example.Server.Filters.Interfaces.Filter;
import org.example.Server.Servlet.HttpServletRequest;
import org.example.Server.Servlet.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;


public class LogFilter implements Filter {
    Logger logger = LogManager.getLogger(LogFilter.class);
//    FileHandler fh;
    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder.setPrettyPrinting().create();

    public LogFilter() {}

    static String getLogOutputFile(InputStream inputStream) throws IOException {
        Properties properties = new Properties();
        properties.load(inputStream);

        return properties.getProperty("filePath");
    }

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        System.out.println("IN LOG FILTER");
        long t1 = System.currentTimeMillis();
        filterChain.doFilter(request, response);
        long t2 = System.currentTimeMillis();

//        HttpSession httpSession = httpServletRequest.getSession(false);


        int status = response.getStatus();
        String statusString = status + ": ";

//        String user = httpSession == null ? "" : (String) httpSession.getAttribute("user");
//        String userString = user.equals("") ? "" : "user: " + user + " ";

        String executionTimeString = "time for execution: " + (t2 - t1) + "ms ";

        String methodString = request.getMethod() + " ";

        String pathInfo = request.getPathInfo() == null ? "" : request.getPathInfo();
        String pathString = request.getServletPath() + pathInfo;

        System.out.println(status);

        if (status >= 400) {
            logger.error("Error " + statusString +
//                    userString +
                    executionTimeString +
                    methodString +
                    pathString);
            return;
        }

        logger.info(statusString +
//                userString +
                executionTimeString +
                methodString +
                pathString);
    }

    void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        String errorMessage = gson.toJson(message);

        response.addHeader("Content-Type", "application/json");
        response.getOutputStream().write(errorMessage.getBytes(StandardCharsets.UTF_8));
    }
}
