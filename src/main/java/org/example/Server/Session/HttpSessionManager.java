package org.example.Server.Session;

import org.example.Server.Servlet.ServletContext;

import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.UUID;


public class HttpSessionManager {
    public static HttpSessionManager instance;
    Map<String, HttpSession> sessions = new HashMap<>();

    public static HttpSessionManager getInstance() {
        if (instance == null)
            instance = new HttpSessionManager();

        return instance;
    }

    public HttpSession createSession(ServletContext context) {
        HttpSession session = new HttpSession(context, constructRandomId());
        sessions.put(session.id, session);
        return session;
    }

    public HttpSession getSession(String id) {
        System.out.println(sessions);
        HttpSession session = sessions.get(id);

        if (session.inactive)
            return null;

        return session;
    }

    private String constructRandomId() {
        return UUID.randomUUID().toString();
    }
}