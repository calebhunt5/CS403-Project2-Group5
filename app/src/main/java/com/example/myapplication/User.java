package com.example.myapplication;

public class User {
    private String strUsername, strPassword;

    public User(String strUsername, String strPassword) {
        this.strUsername = strUsername;
        this.strPassword = strPassword;
    }
    public String getStrUsername() {
        return strUsername;
    }

    public void setStrUsername(String strUsername) {
        this.strUsername = strUsername;
    }

    public String getStrPassword() {
        return strPassword;
    }

    public void setStrPassword(String strPassword) {
        this.strPassword = strPassword;
    }
}
