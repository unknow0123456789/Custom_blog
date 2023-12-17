package com.example.CustomBlog.Authentication;

public class RegisterRequest {
    private String Username;
    private String Password;
    private String DisplayName;

    public RegisterRequest(String username, String password, String displayName) {
        Username = username;
        Password = password;
        DisplayName = displayName;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }
}
