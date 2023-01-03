package org.example.Server.Servlet;

import org.example.Server.Filters.FilterChain;
import org.example.Server.Filters.Interfaces.Filter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServletContext {
    public String docBase;
    public Map<String, HttpServlet> patternServletPairs = new HashMap<>();
    public Map<String, Filter> patternFilterPairs = new LinkedHashMap<>();

    public ServletContext() {}

    public void addServlet(String pattern, HttpServlet servlet) {
        patternServletPairs.put(pattern, servlet);
    }

    public void addFilter(String pattern, Filter filter) {
        patternFilterPairs.put(pattern, filter);
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        Matcher matcher = null;
        HttpServlet servlet = null;

        for (var entry : patternServletPairs.entrySet()) {
            Pattern pattern = Pattern.compile(entry.getKey());
            matcher = pattern.matcher(path);
            if (matcher.find()) {
                servlet = entry.getValue();
                break;
            }
        }

        if (servlet == null) {
            servlet = patternServletPairs.get("static-content");
        }

        FilterChain chain = getFilterChain(servlet, path);

        return new RequestDispatcher(chain);
    }

    FilterChain getFilterChain(HttpServlet servlet, String path) {
        FilterChain chain = new FilterChain(servlet);
        Matcher matcher = null;
        Filter filter = null;

        for (var entry : patternFilterPairs.entrySet()) {
            Pattern pattern = Pattern.compile(entry.getKey());
            matcher = pattern.matcher(path);
            if (matcher.find()) {
                filter = entry.getValue();
                break;
            }
        }

        if (filter != null)
            chain.filters.add(filter);

        return chain;
    }
}
