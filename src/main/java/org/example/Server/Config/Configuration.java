package org.example.Server.Config;

import org.example.Server.Config.Models.*;

import java.util.*;

public class Configuration {
    public List<ServletMapping> servletMappings = new ArrayList<>();
    public Set<ServletFromXML> servlets = new HashSet<>();
    public List<FilterMapping> filterMappings = new ArrayList<>();
    public Set<FilterFromXML> filters = new LinkedHashSet<>();
    public List<ContextFromXML> contexts = new ArrayList<>();
    public int serverPort;
    public String serviceName;
    public int connectorPort;
    public String protocol;
    public int connectionTimeout;
    public String hostName;

    public Configuration() {}

    public void addServlet(ServletFromXML servlet) {
        servlets.add(servlet);
    }

    public void addServletMappings(ServletMapping servletMapping) {
        servletMappings.add(servletMapping);
    }

    public void addFilter(FilterFromXML filter) {
        filters.add(filter);
    }

    public void addFilterMapping(FilterMapping filterMapping) {
        filterMappings.add(filterMapping);
    }

    public void addContext(ContextFromXML context) {
        contexts.add(context);
    }

}
