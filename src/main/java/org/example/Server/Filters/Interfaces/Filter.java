package org.example.Server.Filters.Interfaces;

import org.example.Server.Filters.FilterChain;
import org.example.Server.Servlet.HttpServletRequest;
import org.example.Server.Servlet.HttpServletResponse;

import java.io.IOException;

public interface Filter {
    void doFilter(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain filterChain) throws IOException;
}
