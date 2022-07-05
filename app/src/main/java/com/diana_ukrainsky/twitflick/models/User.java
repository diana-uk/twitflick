package com.diana_ukrainsky.twitflick.models;

import android.net.Uri;

import java.util.HashMap;

public abstract class User {
    private String name;
    private String username;
    private String email;
    private String userId;
    private String userImage;
    private HashMap<String,GeneralUser> friendRequestSent;
    private HashMap<String,GeneralUser> pendingFriendRequests;
    private HashMap<String,GeneralUser> friends;

    public User() {
        initFriendRequestsSent ();
        initPendingFriendRequests ();
        initFriends();
    }

    private void initFriends() {
        friends=new HashMap<> ();
    }

    public void initFriendRequestsSent() {
        friendRequestSent = new HashMap<> ();
    }

   public void initPendingFriendRequests() {
       pendingFriendRequests = new HashMap<> ();
   }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserId() {
        return userId;
    }

    public User setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public HashMap<String,GeneralUser> getFriendRequestSent() {
        return friendRequestSent;
    }

    public void setFriendRequestSent(HashMap<String,GeneralUser> friendRequestSent) {
        this.friendRequestSent = friendRequestSent;
    }

    public HashMap<String,GeneralUser> getPendingFriendRequests() {
        return pendingFriendRequests;
    }

    public void setPendingFriendRequests(HashMap<String,GeneralUser> pendingFriendRequests) {
        this.pendingFriendRequests =pendingFriendRequests;
    }

    public void sendFriendRequest(GeneralUser generalUser) {
        friendRequestSent.put (generalUser.getUserId (),generalUser);
    }

    public void addToPendingFriendRequests(GeneralUser generalUser) {
        pendingFriendRequests.put (generalUser.getUserId (),generalUser);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}

