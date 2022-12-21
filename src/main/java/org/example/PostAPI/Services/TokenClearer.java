package org.example.PostAPI.Services;

import org.example.PostAPI.Models.Token;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TokenClearer {
    TokensDAO dao = new TokensDAO();
    static long clearPeriod = 24 * 60 * 60 * 1000; //1 day
    public TokenClearer() throws FileNotFoundException {}

    public void startClearing() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                List<Token> tokens = dao.getAllToken();
                for (Token token : tokens) {
                    LocalDate tokenExpDate = LocalDate.parse(token.expiration_date);
                    if (!tokenExpDate.isBefore(LocalDate.now()))
                        continue;

                    dao.deleteToken(token.token);
                }

            }
        }, clearPeriod, clearPeriod);
    }
}
