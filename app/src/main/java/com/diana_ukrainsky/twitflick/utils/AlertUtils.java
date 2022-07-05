package com.diana_ukrainsky.twitflick.utils;

import android.content.Context;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class AlertUtils {

    public static void showToast(Context context, String message) {
        Toast.makeText (context,message,Toast.LENGTH_SHORT).show ();
    }
}
