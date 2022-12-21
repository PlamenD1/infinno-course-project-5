package org.example.Server;

import org.example.Server.Config.*;
import org.example.Server.Config.Models.*;
import org.example.Server.Filters.Interfaces.Filter;
import org.example.Server.Servlet.HttpServlet;
import org.example.Server.Servlet.ServletContext;

import javax.xml.parsers.ParserConfigurationException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
    public static Server instance;
    Configuration configuration;

    ExecutorService threadPool;

    Server() throws Exception {
        XMLParser xmlParser = new XMLParser();
        String webXMLPath = "src/main/java/org/example/Server/web.xml";
        String serverXMLPath = "src/main/java/org/example/Server/Config/server.xml";
        this.configuration = xmlParser.parseConfiguration(webXMLPath, serverXMLPath);
        this.threadPool = Executors.newFixedThreadPool(5);
    }
    public static void main(String[] args) throws Exception {
        Server server = Server.getInstance();

        server.fillContextMaps();

        server.connect();
    }

    void fillContextMaps() throws Exception {
        fillServletMap();
        fillFilterMap();
        fillContextMap();
    }

    void fillServletMap() throws Exception {
        ServletContext context = ServletContext.getInstance();
        Map<String, HttpServlet> servlets = new HashMap<>();

        for (ServletFromXML s : configuration.servlets) {
            if (s.name == null || s.name.equals("") ||
                    s.clazz == null)
                throw new ParserConfigurationException("Element with tag: servlet must have valid children!");

            System.out.println(s.clazz);
            HttpServlet httpServlet = s.clazz.getDeclaredConstructor().newInstance();
            servlets.put(s.name, httpServlet);
        }

        for (ServletMapping sm : configuration.servletMappings) {
            HttpServlet servlet = servlets.get(sm.name);
            if (servlet == null)
                throw new ParserConfigurationException("Invalid servlet name for element with tag: servlet-mapping!");

            context.patternServletPairs.put(sm.urlPattern, servlet);
        }
    }

    void fillFilterMap() throws Exception {
        ServletContext context = ServletContext.getInstance();
        Map<String, Filter> filters = new LinkedHashMap<>();

        for (FilterFromXML f : configuration.filters) {
            if (f.name == null || f.name.equals("") ||
                    f.clazz == null)
                throw new ParserConfigurationException("Element with tag: filter must have valid children!");

            Filter filter = (Filter) f.clazz.getDeclaredConstructor().newInstance();
            filters.put(f.name, filter);
        }

        for (FilterMapping fm : configuration.filterMappings) {
            Filter filter = filters.get(fm.name);
            if (filter == null)
                throw new ParserConfigurationException("Invalid servlet name for element with tag: filter-mapping!");

            context.patternFilterPairs.put(fm.urlPattern, filter);
        }
    }

    void fillContextMap() {
        ServletContext context = ServletContext.getInstance();

        for (ContextFromXML c : configuration.contexts) {
            context.pathContextPairs.put(c.path, new ServletContext.Context(c.reloadable, c.docBase));
        }
    }

    static Server getInstance() throws Exception {
        if (instance == null)
            instance = new Server();

        return instance;
    }

    public void connect() throws Exception {
        System.out.println("LISTENING ON PORT: " + configuration.connectorPort);

        while (true) {
            try (ServerSocket server = new ServerSocket(configuration.connectorPort)) {
                Socket incoming = server.accept();
                System.out.println("USER CONNECTED...");
                new ServerTask(incoming).run();
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage() + " IN Server.java");
            }
        }
    }
}
