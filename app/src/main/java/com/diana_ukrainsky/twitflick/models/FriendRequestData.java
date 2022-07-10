package com.diana_ukrainsky.twitflick.models;

import java.util.Date;

public class FriendRequestData {
    private String userId;
    private String username;
    private Date dateSent;

    public FriendRequestData() {
    }

    public String getUserId() {
        return userId;
    }

    public FriendRequestData setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Date getDateSent() {
        return dateSent;
    }

    public FriendRequestData setDateSent(Date dateSent) {
        this.dateSent = dateSent;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public FriendRequestData setUsername(String username) {
        this.username = username;
        return this;
    }
}
