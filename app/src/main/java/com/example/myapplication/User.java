package com.example.myapplication;

public class User {
    private String strEmail, strName, strPassword;

    public User(String strEmail, String strName, String strPassword) {
        this.strEmail = strEmail;
        this.strName = strName;
        this.strPassword = strPassword;
    }

    public String getStrEmail() {
        return strEmail;
    }

    public void setStrEmail(String strEmail) {
        this.strEmail = strEmail;
    }

    public String getStrName() {
        return strName;
    }

    public void setStrName(String strName) {
        this.strName = strName;
    }

    public String getStrPassword() {
        return strPassword;
    }

    public void setStrPassword(String strPassword) {
        this.strPassword = strPassword;
    }
}
