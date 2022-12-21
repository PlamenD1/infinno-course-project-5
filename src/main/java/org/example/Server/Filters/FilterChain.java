package org.example.Server.Filters;

import org.example.Server.Filters.Interfaces.Filter;
import org.example.Server.Servlet.HttpServlet;
import org.example.Server.Servlet.HttpServletRequest;
import org.example.Server.Servlet.HttpServletResponse;
import org.example.Server.Servlet.RequestDispatcher;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class FilterChain {
    Socket socket;
    public List<Filter> filters = new LinkedList<>();
    int filterNumber = 0;

    public FilterChain(Socket socket) {
        this.socket = socket;
    }

    public void doFilter(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (filterNumber >= filters.size()) {
            RequestDispatcher dispatcher = new RequestDispatcher(socket);
            try {
                System.out.println("DISPATCHING");
                dispatcher.dispatch(request, response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        System.out.println("APPLYING FILTER");

        Filter filter = filters.get(filterNumber);
        filterNumber++;
        filter.doFilter(request, response, this);
    }
}