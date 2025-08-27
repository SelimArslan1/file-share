package com.file_share.dto;

public class AuthResponse {
    private String email;
    private String jwt;


    public AuthResponse() {}
    public AuthResponse(String email, String jwt) {
        this.email = email;
        this.jwt = jwt;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getJwt() { return jwt; }
    public void setJwt(String jwt) { this.jwt = jwt; }

}