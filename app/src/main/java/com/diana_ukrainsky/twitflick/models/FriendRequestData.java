package com.diana_ukrainsky.twitflick.models;

public class FriendRequestData {
    User userRequested;

    public FriendRequestData(GeneralUser userRequested) {
        this.userRequested = userRequested;
    }

    public FriendRequestData() {
        userRequested = new GeneralUser ();
    }

    public String getUserName() {
        return userRequested.getName ();
    }

    public String getUserId() {
        return userRequested.getUserId ();
    }

    public String getUserImage() {
        return userRequested.getUserImage ();
    }

    public void setUserRequested(User userRequested) {
        this.userRequested = userRequested;
    }
}
