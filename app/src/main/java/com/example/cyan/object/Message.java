package com.example.cyan.object;

public class Message {
    private String title;
    private String content;
    private int prompt;
    private int order;
    private String avatar;

    public Message(String title, String content, int prompt, int order, String avatar) {
        this.title = title;
        this.content = content;
        this.prompt = prompt;
        this.order = order;
        this.avatar = avatar;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPrompt() {
        return prompt;
    }

    public void setPrompt(int prompt) {
        this.prompt = prompt;
    }

    public void increasePrompt() {
        this.prompt++;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
