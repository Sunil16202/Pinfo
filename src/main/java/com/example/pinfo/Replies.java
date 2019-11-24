package com.example.pinfo;

public class Replies {
    private String parent_post_id;
    private String parent_owner_id;
    private String owner_id;
    private String state;
    private String parent_nick;
    private String nick;
    private String reply_text;
    private String reply_id;

    public Replies(){}

    public Replies(String parent_post_id, String parent_owner_id, String owner_id, String state, String parent_nick, String nick, String reply_text, String reply_id) {
        this.parent_post_id = parent_post_id;
        this.parent_owner_id = parent_owner_id;
        this.owner_id = owner_id;
        this.state = state;
        this.parent_nick = parent_nick;
        this.nick = nick;
        this.reply_text = reply_text;
        this.reply_id = reply_id;
    }

    public String getParent_post_id() {
        return parent_post_id;
    }

    public void setParent_post_id(String parent_post_id) {
        this.parent_post_id = parent_post_id;
    }

    public String getParent_owner_id() {
        return parent_owner_id;
    }

    public void setParent_owner_id(String parent_owner_id) {
        this.parent_owner_id = parent_owner_id;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getParent_nick() {
        return parent_nick;
    }

    public void setParent_nick(String parent_nick) {
        this.parent_nick = parent_nick;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getReply_text() {
        return reply_text;
    }

    public void setReply_text(String reply_text) {
        this.reply_text = reply_text;
    }

    public String getReply_id() {
        return reply_id;
    }

    public void setReply_id(String reply_id) {
        this.reply_id = reply_id;
    }
}
