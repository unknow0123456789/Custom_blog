package com.example.CustomBlog.user;

import java.util.*;

import com.example.CustomBlog.asset.Asset;
import com.example.CustomBlog.content.Comment;
import com.example.CustomBlog.content.Content;

import com.example.CustomBlog.feedback.Feedback;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity(name = "_Users")
@Table
public class User implements UserDetails {

    @Id
    @SequenceGenerator(
        name="user_sq",
        sequenceName ="user_sq",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "user_sq"
    )
    private long id;
    private String Display_name;
    private String Description;
    @Column(
            name = "username",
            unique = true
    )
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToMany (fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "from_user")
    @JsonManagedReference(value = "user_asset")
    private List<Asset> assets;
    @OneToMany (fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "author")
    @JsonManagedReference
    private List<Content> Posts;
    @OneToMany (fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "user")
    @JsonManagedReference
    private List<Comment> Comments;
    @OneToMany (fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "user")
    @JsonManagedReference
    private List<Feedback> Feedbacks;


    public User() {
    }

    public User(String display_name, String username, String password,Role role) {
        Display_name = display_name;
        this.username =username;
        this.password =password;
        this.role=role;
    }



    public List<Feedback> getFeedbacks() {
        return Feedbacks;
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        Feedbacks = feedbacks;
    }

    public List<Comment> getComments() {
        return Comments;
    }

    public void setComments(List<Comment> comments) {
        Comments = comments;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDisplay_name() {
        return Display_name;
    }

    public void setDisplay_name(String display_name) {
        Display_name = display_name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public List<Content> getPosts() {
        return Posts;
    }

    public void setPosts(List<Content> posts) {
        Posts = posts;
    }
    public void addPost(Content newContent)
    {
        Posts.add(newContent);
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }
    public void addAssets(ArrayList<LinkedHashMap> objectList)
    {
        List<Asset> assetList=new ArrayList<>();
        for(LinkedHashMap linkedHashMap:objectList)
        {
            assetList.add(new Asset((String)linkedHashMap.get("name"),(String)linkedHashMap.get("assetURL")));
        }
        for(Asset asset:assetList)
        {
            asset.setFrom_user(this);
        }
        this.assets.addAll(assetList);
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
