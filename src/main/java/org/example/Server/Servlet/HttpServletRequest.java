package org.example.Server.Servlet;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class HttpServletRequest {

    ServletContext.Context staticContentContext;
    HttpServlet servlet;
    Reader bodyReader;
    String method;
    String pathInfo;
    public String fullPath;
    Map<String, String> queryParams = new HashMap<>();
    Map<String, String> headers;
    Map<String, String> cookies = new HashMap<>();

    public HttpServletRequest(HttpServlet servlet, String method, String pathInfo, Reader bodyReader, Map<String, String> headers, String fullPath, ServletContext.Context staticContentContext) {
        this.servlet = servlet;
        this.method = method;
        this.pathInfo = pathInfo;
        this.bodyReader = bodyReader;
        this.headers = headers;
        this.fullPath = fullPath;
        this.staticContentContext = staticContentContext;
        buildQueryParams();
        fillCookiesMap();
    }

    void buildQueryParams() {
        if (fullPath.contains("?")) {
            String paramQuery = fullPath.split("\\?")[1];
            paramQuery = paramQuery.substring(0, paramQuery.length() - 1);
            String[] pairs = paramQuery.split("&");
            for (String pair : pairs) {
                String[] nameValue = pair.split("=");
                String name = nameValue[0];
                String value = nameValue[1];
                queryParams.put(name, value);
            }
        }
    }

    void fillCookiesMap() {
        String cookiesStr = headers.get("Cookies");
        if (cookiesStr == null || cookiesStr.equals(""))
            return;

        String[] cookiesArr = cookiesStr.split(";");
        for (String cookie : cookiesArr) {
            String[] nameValuePair = cookie.split("=");
            cookies.put(nameValuePair[0], nameValuePair[1]);
        }

        headers.remove("Cookies");
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

    public String getParameter(String param) {
        return queryParams.get(param);
    }

    public String getServletPath() {
        int indexOfEndOfServletName = fullPath.indexOf("/");
        return fullPath.substring(0, indexOfEndOfServletName);
    }
}
