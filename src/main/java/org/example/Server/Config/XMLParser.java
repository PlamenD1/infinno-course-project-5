package org.example.Server.Config;

import org.example.Server.Servlet.HttpServlet;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class XMLParser {
    Document doc;

    public XMLParser(String fileName) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        this.doc = builder.parse(fileName);
    }
    public Configuration parseXML() throws Exception {
        Configuration configuration = new Configuration();

        fillServletsSet(configuration);
        fillServletMappingsList(configuration);

        return configuration;
    }

    @SuppressWarnings("unchecked")
    public void fillServletsSet(Configuration configuration) throws Exception {
        NodeList servlets = doc.getElementsByTagName("servlet");

        for (int i = 0; i < servlets.getLength(); i++) {
            if (servlets.item(i).getNodeName().equals("#text"))
                continue;

            NodeList servletChildren = servlets.item(i).getChildNodes();
            if (servletChildren.getLength() - 3 != 2)
                throw new ParserConfigurationException("Invalid children for element with tag: servlet!");

            Node servletNameNode = servletChildren.item(1);
            Node servletClassNode = servletChildren.item(3);

            if (!servletNameNode.getNodeName().equals("servlet-name") ||
                !servletClassNode.getNodeName().equals("servlet-class"))
                throw new ParserConfigurationException("Invalid children for element with tag: servlet!");

            String servletName = servletNameNode.getTextContent();
            Class<?> unprobedServletClass = Class.forName(servletClassNode.getTextContent());

            if (!HttpServlet.class.isAssignableFrom(unprobedServletClass))
                throw new ParserConfigurationException("Invalid " + unprobedServletClass + " for servlet!");

            Class<? extends HttpServlet> servletClass = (Class<? extends HttpServlet>) unprobedServletClass;

            configuration.addServlet(new Servlet(servletName, servletClass));
        }
    }

    public void fillServletMappingsList(Configuration configuration) throws Exception {
        NodeList servletMappings = doc.getElementsByTagName("servlet-mapping");

        for (int i = 0; i < servletMappings.getLength(); i++) {
            if (servletMappings.item(i).getNodeName().equals("#text"))
                continue;

            NodeList servletChildren = servletMappings.item(i).getChildNodes();
            if (servletChildren.getLength() - 3 != 2)
                throw new ParserConfigurationException("Invalid children for element with tag: servlet-mapping!");

            Node servletNameNode = servletChildren.item(1);
            Node urlPatternNode = servletChildren.item(3);

            if (!servletNameNode.getNodeName().equals("servlet-name") ||
                    !urlPatternNode.getNodeName().equals("url-pattern"))
                throw new ParserConfigurationException("Invalid children for element with tag: servlet-mapping!");

            String servletName = servletNameNode.getTextContent();
            String urlPattern = urlPatternNode.getTextContent();

            configuration.addServletMappings(new ServletMapping(servletName, urlPattern));
        }
    }
}
