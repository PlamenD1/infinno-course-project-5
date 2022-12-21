package org.example.PostAPI.Services;

import org.example.PostAPI.Interfaces.CommentsMapper;
import org.example.PostAPI.Models.Comment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Objects;

public class CommentsDAO {
    SqlSessionFactory sessionFactory;
    public CommentsDAO() throws FileNotFoundException {
        sessionFactory = new SqlSessionFactoryBuilder().build(new FileReader("src/main/java/org/example/PostAPI/config.xml"));
    }

    public List<Comment> getCommentsByPost(int id) {
         try (SqlSession conn = sessionFactory.openSession()) {
            var mapper = conn.getMapper(CommentsMapper.class);
            return mapper.getCommentsByPost(id);
        }
    }
}
