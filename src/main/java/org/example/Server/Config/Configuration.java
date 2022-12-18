package org.example.Server.Config;

import org.example.Server.Servlet.HttpServlet;

import java.util.*;

public class Configuration {
    public List<ServletMapping> servletMappings = new ArrayList<>();
    public Set<Servlet> servlets = new HashSet<>();

    public Configuration() {}

    public void addServlet(Servlet servlet) {
        servlets.add(servlet);
    }

    public void addServletMappings(ServletMapping servletMapping) {
        servletMappings.add(servletMapping);
    }
}
