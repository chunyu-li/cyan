package com.example.cyan.object;

import cn.bmob.v3.BmobObject;

public class Request extends BmobObject {
    private String requesterId;
    private String receiverId;
    private String requesterName;
    private String requesterAvatar;

    public Request(String requesterId, String receiverId, String requesterName, String requesterAvatar) {
        this.requesterId = requesterId;
        this.receiverId = receiverId;
        this.requesterName = requesterName;
        this.requesterAvatar = requesterAvatar;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getRequesterAvatar() {
        return requesterAvatar;
    }

    public void setRequesterAvatar(String requesterAvatar) {
        this.requesterAvatar = requesterAvatar;
    }
}
