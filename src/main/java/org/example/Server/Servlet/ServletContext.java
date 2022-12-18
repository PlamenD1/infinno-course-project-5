package org.example.Server.Servlet;

import java.util.HashMap;
import java.util.Map;

public class ServletContext {
    private static ServletContext instance;

    public Map<String, HttpServlet> patternServletPairs = new HashMap<>();

    public static ServletContext getInstance() {
        if (instance == null)
            instance = new ServletContext();

        return instance;
    }
}
