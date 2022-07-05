package com.diana_ukrainsky.twitflick.ui;

import android.app.Application;

import com.diana_ukrainsky.twitflick.utils.DataSP;

//ALL the app itself
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate ();
        //Here i do init to shared preferences
        DataSP.initInstance (this);
    }
}
