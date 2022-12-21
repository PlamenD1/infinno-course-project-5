package org.example.Server.Servlet;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpServletResponse {
    public static int SC_BAD_GATEWAY = 502; // Status code (502) indicating that the HTTP server received an invalid response from a server it consulted when acting as a proxy or gateway.
    public static int SC_BAD_REQUEST = 400; // Status code (400) indicating the request sent by the client was syntactically incorrect.
    public static int SC_FORBIDDEN = 403; //Status code (403) indicating the server understood the request but refused to fulfill it.
    public static int SC_NOT_FOUND = 404; // Status code (404) indicating that the requested resource is not available.
    public static int SC_OK = 200; // Status code (200) indicating the request succeeded normally.
    public static int SC_UNAUTHORIZED = 401; // Status code (401) indicating that the request requires HTTP authentication.

    int status;
    public OutputStream outputStream;
    Socket socket;
    boolean writingBody;

    Map<String, String> headers = new HashMap<>();
    Map<String, String> cookies = new HashMap<>();

    public void setStatus(int status) {
        if (writingBody) {
            System.out.println("WARN: Status is already written!");
            return;
        }

        this.status = status;
    }

    public void addHeader(String header, String value) {
        if (writingBody) {
            System.out.println("WARN: Headers are already written!");
            return;
        }

        headers.put(header, value);
    }

    public OutputStream getOutputStream() throws IOException {
        final int[] contentLength = {0};
        writingBody = true;
        outputStream = new OutputStream() {
            boolean closed = false;
            @Override
            public void write(int b) throws IOException {
                if (closed)
                    throw new IOException("OutputStream is closed!");

                contentLength[0]++;
                try {
                    write(new byte[] {(byte) b});
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void write(byte[] b) throws IOException {
                if (closed)
                    throw new IOException("OutputStream is closed!");

                contentLength[0] += b.length;
                write(b, 0, b.length);
            }

            @Override
            public void close() throws IOException {
                closed = true;
                socket.getOutputStream().write(("HTTP/1.1 " + status + "\n").getBytes());

                if (!cookies.isEmpty()) {
                    String cookieHeaderName = "Cookies";
                    StringBuilder cookieHeaderValue = new StringBuilder();
                    for (var cookie : cookies.entrySet()) {
                        cookieHeaderValue.append(cookie.getKey()).append("=").append(cookie.getValue()).append(";");
                    }
                    cookieHeaderValue.deleteCharAt(cookieHeaderValue.length() - 1);

                    headers.put(cookieHeaderName, cookieHeaderValue.toString());
                }

                for (var entity : headers.entrySet()) {
                    socket.getOutputStream().write((entity.getKey() + ":" + entity.getValue() + "\n").getBytes());
                }
                socket.getOutputStream().write(("Content-Length: " + contentLength[0]).getBytes());
                socket.getOutputStream().write("\n".getBytes());
            }
        };

        return outputStream;
    }

    public int getStatus() {
        return status;
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }
    public void addCookie(String name, String value) {
        cookies.put(name, value);
    }
}
