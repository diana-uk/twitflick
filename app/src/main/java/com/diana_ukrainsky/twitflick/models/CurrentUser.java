package com.diana_ukrainsky.twitflick.models;

import java.util.List;

public class CurrentUser extends User{
    /*
        Singleton Support
         */
    public static CurrentUser instance;

    private CurrentUser() {
        super();
        // Private to prevent anyone else from instantiating
    }

    public static CurrentUser getInstance() {
        if(instance == null) {
            instance = new CurrentUser ();
        }
        return instance;
    }


}
