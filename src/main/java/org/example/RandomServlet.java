package org.example;

import org.example.Server.Servlet.HttpServlet;
import org.example.Server.Servlet.HttpServletRequest;
import org.example.Server.Servlet.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RandomServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Type", "text/plain");
        try {
            response.getOutputStream().write("Hello!".getBytes());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {

    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) {

    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) {

    }
}
