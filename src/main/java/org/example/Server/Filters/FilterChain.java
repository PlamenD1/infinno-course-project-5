package org.example.Server.Filters;

import org.example.Server.Filters.Interfaces.Filter;
import org.example.Server.ServerTask;
import org.example.Server.Servlet.HttpServlet;
import org.example.Server.Servlet.HttpServletRequest;
import org.example.Server.Servlet.HttpServletResponse;
import org.example.Server.Servlet.RequestDispatcher;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class FilterChain {
    HttpServlet servlet;
    public List<Filter> filters = new ArrayList<>();
    int filterNumber = 0;

    public FilterChain(HttpServlet servlet) {
        this.servlet = servlet;
    }

    public void doFilter(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (filterNumber == filters.size()) {
            servlet.service(request, response);
            return;
        }

        Filter filter = filters.get(filterNumber);
        filterNumber++;
        filter.doFilter(request, response, this);
    }
}