package org.example.Server.Servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StaticContentServlet extends HttpServlet {
    @Override
    public void init() {

    }

    public StaticContentServlet() {}

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        List<String> listing = new ArrayList<>();
        String docBase = request.docBase;

        File file = new File(docBase + request.fullPath);

        try {
            if (file.isDirectory()) {
                File indexHtml = new File(docBase + "\\index.html");

                if (indexHtml.exists())
                    setBodyAndHeaders(indexHtml, response);
                else {
                    final int[] deepness = {0};
                    final String[] observedPath = {System.getProperty("user.dir")};
                    String[] pathPieces = docBase.split("/");

                    Files.walkFileTree(Path.of(docBase), new SimpleFileVisitor<>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                            listing.add(file.toString());
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                            if ((dir.toString() + "\\").equals(observedPath[0])) {
                                if (pathPieces.length > 0) {
                                    observedPath[0] += pathPieces[deepness[0]];
                                }
                                if (deepness[0] + 1 < pathPieces.length)
                                    deepness[0]++;
                                return FileVisitResult.CONTINUE;
                            }

                            listing.add(dir.toString());
                            return FileVisitResult.SKIP_SUBTREE;
                        }
                    });

                    response.setStatus(HttpServletResponse.SC_OK);
                    response.addHeader("Content-Type", "text/html");
                    response.getOutputStream().write(buildHtml(listing));
                }
            } else {
                if (!file.exists()) {
                    sendNotFound(response);
                    return;
                }

                setBodyAndHeaders(file, response);
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    void sendNotFound(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.addHeader("Content-Type", "text/plain");
        response.getOutputStream().write("404 Not Found! Here".getBytes());
    }

    void setBodyAndHeaders(File file, HttpServletResponse response) throws Exception {
        if (!file.exists()) {
            sendNotFound(response);
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);

        setContentHeaders(file, response);

        setBody(file, response);
    }

    void setContentHeaders(File file, HttpServletResponse response) throws IOException {
        String type = Files.probeContentType(file.toPath());
        long length = file.length();
        Date lastModified = new Date(file.lastModified());

        response.addHeader("Content-Type", type);
        response.addHeader("Content-Length", String.valueOf(length));
        response.addHeader("Last-Modified", lastModified.toString());
        response.addHeader("Date", new Date().toString());
    }

    void setBody(File file, HttpServletResponse response) throws Exception {
        try (FileInputStream fis = new FileInputStream(file)) {
            response.getOutputStream().write(fis.readAllBytes());
        }
    }

    byte[] buildHtml(List<String> paths) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<html>")
                .append("<head></head>")
                .append("<body>")
                .append("<h1>Indexes</h1>");
        for (String path : paths) {
            String name = String.valueOf(Path.of(path).getFileName());
            sb.append("<a href=\"").append("/").append(name).append("\">").append(name).append("</a><br/>");
        }
        sb.append("</body>")
                .append("</html>\n");

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
