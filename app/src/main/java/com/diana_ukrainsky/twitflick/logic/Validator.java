package com.diana_ukrainsky.twitflick.logic;

import android.content.Context;
import android.widget.ImageView;

import com.diana_ukrainsky.twitflick.R;
import com.diana_ukrainsky.twitflick.utils.AlertUtils;
import com.squareup.picasso.Picasso;

public class Validator {

    public static boolean validateUsernameRules(Context context,String username) {
        if (username.isEmpty ()) {
            AlertUtils.showToast (context , context.getString (R.string.username_empty));
            return false;
        }
        else if (username.contains (" ")) {
            AlertUtils.showToast (context, context.getString (R.string.enter_valid_username));
            return false;
        }
        return true;
    }

}
