package com.example.CustomBlog.web_information;

import com.example.CustomBlog.asset.Asset;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table
public class Web_Information {
    @Id
    @SequenceGenerator(
            name="webinfo_sq",
            sequenceName ="webinfo_sq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "webinfo_sq"
    )
    private long id;
    private String name;

    private String content;

    @OneToMany (fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "from_webinfo")
    @JsonManagedReference(value = "webinfo_asset")
    private List<Asset> assetList;

    public Web_Information() {
    }

    public Web_Information(String name, String content, List<Asset> assetList) {
        this.name = name;
        this.content = content;
        this.assetList = assetList;
    }

    public Web_Information(String name, String content) {
        this.name = name;
        this.content = content;
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
}
