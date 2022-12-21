package org.example.Server;

import org.example.Server.Config.*;
import org.example.Server.Config.Models.*;
import org.example.Server.Filters.Interfaces.Filter;
import org.example.Server.Servlet.HttpServlet;
import org.example.Server.Servlet.ServletContext;
import org.example.Server.Servlet.ServletDispatcher;

import javax.naming.ConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
    public static Server instance;
    Configuration configuration;

    ExecutorService threadPool;

    Server() throws Exception {
//        XMLParser xmlParser = new XMLParser();
//        String serverXMLPath = "src/main/java/org/example/Server/Config/server.xml";
//        this.configuration = xmlParser.parseConfiguration(serverXMLPath);
//
//        this.threadPool = Executors.newFixedThreadPool(5);
    }
    public static void main(String[] args) throws Exception {

        System.out.println(Server.getInstance().getClass().getClassLoader().getResource("web.xml"));
//        Server server = Server.getInstance();
//
//        server.initServletContexts();
//
//        server.connect();
    }

    void initServletContexts() throws Exception {
        ServletDispatcher dispatcher = ServletDispatcher.getInstance();

        for (ContextFromXML c : configuration.contexts) {
            File webXML = new File(c.docBase + "/web.xml");

            if (!webXML.exists())
                throw new ConfigurationException("Missing configuration file: web.xml in: " + c.docBase);

            XMLParser xmlParser = new XMLParser();
            ServletContext context = xmlParser.parseServletContext(webXML);
            context.docBase = c.docBase;
            dispatcher.addContext(c.path, context);
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
