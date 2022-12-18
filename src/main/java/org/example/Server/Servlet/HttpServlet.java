package org.example.Server.Servlet;

public abstract class HttpServlet {
    public HttpServlet() {};
    public abstract void doGet(HttpServletRequest request, HttpServletResponse response);
    public abstract void doPost(HttpServletRequest request, HttpServletResponse response);
    public abstract void doPut(HttpServletRequest request, HttpServletResponse response);
    public abstract void doDelete(HttpServletRequest request, HttpServletResponse response);
}