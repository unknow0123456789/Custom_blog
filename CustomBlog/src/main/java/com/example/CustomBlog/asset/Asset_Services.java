package com.example.CustomBlog.asset;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class Asset_Services {
    private final Asset_Repository asset_Repository;

    public Asset_Services(Asset_Repository asset_Repository) {
        this.asset_Repository = asset_Repository;
    }
    
    public List<Asset> getFullAssets()
    {
        return asset_Repository.findAll();
    }
    public void addAsset(Asset newAsset)
    {
        asset_Repository.save(newAsset);
    }
    public void addAssets(List<Asset> newAssets)
    {
        asset_Repository.saveAll(newAssets);
    }
}
