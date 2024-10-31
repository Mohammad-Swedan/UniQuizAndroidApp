package com.example.myapplicationuq.Responses;

import java.util.Set;

public class LoginResponse {
    private String token;
    private Set<String> permissions;

    public String getToken() {
        return token;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

}
