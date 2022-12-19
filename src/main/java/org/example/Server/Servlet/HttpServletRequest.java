package org.example.Server.Servlet;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class HttpServletRequest {
    HttpServlet servlet;
    Reader bodyReader;
    String method;
    String pathInfo;
    public String fullPath;
    Map<String, String> headers = new HashMap<>();

    public HttpServletRequest(HttpServlet servlet, String method, String pathInfo, Reader bodyReader, Map<String, String> headers, String fullPath) {
        this.servlet = servlet;
        this.method = method;
        this.pathInfo = pathInfo;
        this.bodyReader = bodyReader;
        this.headers = headers;
        this.fullPath = fullPath;
    }

    public String getHeader(String header) {
        return headers.get(header);
    }

    public void setHeader(String header, String value) {
        headers.put(header, value);
    }

    public String getPathInfo() {
        return pathInfo;
    }

    public String getMethod() {
        return method;
    }

    public BufferedReader getReader() {
        return new BufferedReader(bodyReader);
    }
}
