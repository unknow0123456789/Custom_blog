package com.example.CustomBlog.Authentication;

public class ChangePasswordRequest {
    private AuthenticationRequest authenticationRequest;
    private String newPassword;

    public ChangePasswordRequest(AuthenticationRequest authenticationRequest, String newPassword) {
        this.authenticationRequest = authenticationRequest;
        this.newPassword = newPassword;
    }

    public AuthenticationRequest getAuthenticationRequest() {
        return authenticationRequest;
    }

    public void setAuthenticationRequest(AuthenticationRequest authenticationRequest) {
        this.authenticationRequest = authenticationRequest;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
