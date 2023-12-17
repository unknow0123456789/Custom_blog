package com.example.CustomBlog;

public class PutRequest {
    private long itemId;
    private String property;
    private Object value;

    public <T> PutRequest(long itemId, String property, T value) {
        this.itemId = itemId;
        this.property = property;
        this.value = value;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }


    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }
}
