package com.diana_ukrainsky.twitflick.callbacks;

import com.diana_ukrainsky.twitflick.models.GeneralUser;
import com.diana_ukrainsky.twitflick.models.User;

import java.util.List;

public interface Callback_setFriendRequests {
    void setFriendRequestsList(List<GeneralUser> friendRequestsList);
}
