package org.example.Server;

import org.example.Server.Config.Configuration;
import org.example.Server.Config.Servlet;
import org.example.Server.Config.ServletMapping;
import org.example.Server.Config.XMLParser;
import org.example.Server.Servlet.HttpServlet;

import javax.xml.parsers.ParserConfigurationException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
    public static Server instance;
    Configuration configuration;

    Map<String, HttpServlet> patternServletPairs = new HashMap<>();
    static int port = 80;

    ExecutorService threadPool;

    Server() throws Exception {
        XMLParser xmlParser = new XMLParser("src/main/java/org/example/Server/web.xml");
        this.configuration = xmlParser.parseXML();
        this.threadPool = Executors.newFixedThreadPool(5);
    }
    public static void main(String[] args) throws Exception {
        Server server = Server.getInstance();

        server.fillServletMap();

        server.connect();
    }

    void fillServletMap() throws Exception {
        Map<String, HttpServlet> servlets = new HashMap<>();

        for (Servlet s : configuration.servlets) {
            if (s.name == null || s.name.equals("") ||
                s.clazz == null)
                throw new ParserConfigurationException("Element with tag: servlet must have valid children!");

            HttpServlet httpServlet = s.clazz.getDeclaredConstructor().newInstance();
            servlets.put(s.name, httpServlet);
        }

        for (ServletMapping sm : configuration.servletMappings) {
            HttpServlet servlet = servlets.get(sm.name);
            if (servlet == null)
                throw new ParserConfigurationException("Invalid servlet name for element with tag: servlet-mapping!");

            patternServletPairs.put(sm.urlPattern, servlet);
        }
    }

    static Server getInstance() throws Exception {
        if (instance == null)
            instance = new Server();

        return instance;
    }

    public void connect() throws Exception {
        System.out.println("LISTENING ON PORT: " + port);

        while (true) {
            try (ServerSocket server = new ServerSocket(port)) {
                Socket incoming = server.accept();
                System.out.println("USER CONNECTED...");
                threadPool.submit(new ServerTask(incoming));
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage() + " IN Server.java");
            }
        }
    }
}
