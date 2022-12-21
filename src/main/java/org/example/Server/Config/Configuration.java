package org.example.Server.Config;

import org.example.Server.Config.Models.*;

import java.util.*;

public class Configuration {
    public List<ContextFromXML> contexts = new ArrayList<>();
    public int serverPort;
    public String serviceName;
    public int connectorPort;
    public String protocol;
    public int connectionTimeout;
    public String hostName;

    public Configuration() {}

    public void addContext(ContextFromXML context) {
        contexts.add(context);
    }

}
