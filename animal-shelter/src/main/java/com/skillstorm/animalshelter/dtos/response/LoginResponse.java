package com.skillstorm.animalshelter.dtos.response;

import java.util.List;

public class LoginResponse {

    private String accessToken;
    private UserMeResponse user;

    public LoginResponse() {
    }

    public LoginResponse(String accessToken, UserMeResponse user) {
        this.accessToken = accessToken;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public UserMeResponse getUser() {
        return user;
    }

    public void setUser(UserMeResponse user) {
        this.user = user;
    }
}
