package org.example.PostAPI.Interfaces;

import org.example.PostAPI.Models.Comment;
import org.example.PostAPI.Models.Post;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface PostsMapper {
    @Select("SELECT * FROM posts")
    List<Post> getAllPosts();

    @Select("SELECT * FROM posts WHERE id = #{value}")
    Post getPostById(int id);

    @Select("SELECT * FROM comments where id = #{value}")
    List<Comment> getCommentsForPost(int id);

    @Insert("INSERT INTO posts (post_body, author_id) VALUES (#{post.post_body}, #{post.author_id})")
    @Options(useGeneratedKeys=true, keyColumn="id", keyProperty="id")
    int addPost(@Param("post") Post post);

    @Update("UPDATE posts SET post_body = #{post.post_body}, author_id = #{post.author_id} WHERE id = #{post.id}")
    int updatePost(@Param("post") Post post);

    @Delete("DELETE FROM posts WHERE id = #{id}")
    int deletePost(int id);
}
