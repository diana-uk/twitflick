package com.diana_ukrainsky.twitflick.models;

import java.util.HashMap;

public abstract class User {
    private String name;
    private String username;
    private String email;
    private String userId;
    private String userImage;
    // To save in Firebase it's convenient to save HashMap<userId, FriendRequestData>
    private HashMap<String, FriendRequestData> friendRequestsSent;
    private HashMap<String,FriendRequestData> pendingFriendRequests;
    // To save friends it's convenient to save HashMap<userId,isUserActive>
    private HashMap<String,Object> friends;
     //All other attributes eg. number of friends are in Hashmap<nameOfAttribute,Object>
    private HashMap<String,Object> attributes;

    public User() {
        initFriendRequestsSent ();
        initPendingFriendRequests ();
        initFriends();
      //  initAttributes();
    }



    public void initFriendRequestsSent() {
        friendRequestsSent = new HashMap<> ();
    }

    public void initPendingFriendRequests() {
        pendingFriendRequests = new HashMap<> ();
    }

    private void initFriends() {
        friends=new HashMap<> ();
        //friends.put ("HEAD",true);
    }

    public void initAttributes() {
        attributes = new HashMap<> ();
        Integer START= 0;
        attributes.put ("NumberOfFriends", START);
        attributes.put ("online",true);
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

    public User setFriendRequestsSent(HashMap<String, FriendRequestData> friendRequestsSent) {
        this.friendRequestsSent = friendRequestsSent;
        return this;
    }

    public HashMap<String,FriendRequestData> getPendingFriendRequests() {
        return pendingFriendRequests;
    }

    public User setPendingFriendRequests(HashMap<String, FriendRequestData> pendingFriendRequests) {
        this.pendingFriendRequests = pendingFriendRequests;
        return this;
    }

    public void sendFriendRequest(FriendRequestData friendRequestData) {
        friendRequestsSent.put (friendRequestData.getUserId (),friendRequestData);
    }

//    public void addToPendingFriendRequests(GeneralUser generalUser) {
//        pendingFriendRequests.put (generalUser.getUserId (),generalUser);
//    }

    public HashMap<String, Object> getFriends() {
        return friends;
    }

    public User setFriends(HashMap<String, Object> friends) {
        this.friends = friends;
        return this;
    }

    public HashMap<String, Object> getAttributes() {
        return attributes;
    }

    public User setAttributes(HashMap<String, Object> attributes) {
        this.attributes = attributes;
        return this;
    }

}

