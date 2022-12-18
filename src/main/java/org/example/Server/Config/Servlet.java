package org.example.Server.Config;

import org.example.Server.Servlet.HttpServlet;

public class Servlet {
    public String name;
    public Class<? extends HttpServlet> clazz;

    public Servlet(String name, Class<? extends HttpServlet> clazz) {
        this.name = name;
        this.clazz = clazz;
    }
}