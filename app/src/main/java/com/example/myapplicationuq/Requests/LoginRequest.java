package com.example.myapplicationuq.Requests;

public class LoginRequest {
    private String email;
    private String username;
    private String password;

    public LoginRequest(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

}
