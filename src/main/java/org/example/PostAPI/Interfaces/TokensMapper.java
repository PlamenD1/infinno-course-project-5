package org.example.PostAPI.Interfaces;

import org.example.PostAPI.Models.Token;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TokensMapper {

    @Select("SELECT * FROM tokens")
    List<Token> getAllTokens();
    @Select("SELECT * FROM tokens WHERE token = #{token}")
    Token getToken(@Param("token") String token);

    @Select("SELECT * FROM tokens WHERE user_id = #{userId}")
    Token getTokenByUserId(@Param("userId") Integer userId);

    @Insert("INSERT INTO tokens (token, user_id, created_date, expiration_date) VALUES (#{token.token}, #{token.user_id}, #{token.created_date}, #{token.expiration_date})")
    int createToken(@Param("token") Token token);

    @Delete("DELETE FROM tokens WHERE token = #{token}")
    int deleteToken(@Param("token") String token);
}