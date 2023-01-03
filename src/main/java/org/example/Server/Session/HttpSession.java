package org.example.Server.Session;

import org.example.Server.Servlet.ServletContext;

import java.util.*;

public class HttpSession {
    private final static long DEFAULT_INACTIVE_INTERVAL = 3600000L;
    ServletContext context;
    long inactiveInterval;
    String id;
    Map<String, Object> attributes = new HashMap<>();
    Timer deactivate;
    boolean inactive = false;

    public HttpSession(ServletContext context, String id) {
        this.context = context;
        this.id = id;
        this.inactiveInterval = DEFAULT_INACTIVE_INTERVAL;
        setDeactivation();
    }

    private void setDeactivation() {
        deactivate = new Timer();
        deactivate.schedule(new TimerTask() {
            @Override
            public void run() {
                inactive = true;
            }
        }, inactiveInterval);
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public  void setMaxInactiveInterval(int interval) {
        this.inactiveInterval = interval;
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public String getId() {
        return id;
    }
}