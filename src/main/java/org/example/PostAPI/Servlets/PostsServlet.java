package org.example.PostAPI.Servlets;

import org.example.PostAPI.Models.Comment;
import org.example.PostAPI.Models.Post;
import org.example.PostAPI.Models.Token;
import org.example.PostAPI.Services.PostsDAO;
import org.example.PostAPI.Services.TokensDAO;
import org.example.PostAPI.Utils.PathInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.Server.Servlet.HttpServlet;
import org.example.Server.Servlet.HttpServletRequest;
import org.example.Server.Servlet.HttpServletResponse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.example.Server.Servlet.HttpServletResponse.*;

public class PostsServlet extends HttpServlet {

    final Map<String, Pattern> patterns = new HashMap<>();

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    PostsDAO postsDAO = new PostsDAO();
    TokensDAO tokensDAO = new TokensDAO();

    public PostsServlet() throws FileNotFoundException {}

    public void init() {
        patterns.put("singleNumberPath", Pattern.compile("\\/(\\d+)")); //get single post; update post; delete post
        patterns.put("commentsOfSinglePost", Pattern.compile("\\/(\\d+)/comments")); //get comments of single post
    }

    PathInfo getPath(HttpServletRequest request) {
        String path = request.getPathInfo();
        System.out.println(path);
        if (path == null || path.equals("/"))
            return new PathInfo("emptyPath", null);

        for (var entry : patterns.entrySet()) {
            Matcher matcher = entry.getValue().matcher(path);

            if (matcher.matches()) {
                return new PathInfo(entry.getKey(), matcher);
            }
        }

        return new PathInfo("404", null);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!isAuthorized(request, response))
            return;

        PathInfo pathInfo = getPath(request);
        String pathName = pathInfo.pathName;
        Matcher matcher = pathInfo.matcher;

        switch (pathName) {
            case "emptyPath": {
                List<Post> posts = postsDAO.getAllPosts();
                sendResponse(response, posts);
                break;
            }
            case "singleNumberPath": {
                String idString = matcher.group(1);
                int id = Integer.parseInt(idString);
                Post post = postsDAO.getPostById(id);

                if (post == null)
                    sendError(response, SC_NOT_FOUND, "404 Not Found!");

                sendResponse(response, post);
                break;
            }
            case "commentsOfSinglePost": {
                String idString = matcher.group(1);
                int id = Integer.parseInt(idString);

                List<Comment> obj = postsDAO.getCommentsForPost(id);
                sendResponse(response, obj);
                break;
            }
            default: sendError(response, SC_NOT_FOUND, "404 Not Found!");
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!isAuthorized(request, response))
            return;

        PathInfo pathInfo = getPath(request);
        String pathName = pathInfo.pathName;
        Post postToAdd = getPostFromBody(request);

        switch (pathName) {
            case "emptyPath": {
                postsDAO.addPost(postToAdd);

                sendResponse(response, postToAdd);
                break;
            }
            default:sendError(response, SC_NOT_FOUND, "404 Not Found!");
        }
    }

    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!isAuthorized(request, response))
            return;

        PathInfo pathInfo = getPath(request);
        String pathName = pathInfo.pathName;
        Matcher matcher = pathInfo.matcher;
        Post postToAdd = getPostFromBody(request);

        switch (pathName) {
            case "singleNumberPath": {
                String idString = matcher.group(1);

                postToAdd.id = Integer.parseInt(idString);
                int rowsAffected = postsDAO.updatePost(postToAdd);
                if (rowsAffected != 1)
                    sendError(response, SC_BAD_REQUEST, "Error while updating post!");

                sendResponse(response, postToAdd);
                break;
            }
            default: sendError(response, SC_NOT_FOUND, "404 Not Found!");
        }
    }

    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!isAuthorized(request, response))
            return;

        PathInfo pathInfo = getPath(request);
        String pathName = pathInfo.pathName;
        Matcher matcher = pathInfo.matcher;

        switch (pathName) {
            case "singleNumberPath": {
                String idString = matcher.group(1);
                int id = Integer.parseInt(idString);

                int rowsAffected = postsDAO.deletePost(id);
                if (rowsAffected != 1)
                    sendError(response, SC_BAD_REQUEST, "Error while deleting post!");

                sendResponse(response, id);
                break;
            }
            default: sendError(response, SC_NOT_FOUND, "404 Not Found!");
        }
    }

    void sendResponse(HttpServletResponse response, Object o) throws IOException {
        String json = gson.toJson(o);

        response.setStatus(200);
        response.addHeader("Content-Type", "application/json");
        response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
    }

    void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        String errorMessage = gson.toJson(message);

        response.addHeader("Content-Type", "application/json");
        response.getOutputStream().write(errorMessage.getBytes(StandardCharsets.UTF_8));
    }

    boolean isAuthorized(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authHeaderValue = request.getHeader("Authorization");

        if (authHeaderValue == null || authHeaderValue.equals("")) {
            sendError(response, SC_BAD_REQUEST, "Missing authorization method!");
            return false;
        }

        if (!authHeaderValue.startsWith("Bearer")) {
            sendError(response, SC_BAD_REQUEST, "Not supported method of authorization!");
            return false;
        }

        String tokenHash = authHeaderValue.split(" ")[1];

        if (tokenHash == null || tokenHash.equals("")) {
            sendError(response, SC_UNAUTHORIZED, "User is not authorized!");
            return false;
        }

        Token token = tokensDAO.getToken(tokenHash);

        if (token.token == null) {
            sendError(response, SC_UNAUTHORIZED, "This token doesn't exist or is expired! User is not authorized!");
            return false;
        }

        return true;
    }

    Post getPostFromBody(HttpServletRequest request) throws IOException {
        var collector = Collectors.joining(System.lineSeparator());
        String jsonString = request.getReader().lines().collect(collector);
        return gson.fromJson(jsonString, Post.class);
    }
}