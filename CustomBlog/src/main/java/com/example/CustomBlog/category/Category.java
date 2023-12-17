package com.example.CustomBlog.category;

import java.util.Set;

import com.example.CustomBlog.content.Content;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;

@Entity
@Table
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Category {

    @Id
    @SequenceGenerator(
        name="cate_sq",
        sequenceName ="cate_sq",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "cate_sq"
    )
    private long id;

    @Column(
            name = "name",
            unique = true)
    private String name;


    @ManyToMany(mappedBy = "categories")
    private Set<Content> IncludedContents;

    

    public Category(String name) {
        this.name = name;
    }
    public Category(long id) {
        this.id=id;
    }

    public Category() {
    }

    public long getId() {
        return id;
    }




    public void setId(long id) {
        this.id = id;
    }




    public String getName() {
        return name;
    }




    public void setName(String name) {
        this.name = name;
    }

    public Set<Content> getIncludedContents() {
        return IncludedContents;
    }

    public void setIncludedContents(Set<Content> includedContents) {
        IncludedContents = includedContents;
    }

    public void addIncludedContent(Content content)
    {
        IncludedContents.add(content);
    }
}
