package org.example.PostAPI.Filters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.Server.Filters.FilterChain;
import org.example.Server.Filters.Interfaces.Filter;
import org.example.Server.Servlet.HttpServletRequest;
import org.example.Server.Servlet.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.example.Server.Servlet.HttpServletResponse.*;


public class AuthFilter implements Filter {
    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder.setPrettyPrinting().create();

    public AuthFilter() {}

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        if (!isAuthorized(request, response)) {
            sendError(response, SC_FORBIDDEN, "USER IS NOT LOGGED IN!");
            return;
        }

        filterChain.doFilter(request, response);
    }

    boolean isAuthorized(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        HttpSession session = request.getSession(false);
//        if (session == null) {
//            sendError(response, SC_FORBIDDEN, "Unauthorized user!");
//            return false;
//        }

        return true;
    }
    void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        String errorMessage = gson.toJson(message);

        response.addHeader("Content-Type", "application/json");
        response.getOutputStream().write(errorMessage.getBytes(StandardCharsets.UTF_8));
    }
}
