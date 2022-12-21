package org.example.Server.Config.Models;

public class ServletMapping {
    public String name;
    public String urlPattern;

    public ServletMapping(String name, String urlPattern) {
        this.name = name;
        this.urlPattern = urlPattern;
    }
}