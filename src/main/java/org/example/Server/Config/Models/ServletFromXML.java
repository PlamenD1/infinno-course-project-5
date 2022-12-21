package org.example.Server.Config.Models;

import org.example.Server.Servlet.HttpServlet;

public class ServletFromXML {
    public String name;
    public Class<? extends HttpServlet> clazz;

    public ServletFromXML(String name, Class<? extends HttpServlet> clazz) {
        this.name = name;
        this.clazz = clazz;
    }
}