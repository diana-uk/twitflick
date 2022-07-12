package com.diana_ukrainsky.twitflick.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.diana_ukrainsky.twitflick.R;
import com.diana_ukrainsky.twitflick.logic.DatabaseManager;
import com.diana_ukrainsky.twitflick.models.GeneralUser;
import com.diana_ukrainsky.twitflick.utils.Constants;
import com.diana_ukrainsky.twitflick.utils.ImageUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

public class FriendDetailsFragment extends Fragment {

    private MaterialTextView fragmentFriendDetails_TXT_username;
    private ImageView fragmentFriendDetails_CIMG_userCircularImage;
    private MaterialTextView fragmentFriendDetails_TXT_isActive;
    private ImageView fragmentFriendDetails_IMG_isActive;

    private View view;

    private static final String ARG_PARAM1 = "param1";
    private String mParam1;

    public FriendDetailsFragment() {
        // Required empty public constructor
    }

    public static FriendDetailsFragment newInstance(String param1) {
        FriendDetailsFragment fragment = new FriendDetailsFragment ();
        Bundle args = new Bundle ();
        args.putString (ARG_PARAM1, param1);
        fragment.setArguments (args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        if (getArguments () != null) {
            mParam1 = getArguments ().getString (ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate (R.layout.fragment_friend_details, container, false);
        findViews (view);
        getGeneralUser ();
        return view;
    }

    private void getGeneralUser() {
        GeneralUser generalUser = new Gson ().fromJson (mParam1, GeneralUser.class);
        fragmentFriendDetails_TXT_username.setText (generalUser.getUsername ());
        if ((Boolean) generalUser.getAttributes ().get ("online")) {
            fragmentFriendDetails_IMG_isActive.setImageResource (R.drawable.ic_online);
            fragmentFriendDetails_TXT_isActive.setText ("online");
        } else {
            fragmentFriendDetails_IMG_isActive.setImageResource (R.drawable.ic_offline);
            fragmentFriendDetails_TXT_isActive.setText ("offline");
        }


        setUserImageUI (generalUser.getUserId (), fragmentFriendDetails_CIMG_userCircularImage);
    }

    private void findViews(View view) {
        fragmentFriendDetails_TXT_username = view.findViewById (R.id.fragmentFriendDetails_TXT_username);
        fragmentFriendDetails_CIMG_userCircularImage = view.findViewById (R.id.fragmentFriendDetails_CIMG_userCircularImage);
        fragmentFriendDetails_TXT_isActive = view.findViewById (R.id.fragmentFriendDetails_TXT_isActive);
        fragmentFriendDetails_IMG_isActive = view.findViewById (R.id.fragmentFriendDetails_IMG_isActive);
    }

    private void setUserImageUI(String userId, ImageView userItem_cimg_userCircularImage) {
        StorageReference userStorageReference = DatabaseManager.getInstance ().getStorageReference ().child (Constants.STORAGE_PATH + userId);
        userStorageReference.getDownloadUrl ().addOnSuccessListener (new OnSuccessListener<Uri> () {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                if (getContext () == null) {
                    return;
                }
                ImageUtils.setImageUI (getContext (), uri, userItem_cimg_userCircularImage);

            }
        }).addOnFailureListener (new OnFailureListener () {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // File not found
                setNoImageUI (userItem_cimg_userCircularImage);
            }
        });
    }

    private void setNoImageUI(ImageView userItem_CIMG_userCircularImage) {
        userItem_CIMG_userCircularImage.setImageResource (R.drawable.ic_no_picture);
    }
}