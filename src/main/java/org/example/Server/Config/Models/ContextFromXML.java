package org.example.Server.Config.Models;

public class ContextFromXML {
    public String path;
    public boolean reloadable;
    public String docBase;

    public ContextFromXML(String path, boolean reloadable, String docBase) {
        this.path = path;
        this.reloadable = reloadable;
        this.docBase = docBase;
    }
}
