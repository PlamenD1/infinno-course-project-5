package org.example.PostAPI.Services;

import org.example.PostAPI.Interfaces.RegisterMapper;
import org.example.PostAPI.Models.User;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Objects;

public class RegisterDAO {

    SqlSessionFactory sessionFactory;
    public RegisterDAO() throws FileNotFoundException {
        FileReader fr = new FileReader(new File("src/main/java/org/example/PostAPI/config.xml"));
        sessionFactory = new SqlSessionFactoryBuilder().build(fr);
    }
    public int register(User user) {
        try (SqlSession conn = sessionFactory.openSession()) {
            var mapper = conn.getMapper(RegisterMapper.class);
            int result = mapper.register(user);
            conn.commit();

            return result;
        }
    }
}


