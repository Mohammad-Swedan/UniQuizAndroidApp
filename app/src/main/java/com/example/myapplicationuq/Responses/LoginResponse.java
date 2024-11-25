package com.example.myapplicationuq.Responses;

import java.io.Serializable;
import java.util.Set;

public class LoginResponse implements Serializable {
    private String token;
    private Set<String> permissions;

    public String getToken() {
        return token;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

}
