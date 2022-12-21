package org.example.PostAPI.Models;

public class Token {
    public static long expirationPeriodInDays = 31L;

    public String token;
    public Integer user_id;
    public String created_date;
    public String expiration_date;

    public Token(String token, Integer user_id, String created_date, String expiration_date) {
        this.token = token;
        this.user_id = user_id;
        this.created_date = created_date;
        this.expiration_date = expiration_date;
    }
}
