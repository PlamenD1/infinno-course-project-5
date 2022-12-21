package org.example.Server.Servlet;

import org.example.Server.Filters.Interfaces.Filter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ServletContext {
    public static class Context {
        public boolean reloadable;
        public String docBase;

        public Context(boolean reloadable, String docBase) {
            this.reloadable = reloadable;
            this.docBase = docBase;
        }
    }
    private static ServletContext instance;
    public Map<String, HttpServlet> patternServletPairs = new HashMap<>();
    public Map<String, Filter> patternFilterPairs = new LinkedHashMap<>();
    public Map<String, Context> pathContextPairs = new HashMap<>();

    private ServletContext() {}

    public static ServletContext getInstance() {
        if (instance == null)
            instance = new ServletContext();

        return instance;
    }
}
