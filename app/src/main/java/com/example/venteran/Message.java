package com.example.venteran;

public class Message {
    private String username;
    private String message;

    public Message(){

    }
    public Message(String nickname, String message) {
        this.username = nickname;
        this.message = message;
    }

    public String getNickname() {
        return username;
    }

    public void setNickname(String nickname) {
        this.username = nickname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
