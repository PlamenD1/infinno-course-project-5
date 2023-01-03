package org.example.Server.Servlet;

import org.example.Server.Session.Cookie;
import org.example.Server.Session.HttpSession;
import org.example.Server.Session.HttpSessionManager;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.*;

public class HttpServletRequest {

    HttpServlet servlet;
    Reader bodyReader;
    String method;
    String pathInfo;
    public String fullPath;
    String docBase;
    Map<String, String> queryParams = new HashMap<>();
    Map<String, String> headers;
    HttpSession httpSession;
    ServletContext context;
    List<Cookie> cookies = new ArrayList<>();

    public HttpServletRequest(HttpServlet servlet, String method, String pathInfo, Reader bodyReader, Map<String, String> headers, String fullPath, String docBase) {
        this.servlet = servlet;
        this.method = method;
        this.pathInfo = pathInfo;
        this.bodyReader = bodyReader;
        this.headers = headers;
        this.fullPath = fullPath;
        this.docBase = docBase;
        buildQueryParams();
        fillCookiesArray();
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

    void fillCookiesArray() {
        String cookiesStr = headers.get("Cookie");
        if (cookiesStr == null || cookiesStr.equals(""))
            return;

        String[] cookiesArr = cookiesStr.split(";");
        for (String str : cookiesArr) {
            String[] nameValuePair = str.split("=");
            Cookie cookie = new Cookie(nameValuePair[0], nameValuePair[1]);

            if (cookie.name.trim().equals("SESSION")) {
                httpSession = HttpSessionManager.getInstance().getSession(cookie.value);
            }

            cookies.add(cookie);
        }
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

    Cookie[] getCookies() {
        return cookies.toArray(new Cookie[0]);
    }

    public HttpSession getSession() {
        if (httpSession == null) {
            httpSession = HttpSessionManager.getInstance().createSession(context);
        }

        return httpSession;
    }

    public HttpSession getSession(boolean create) {
        if (create && httpSession == null) {
            httpSession = HttpSessionManager.getInstance().createSession(context);
        }

        return httpSession;
    }
}
