package com.example.cyan.object;

import cn.bmob.v3.BmobObject;

public class Moment extends BmobObject {
    private String avatar;
    private String user;
    private String content;
    private String photo;
    private int like;

    public Moment(String avatar, String user, String content, String photo, int like) {
        this.avatar = avatar;
        this.user = user;
        this.content = content;
        this.photo = photo;
        this.like = like;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public void increaseLike() {
        like++;
    }
}
