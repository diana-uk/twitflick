package com.diana_ukrainsky.twitflick.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.diana_ukrainsky.twitflick.R;
import com.diana_ukrainsky.twitflick.callbacks.Callback_setUsername;
import com.diana_ukrainsky.twitflick.databinding.FragmentUserFeedBinding;
import com.diana_ukrainsky.twitflick.logic.DatabaseManager;
import com.diana_ukrainsky.twitflick.ui.BottomNavigationActivity;
import com.diana_ukrainsky.twitflick.ui.SearchUserActivity;
import com.diana_ukrainsky.twitflick.ui.SignInOptionsActivity;
import com.diana_ukrainsky.twitflick.utils.Constants;
import com.diana_ukrainsky.twitflick.utils.ImageUtils;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFeedFragment extends Fragment {
    private ImageButton fragmentUserFeed_IMGBTN_addFriend;
    private ImageButton fragmentUserFeed_IMGBTN_exit;
    private CircleImageView fragmentUserFeed_CIMG_userCircularImage;
    private MaterialButton fragmentUserFeed_BTN_uploadImage;
    private MaterialTextView fragmentUserFeed_TXT_username;

    private FragmentUserFeedBinding binding;
    private View view;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserFeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFeedFragment newInstance(String param1, String param2) {
        UserFeedFragment fragment = new UserFeedFragment ();
        Bundle args = new Bundle ();
        args.putString (ARG_PARAM1, param1);
        args.putString (ARG_PARAM2, param2);
        fragment.setArguments (args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        if (getArguments () != null) {
            mParam1 = getArguments ().getString (ARG_PARAM1);
            mParam2 = getArguments ().getString (ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate (inflater, R.layout.fragment_user_feed, container, false);
        view = binding.getRoot ();

        findViews ();
        setListeners ();
        setViewUI ();

        return view;
    }

    private void setViewUI() {
        setNoImageUI ();
        setUserImageUI ();
        setUsernameUI ();
    }

    private void setNoImageUI() {
        ImageUtils.setImageUI (getContext (), DatabaseManager.getInstance ().getNoImageStorageReference (), fragmentUserFeed_CIMG_userCircularImage);
    }

    private void setUsernameUI() {
        DatabaseManager.getInstance ().getUsernameFromId (DatabaseManager.getInstance ().getFirebaseUser ().getUid (),new Callback_setUsername () {
            @Override
            public void setUsername(String username) {
                if (username != null)
                    fragmentUserFeed_TXT_username.setText (username);
                else
                    fragmentUserFeed_TXT_username.setText (getString (R.string.no_username));
            }
        });
    }

    private void setUserImageUI() {
        String userUID = DatabaseManager.getInstance ().getFirebaseUser ().getUid ();
        StorageReference userStorageReference = DatabaseManager.getInstance ().getStorageReference ().child (Constants.STORAGE_PATH + userUID);
        userStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri> () {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                if (getActivity() == null) {
                    return;
                }
                ImageUtils.setImageUI (getActivity (),userStorageReference,fragmentUserFeed_CIMG_userCircularImage);

            }
        }).addOnFailureListener(new OnFailureListener () {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // File not found
                setNoImageUI();
            }
        });
    }

    private void setListeners() {
        fragmentUserFeed_IMGBTN_addFriend.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                startSearchUserActivity ();
            }
        });
        fragmentUserFeed_IMGBTN_exit.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                signOutClicked ();
            }
        });

        fragmentUserFeed_BTN_uploadImage.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                imageChooserUI ();
            }
        });
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
                        Bitmap selectedImageBitmap = ImageUtils.setImageBitmapFromUriUI (getContext (), selectedImageUri);
                        fragmentUserFeed_CIMG_userCircularImage.setImageBitmap (selectedImageBitmap);
                        DatabaseManager.getInstance ().uploadFileToCloudStorage (getContext (), selectedImageUri);
                    }
                }
            });

    private void signOutClicked() {
        AuthUI.getInstance ()
                .signOut (getContext ())
                .addOnCompleteListener (new OnCompleteListener<Void> () {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        DatabaseManager.getInstance ().userSignedOut ();
                        startActivity (new Intent (getActivity ().getApplicationContext (), SignInOptionsActivity.class));
                        BottomNavigationActivity.getInstance ().finish ();
                        getActivity ().finish ();
                    }
                });
    }

    private void startSearchUserActivity() {
        Intent intent = new Intent (getContext (), SearchUserActivity.class);
        startActivity (intent);
    }

    private void findViews() {
        fragmentUserFeed_IMGBTN_addFriend = view.findViewById (R.id.fragmentUserFeed_IMGBTN_addFriend);
        fragmentUserFeed_IMGBTN_exit = view.findViewById (R.id.fragmentUserFeed_IMGBTN_exit);
        fragmentUserFeed_CIMG_userCircularImage = view.findViewById (R.id.fragmentUserFeed_CIMG_userCircularImage);
        fragmentUserFeed_BTN_uploadImage = view.findViewById (R.id.fragmentUserFeed_BTN_uploadImage);
        fragmentUserFeed_TXT_username = view.findViewById (R.id.fragmentUserFeed_TXT_username);
    }
}