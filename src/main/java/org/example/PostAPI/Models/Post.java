package org.example.PostAPI.Models;

public class Post {
    public int id;
    public String post_body;
    public String author_id;

    public Post() {}
    public Post(String post_body, String author_id) {
        this.post_body = post_body;
        this.author_id = author_id;
    }
    public Post(int id, String post_body, String author_id) {
        this.id = id;
        this.post_body = post_body;
        this.author_id = author_id;
    }
}
