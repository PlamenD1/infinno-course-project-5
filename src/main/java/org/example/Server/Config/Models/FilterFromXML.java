package org.example.Server.Config.Models;

import org.example.Server.Filters.Interfaces.Filter;

public class FilterFromXML {
    public String name;
    public Class<?> clazz;

    public FilterFromXML(String name, Class<?> clazz) {
        this.name = name;
        this.clazz = clazz;
    }
}