package org.example.PostAPI.Services;

import org.example.PostAPI.Interfaces.LoginMapper;
import org.example.PostAPI.Models.User;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.util.Objects;

public class LoginDAO {
    SqlSessionFactory sessionFactory;

    public LoginDAO() throws FileNotFoundException, URISyntaxException {
        sessionFactory = new SqlSessionFactoryBuilder().build(new FileReader(new File("src/main/java/org/example/PostAPI/config.xml")));
    }

    public Integer login(User user) {
        try (SqlSession conn = sessionFactory.openSession()) {
            var mapper = conn.getMapper(LoginMapper.class);
            return mapper.login(user);
        }
    }

    public Integer getUserSalt(String username) {
        try (SqlSession conn = sessionFactory.openSession()) {
            var mapper = conn.getMapper(LoginMapper.class);
            return mapper.getUserSalt(username);
        }
    }

}
