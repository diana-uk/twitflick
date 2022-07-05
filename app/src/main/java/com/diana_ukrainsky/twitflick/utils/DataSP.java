package com.diana_ukrainsky.twitflick.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class DataSP {
    private final String SHARED_PREF_KEY = "SHARED_KEY";

    private SharedPreferences sharedPreferences = null;
    //Design Pattern singleton
    private static DataSP instance;

    private DataSP(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
    }

    public static void initInstance(Context context) {
        if (instance == null) {
            instance = new DataSP(context);
        }
    }

    public static DataSP getInstance() {
        return instance;
    }

    public String getString(String KEY, String defValue) {
        return sharedPreferences.getString(KEY, defValue);
    }

    public void putString(String KEY, String value) {
        sharedPreferences.edit().putString(KEY, value).apply();
    }
    public void clearAll() {
        sharedPreferences.edit ().clear ().apply ();
    }
}
