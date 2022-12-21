package org.example.Server.Config.Models;

public class FilterMapping {
    public String name;
    public String urlPattern;

    public FilterMapping(String name, String urlPattern) {
        this.name = name;
        this.urlPattern = urlPattern;
    }
}