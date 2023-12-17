package com.example.CustomBlog.content;

import com.example.CustomBlog.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
@Table
public class Comment {
    @Id
    @SequenceGenerator(
            name="comment_sq",
            sequenceName ="comment_sq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "comment_sq"
    )
    private long id;
    private String content;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "Content_id")
    @JsonBackReference(value = "content_comment")
    private Content post;

    public Comment() {
    }

    public Comment(String content, User user, Content post) {
        this.content = content;
        this.user = user;
        this.post = post;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Content getPost() {
        return post;
    }

    public void setPost(Content post) {
        this.post = post;
    }
}
