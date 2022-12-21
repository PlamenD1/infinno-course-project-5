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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class XMLParser {
    DocumentBuilder docBuilder;

    public XMLParser() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        docBuilder = factory.newDocumentBuilder();
    }

    public ServletContext parseServletContext(File webXML) throws Exception {
        ServletContext context = new ServletContext();
        Document web = docBuilder.parse(webXML);

        Map<String, Object> servlets = fillSetOfType(web, "servlet");
        Map<String, String> servletMappings = fillMappingsListOfType(web, "servlet");

        Map<String, Object> filters = fillSetOfType(web, "filter");
        Map<String, String> filterMappings = fillMappingsListOfType(web, "filter");

        for (var sm : servletMappings.entrySet()) {
            HttpServlet servlet = (HttpServlet) servlets.get(sm.getKey());
            if (servlet == null)
                throw new ParserConfigurationException("Invalid servlet name for element with tag: servlet-mapping!");

            context.patternServletPairs.put(sm.getValue(), servlet);
        }

        for (var fm : filterMappings.entrySet()) {
            Filter filter = (Filter) filters.get(fm.getKey());
            if (filter == null)
                throw new ParserConfigurationException("Invalid servlet name for element with tag: filter-mapping!");

            context.patternFilterPairs.put(fm.getValue(), filter);
        }

        return context;
    }

    public Configuration parseConfiguration(String serverXML) throws Exception {
        Configuration configuration = new Configuration();

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
    public Map<String, Object> fillSetOfType(Document doc, String type) throws Exception {
        Map<String, Object> result = new LinkedHashMap<>();
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

                HttpServlet servlet = servletClass.getDeclaredConstructor().newInstance();

                result.put(name, servlet);
            } else if (type.equals("filter")) {
                if (!Filter.class.isAssignableFrom(unprobedClass))
                    throw new ParserConfigurationException("Invalid " + unprobedClass + " for filter!");

                Filter filter = (Filter) unprobedClass.getDeclaredConstructor().newInstance();

                result.put(name, filter);
            }
        }

        return result;
    }

    public Map<String, String> fillMappingsListOfType(Document doc, String type) throws Exception {
        Map<String, String> result = new HashMap<>();
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

            result.put(name, urlPattern);
        }

        return result;
    }
}
