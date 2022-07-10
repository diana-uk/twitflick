package com.diana_ukrainsky.twitflick.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.diana_ukrainsky.twitflick.R;
import com.diana_ukrainsky.twitflick.callbacks.Callback_handleSignedInUser;
import com.diana_ukrainsky.twitflick.logic.DataManager;
import com.diana_ukrainsky.twitflick.logic.DatabaseManager;
import com.diana_ukrainsky.twitflick.logic.Validator;
import com.diana_ukrainsky.twitflick.utils.AlertUtils;
import com.diana_ukrainsky.twitflick.utils.Constants;
import com.diana_ukrainsky.twitflick.utils.ImageUtils;
import com.google.android.material.button.MaterialButton;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetailsActivity extends AppCompatActivity {
    private EditText userDetails_TF_username;
    private MaterialButton userDetails_BTN_next;
    private MaterialButton userDetails_BTN_uploadImage;
    private CircleImageView userDetails_CIMG_userCircularImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_user_details);

        findViews ();
        setListeners ();
    }

    private void findViews() {
        userDetails_TF_username = findViewById (R.id.userDetails_TF_username);
        userDetails_BTN_next = findViewById (R.id.userDetails_BTN_next);
        userDetails_BTN_uploadImage = findViewById (R.id.userDetails_BTN_uploadImage);
        userDetails_CIMG_userCircularImage = findViewById (R.id.userDetails_CIMG_userCircularImage);
    }


    private void setListeners() {
        userDetails_BTN_next.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                handleUsernameExists ();
                //TODO: check if only letters and else
            }
        });
        userDetails_BTN_uploadImage.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                imageChooserUI ();
            }
        });
    }

    private void startBottomNavigationActivity() {
        Intent intent = new Intent (this, BottomNavigationActivity.class);
        startActivity (intent);
        finish ();
    }

    private void handleUsernameExists() {
        DatabaseManager.getInstance ().handleSignedInUser (new Callback_handleSignedInUser () {
            @Override
            public void isUserExist(boolean isExist) {
                Log.d (Constants.LOG_TAG, "isUserExist before: " + isExist);
                if (isExist) {
                    AlertUtils.showToast (getApplicationContext (), getString (R.string.username_exists));
                }
                else if(Validator.validateUsernameRules (getApplicationContext (), userDetails_TF_username.getText ().toString ())) {
                    DatabaseManager.getInstance ().setReferences ();
                    saveCurrentUserData ();
                    DatabaseManager.getInstance ().setReferences ();
                    saveCurrentUserData ();
                    startBottomNavigationActivity ();
                    finish ();
                }
            }
        });
    }

    private void saveCurrentUserData() {
        DataManager.getInstance ().saveCurrentUserData (userDetails_TF_username.getText ().toString ());
        DatabaseManager.getInstance ().saveCurrentUserDataInFirebase ();
        DatabaseManager.getInstance ().setCurrentUserReferences ();
    }

    private void imageChooserUI() {
        Intent intent = new Intent ();
        intent.setType ("image/*");
        intent.setAction (Intent.ACTION_GET_CONTENT);

        pickFromGalleryActivity.launch (intent);
    }

    ActivityResultLauncher<Intent> pickFromGalleryActivity = registerForActivityResult (
            new ActivityResultContracts.StartActivityForResult (),
            result -> {
                if (result.getResultCode () == Activity.RESULT_OK) {
                    Intent data = result.getData ();
                    if (data != null && data.getData () != null) {
                        Uri selectedImageUri = data.getData ();
                        Bitmap selectedImageBitmap = ImageUtils.setImageBitmapFromUriUI (getApplicationContext (), selectedImageUri);
                        userDetails_CIMG_userCircularImage.setImageBitmap (selectedImageBitmap);
                        DatabaseManager.getInstance ().uploadFileToCloudStorage (this, selectedImageUri);
                    }
                }
            });
}