package com.example.cyan.object;

import cn.bmob.v3.BmobObject;

public class Chat extends BmobObject {
    private String sender;
    private String receiver;
    private String content;
    private int order;
    private boolean isRead;

    public Chat(String sender, String receiver, String content, int order, boolean isRead) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.order = order;
        this.isRead = isRead;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
