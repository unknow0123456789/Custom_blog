package com.example.CustomBlog.asset;

import com.example.CustomBlog.content.Comment;
import com.example.CustomBlog.content.Content;
import com.example.CustomBlog.user.User;
import com.example.CustomBlog.web_information.Web_Information;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table
public class Asset {

    @Id
    @SequenceGenerator(
        name="aset_sq",
        sequenceName ="aset_sq",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "aset_sq"
    )
    private long id;
    private String Name;
    private String assetURL;
    @ManyToOne
    @JoinColumn(name = "content_id",nullable = true)
    @JsonBackReference
    private Content from_content;
    @ManyToOne
    @JoinColumn(name = "user_id",nullable = true)
    @JsonBackReference(value = "user_asset")
    private User from_user;
    @ManyToOne
    @JoinColumn(name = "webinfo_id",nullable = true)
    @JsonBackReference(value = "webinfo_asset")
    private Web_Information from_webinfo;
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAssetURL() {
        return assetURL;
    }

    public void setAssetURL(String assetURL) {
        this.assetURL = assetURL;
    }

    public Content getFrom_content() {
        return from_content;
    }

    public void setFrom_content(Content from_content) {
        this.from_content = from_content;
    }

    public User getFrom_user() {
        return from_user;
    }

    public void setFrom_user(User from_user) {
        this.from_user = from_user;
    }

    public Web_Information getFrom_webinfo() {
        return from_webinfo;
    }

    public void setFrom_webinfo(Web_Information from_webinfo) {
        this.from_webinfo = from_webinfo;
    }

    public Asset(String name, String assetURL) {
        Name = name;
        this.assetURL = assetURL;
    }

    public Asset() {
    }
}
