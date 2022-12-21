package org.example.PostAPI.Servlets;

import org.example.PostAPI.Models.Token;
import org.example.PostAPI.Models.User;
import org.example.PostAPI.Services.RegisterDAO;
import org.example.PostAPI.Services.TokensDAO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.codec.digest.DigestUtils;
import org.example.Server.Servlet.HttpServlet;
import org.example.Server.Servlet.HttpServletRequest;
import org.example.Server.Servlet.HttpServletResponse;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Random;

import static org.example.Server.Servlet.HttpServletResponse.*;

public class RegisterServlet extends HttpServlet {
    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder.setPrettyPrinting().create();
    RegisterDAO registerDAO = new RegisterDAO();
    TokensDAO tokensDAO = new TokensDAO();

    public RegisterServlet() throws FileNotFoundException {}

    @Override
    public void init() {}

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userString = request.getParameter("user");;
        String passString = request.getParameter("pass");

        System.out.println("got params");

        if (userString == null ||
                passString == null) {
            sendError(response, SC_UNAUTHORIZED, "Username or password is empty! USER NOT LOGGED IN!");
            return;
        }

        Random r = new Random();
        int salt = r.nextInt();
        String hashPass = DigestUtils.sha1Hex(passString.concat(String.valueOf(salt)));

        String path = request.getPathInfo();
        System.out.println(path);
        if (path != null && !path.equals("/"))
            sendError(response, SC_NOT_FOUND, "404 Not Found!");

        User userToRegister = new User(userString, hashPass, salt);
        int rowsAffected = registerDAO.register(userToRegister);

        if (rowsAffected != 1) {
            String existingAccount = "Account with these credentials already exists!";
            sendError(response, SC_UNAUTHORIZED, existingAccount);
            return;
        }

        Token token = getToken(userToRegister.id);
        tokensDAO.createToken(token);

        response.setStatus(200);
        response.setHeader("Authorization", "Bearer " + token.token);
        response.getOutputStream().write("USER REGISTERED!".getBytes());
    }

    void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        String errorMessage = gson.toJson(message);

        response.addHeader("Content-Type", "application/json");
        response.getOutputStream().write(errorMessage.getBytes(StandardCharsets.UTF_8));
    }

    Token getToken(int userId) {
        int tokenNum = new Random().nextInt();
        String tokenHash = DigestUtils.sha1Hex(String.valueOf(tokenNum));
        String createdDate =  LocalDate.now().toString();
        String expirationDate = LocalDate.now().plusDays(Token.expirationPeriodInDays).toString();

        return new Token(tokenHash, userId, createdDate, expirationDate);
    }
}
