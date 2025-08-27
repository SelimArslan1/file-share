package com.file_share.dto;

public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String repeatedPassword;

    public RegisterRequest() {}
    public RegisterRequest(String username, String email, String password, String repeatedPassword) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.repeatedPassword = repeatedPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRepeatedPassword() {
        return repeatedPassword;
    }

    public void setRepeatedPassword(String repeatedPassword) {
        this.repeatedPassword = repeatedPassword;
    }
}
