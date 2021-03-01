package com.example.cyan.object;

import cn.bmob.v3.BmobObject;

public class User extends BmobObject {
    private String username;
    private String password;
    private String avatar;
    private String email;

    public User(String username, String password, String avatar, String email) {
        this.username = username;
        this.password = password;
        this.avatar = avatar;
        this.email = email;
    }

    public User() {
        this.avatar = "";
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
