package org.example.PostAPI.Servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.codec.digest.DigestUtils;
import org.example.PostAPI.Models.Token;
import org.example.PostAPI.Models.User;
import org.example.PostAPI.Services.LoginDAO;
import org.example.PostAPI.Services.TokenClearer;
import org.example.PostAPI.Services.TokensDAO;
import org.example.Server.Servlet.HttpServlet;
import org.example.Server.Servlet.HttpServletRequest;
import org.example.Server.Servlet.HttpServletResponse;
import org.example.Server.Session.HttpSession;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.example.Server.Servlet.HttpServletResponse.*;

public class LoginServlet extends HttpServlet {
    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder.setPrettyPrinting().create();

    LoginDAO loginDAO = new LoginDAO();
    TokensDAO tokensDAO = new TokensDAO();

    public void init() {
        try {
            TokenClearer tokenClearer = new TokenClearer();
            tokenClearer.startClearing();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public LoginServlet() throws FileNotFoundException, URISyntaxException {}

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userString = request.getParameter("user");
        String passString = request.getParameter("pass");

        if (userString == null ||
                passString == null) {
            sendError(response, SC_UNAUTHORIZED, "Username or password is empty! USER NOT LOGGED IN!");
            return;
        }

        Integer salt = loginDAO.getUserSalt(userString);
        if (salt == null) {
            sendError(response, SC_UNAUTHORIZED, "This user does not exists! USER NOT LOGGED IN!");
            return;
        }
        String hashPass = DigestUtils.sha1Hex(passString.concat(salt.toString()));
//
//        String path = request.getPathInfo();
//        System.out.println("PATH: " + path);
//        if (path != null && !path.equals("/")) {
//            sendError(response, SC_NOT_FOUND, "404 Not Found!");
//            return;
//        }
        Integer userId = loginDAO.login(new User(userString, hashPass));
        System.out.println(userId);
        if (userId == null) {
            String invalidCredentials = "Invalid credentials! USER NOT LOGGED IN!";
            sendError(response, SC_UNAUTHORIZED, invalidCredentials);
            return;
        }

        Token token = tokensDAO.getTokenByUserId(userId);

        if (token == null) {
            token = generateToken(userId);
            tokensDAO.createToken(token);
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("user", userString);

        response.setStatus(SC_OK);
        response.setHeader("Authorization", "Bearer " + token.token);
        response.getOutputStream().write("LOGGED IN!".getBytes());
    }

    void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        String errorMessage = gson.toJson(message);

        response.addHeader("Content-Type", "application/json");
        response.getOutputStream().write(errorMessage.getBytes(StandardCharsets.UTF_8));
    }

    Token generateToken(int userId) {
        long tokenNum = System.currentTimeMillis() + userId;
        String tokenHash = DigestUtils.sha1Hex(String.valueOf(tokenNum));
        String createdDate =  LocalDate.now().toString();
        String expirationDate = LocalDate.now().plusDays(Token.expirationPeriodInDays).toString();

        return new Token(tokenHash, userId, createdDate, expirationDate);
    }
}
