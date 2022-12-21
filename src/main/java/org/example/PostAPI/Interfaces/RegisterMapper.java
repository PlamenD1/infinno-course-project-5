package org.example.PostAPI.Interfaces;

import org.example.PostAPI.Models.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

public interface RegisterMapper {
    @Insert("INSERT INTO users (username, password, salt) VALUES (#{user.username}, #{user.password}, #{user.salt})")
    @Options(useGeneratedKeys=true, keyColumn="id", keyProperty="id")
    int register(@Param("user") User user);
}
