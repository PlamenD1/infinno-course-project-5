package org.example.Server.Config;

import org.example.Server.Config.Models.*;
import org.example.Server.Filters.Interfaces.Filter;
import org.example.Server.Servlet.HttpServlet;
import org.example.Server.Servlet.ServletContext;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;

public class XMLParser {
    DocumentBuilder docBuilder;

    public XMLParser() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        docBuilder = factory.newDocumentBuilder();
    }

    public ServletContext parseServletContext(String fileName) throws Exception {
        Document doc = docBuilder.parse(new File(fileName));

        ServletContext servletContext = ServletContext.getInstance();



        //todo
        return ServletContext.getInstance();
    }

    public Configuration parseConfiguration(String webXML, String serverXML) throws Exception {
        Configuration configuration = new Configuration();

        Document web = docBuilder.parse(new File(webXML));

        fillSetOfType(configuration, web, "servlet");
        fillMappingsListOfType(configuration, web, "servlet");

        fillSetOfType(configuration, web, "filter");
        fillMappingsListOfType(configuration, web, "filter");

        Document server = docBuilder.parse(new File(serverXML));

        fillContexts(configuration, server);

        setInitializingInfo(configuration, server);

        return configuration;
    }

    void setInitializingInfo(Configuration configuration, Document doc) throws Exception {
        configuration.serverPort = getIntAttrValueFor(doc, "Server", "port");
        configuration.serviceName = getStringAttrValueFor(doc, "Service", "name");
        configuration.connectorPort = getIntAttrValueFor(doc, "Connector", "port");
        configuration.protocol = getStringAttrValueFor(doc, "Connector", "protocol");
        configuration.connectionTimeout = getIntAttrValueFor(doc, "Connector", "connectionTimeout");
        configuration.hostName = getStringAttrValueFor(doc, "Host", "name");
    }

    String getStringAttrValueFor(Document doc, String tag, String attr) throws Exception {
        NodeList items = doc.getElementsByTagName(tag);
        if (items.getLength() != 1)
            throw new ParserConfigurationException("Element: " + tag + " must be only 1!");

        Node node = items.item(0);
        Node attrNode = node.getAttributes().getNamedItem(attr);
        if (attrNode == null || attrNode.getTextContent().equals(""))
            throw new ParserConfigurationException("Element: " + tag + " must have non-empty attribute: " + attr + "!");

        return attrNode.getTextContent();
    }

    int getIntAttrValueFor(Document doc, String tag, String attr) throws Exception {
        NodeList items = doc.getElementsByTagName(tag);
        if (items.getLength() != 1)
            throw new ParserConfigurationException("Element: " + tag + "  must be only 1!");

        Node node = items.item(0);
        Node attrNode = node.getAttributes().getNamedItem(attr);
        if (attrNode == null || attrNode.getTextContent().equals(""))
            throw new ParserConfigurationException("Element: " + tag + " must have non-empty attribute: " + attr + "!");

        String valueStr = attrNode.getTextContent();
        int value;
        try {
            value = Integer.parseInt(valueStr);
        } catch (Exception e) {
            throw new ParserConfigurationException("Attribute: " + attr + " on element: " + tag + " must be valid integer!");
        }

        return value;
    }

    void fillContexts(Configuration configuration, Document doc) throws Exception {
        NodeList contexts = doc.getElementsByTagName("Context");
        if (contexts.getLength() == 0)
            return;

        for (int i = 0; i < contexts.getLength(); i++) {
            Node context = contexts.item(i);
            NamedNodeMap attrs = context.getAttributes();
            Node pathNode = attrs.getNamedItem("path");
            Node reloadableNode = attrs.getNamedItem("reloadable");
            Node docBaseNode = attrs.getNamedItem("docBase");
            if (pathNode == null || pathNode.getTextContent().equals(""))
                throw new ParserConfigurationException("Element: Context must have non-empty attribute: path!");
            if (reloadableNode == null || reloadableNode.getTextContent().equals(""))
                throw new ParserConfigurationException("Element: Context must have non-empty attribute: reloadable!");
            if (docBaseNode == null || docBaseNode.getTextContent().equals(""))
                throw new ParserConfigurationException("Element: Context must have non-empty attribute: docBase!");

            String path = pathNode.getTextContent();
            boolean reloadable = Boolean.getBoolean(reloadableNode.getTextContent());
            String docBase = docBaseNode.getTextContent();
            if (docBase.endsWith("/"))
                docBase = docBase.substring(0, docBase.length() - 1);

            configuration.addContext(new ContextFromXML(path, reloadable, docBase));
        }
    }

    @SuppressWarnings("unchecked")
    public void fillSetOfType(Configuration configuration, Document doc, String type) throws Exception {
        NodeList items = doc.getElementsByTagName(type);

        for (int i = 0; i < items.getLength(); i++) {
            if (items.item(i).getNodeName().equals("#text"))
                continue;

            NodeList children = items.item(i).getChildNodes();
            if (children.getLength() - 3 != 2)
                throw new ParserConfigurationException("Invalid children for element with tag: " + type + "!");

            Node nameNode = children.item(1);
            Node classNode = children.item(3);

            if (!nameNode.getNodeName().equals(type + "-name") ||
                !classNode.getNodeName().equals(type + "-class"))
                throw new ParserConfigurationException("Invalid children for element with tag: " + type + "!");

            String name = nameNode.getTextContent();
            Class<?> unprobedClass = Class.forName(classNode.getTextContent());

            if (type.equals("servlet")) {
                if (!HttpServlet.class.isAssignableFrom(unprobedClass))
                    throw new ParserConfigurationException("Invalid " + unprobedClass + " for servlet!");

                Class<? extends HttpServlet> servletClass = (Class<? extends HttpServlet>) unprobedClass;

                configuration.addServlet(new ServletFromXML(name, servletClass));
            } else if (type.equals("filter")) {
                if (!Filter.class.isAssignableFrom(unprobedClass))
                    throw new ParserConfigurationException("Invalid " + unprobedClass + " for filter!");

                configuration.addFilter(new FilterFromXML(name, unprobedClass));
            }
        }
    }

    public void fillMappingsListOfType(Configuration configuration, Document doc, String type) throws Exception {
        NodeList mappings = doc.getElementsByTagName(type + "-mapping");

        for (int i = 0; i < mappings.getLength(); i++) {
            if (mappings.item(i).getNodeName().equals("#text"))
                continue;

            NodeList children = mappings.item(i).getChildNodes();
            if (children.getLength() - 3 != 2)
                throw new ParserConfigurationException("Invalid children for element with tag: " + type + "-mapping!");

            Node nameNode = children.item(1);
            Node urlPatternNode = children.item(3);

            if (!nameNode.getNodeName().equals(type + "-name") ||
                    !urlPatternNode.getNodeName().equals("url-pattern"))
                throw new ParserConfigurationException("Invalid children for element with tag: " + type + "-mapping!");

            String name = nameNode.getTextContent();
            String urlPattern = urlPatternNode.getTextContent();

            if (type.equals("servlet"))
                configuration.addServletMappings(new ServletMapping(name, urlPattern));
            else if (type.equals("filter"))
                configuration.addFilterMapping(new FilterMapping(name, urlPattern));

        }
    }
}
