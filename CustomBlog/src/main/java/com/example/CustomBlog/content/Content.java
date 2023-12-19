package com.example.CustomBlog.content;

import java.text.SimpleDateFormat;
import java.util.*;

import com.example.CustomBlog.asset.Asset;
import com.example.CustomBlog.category.Category;
import com.example.CustomBlog.user.User;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;

@Entity
@Table
//@JsonIdentityInfo(
//        generator = ObjectIdGenerators.PropertyGenerator.class,
//        property = "id")
public class Content {

    @Id
    @SequenceGenerator(
        name="content_sq",
        sequenceName ="content_sq",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "content_sq"
    )
    private long id;

    private String title;
    private String description;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany (fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "from_content")
    @JsonManagedReference
    private List<Asset> assetList;
    private int views;

    @ManyToMany
    @JoinTable(
            name = "content_category",
            joinColumns = @JoinColumn(name="content_id"),
            inverseJoinColumns = @JoinColumn(name="category_id")
    )
    @JsonIdentityReference(alwaysAsId = true)
    private Set<Category> categories;

    private boolean pending;

    @ManyToOne
    @JoinColumn(name = "author_id",nullable = false)
    @JsonBackReference
    private User author;

    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "post")
    @JsonManagedReference(value = "content_comment")
    private List<Comment> comments;

    private Date lastUpload;

    public Content(
            String title,
            String description,
            String content,
            List<Asset> assetList,
            Set<Category> content_category
    )
    {
        this.title = title;
        this.description = description;
        this.content = content;
        this.assetList = assetList;
        this.categories = content_category;
    }

    public Content() {
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Asset> getAssetList() {
        return assetList;
    }

    public void setAssetList(List<Asset> assetList) {
        this.assetList = assetList;
    }
    public void addAssetList(ArrayList<LinkedHashMap> objectList)
    {
        List<Asset> assetList=new ArrayList<>();
        for(LinkedHashMap linkedHashMap:objectList)
        {
            assetList.add(new Asset((String)linkedHashMap.get("name"),(String)linkedHashMap.get("assetURL")));
        }
        for(Asset asset:assetList)
        {
            asset.setFrom_content(this);
        }
        this.assetList.addAll(assetList);
    }

    public int getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(Boolean pending) {
        this.pending = pending;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getLastUpload() {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return simpleDateFormat.format(lastUpload);
    }

    public void setLastUpload(Date lastUpload) {
        this.lastUpload = lastUpload;
    }
}
