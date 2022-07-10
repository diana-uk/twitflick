package com.diana_ukrainsky.twitflick.models;

import java.util.HashMap;

public abstract class User {
    private String name;
    private String username;
    private String email;
    private String userId;
    private String userImage;
    private HashMap<String, FriendRequestData> friendRequestsSent;
    private HashMap<String,FriendRequestData> pendingFriendRequests;
    //TODO: HashMap<String,Boolean> friends
    private HashMap<String,Boolean> friends;

    public User() {
        initFriendRequestsSent ();
        initPendingFriendRequests ();
        initFriends();
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

    private void initFriends() {
        friends=new HashMap<> ();
    }

    public void initFriendRequestsSent() {
        friendRequestsSent = new HashMap<> ();
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

    public HashMap<String,FriendRequestData> getFriendRequestsSent() {
        return friendRequestsSent;
    }

    public void setFriendRequestsSent(HashMap<String,FriendRequestData> friendRequestsSent) {
        this.friendRequestsSent = friendRequestsSent;
    }

    public HashMap<String,FriendRequestData> getPendingFriendRequests() {
        return pendingFriendRequests;
    }

    public void setPendingFriendRequests(HashMap<String,FriendRequestData> pendingFriendRequests) {
        this.pendingFriendRequests =pendingFriendRequests;
    }

    public void sendFriendRequest(FriendRequestData friendRequestData) {
        friendRequestsSent.put (friendRequestData.getUserId (),friendRequestData);
    }

//    public void addToPendingFriendRequests(GeneralUser generalUser) {
//        pendingFriendRequests.put (generalUser.getUserId (),generalUser);
//    }

    public HashMap<String, Boolean> getFriends() {
        return friends;
    }

    public User setFriends(HashMap<String, Boolean> friends) {
        this.friends = friends;
        return this;
    }
}

