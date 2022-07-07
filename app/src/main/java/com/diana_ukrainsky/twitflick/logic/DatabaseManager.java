package com.diana_ukrainsky.twitflick.logic;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.diana_ukrainsky.twitflick.callbacks.Callback_handleSignOut;
import com.diana_ukrainsky.twitflick.callbacks.Callback_handleSignedInUser;
import com.diana_ukrainsky.twitflick.callbacks.Callback_searchUserByUsername;
import com.diana_ukrainsky.twitflick.callbacks.Callback_setFriendRequests;
import com.diana_ukrainsky.twitflick.callbacks.Callback_setMyFriends;
import com.diana_ukrainsky.twitflick.callbacks.Callback_setReviews;
import com.diana_ukrainsky.twitflick.callbacks.Callback_setUsername;
import com.diana_ukrainsky.twitflick.models.CurrentUser;
import com.diana_ukrainsky.twitflick.models.GeneralUser;
import com.diana_ukrainsky.twitflick.models.Genre;
import com.diana_ukrainsky.twitflick.models.MovieData;
import com.diana_ukrainsky.twitflick.models.ReviewData;
import com.diana_ukrainsky.twitflick.ui.BottomNavigationActivity;
import com.diana_ukrainsky.twitflick.ui.SignInOptionsActivity;
import com.diana_ukrainsky.twitflick.utils.AlertUtils;
import com.diana_ukrainsky.twitflick.utils.Constants;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class DatabaseManager {
    public static DatabaseManager INSTANCE = null;
    private final FirebaseDatabase database;
    private DatabaseReference databaseReference;

    // Instance for firebase storage and StorageReference
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private FirebaseUser firebaseUser;
    private CurrentUser currentUser;

    private DatabaseReference reviewsReference;
    private DatabaseReference friendRequestsReference;
    private DatabaseReference myFriendsReference;
    private DatabaseReference usersReference;
    private DatabaseReference usernamesReference;

    private ValueEventListener reviewsListValueEventListener;
    private ValueEventListener friendRequestsListValueEventListener;
    private ValueEventListener friendsListValueEventListener;


    private DatabaseManager() {
        database = FirebaseDatabase.getInstance ();
        setDatabaseReference ();
        setReferences ();
        setStorageReference ();
        setCurrentFirebaseUser ();
        setCurrentUser ();
    }

    public static DatabaseManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DatabaseManager ();
        }
        return INSTANCE;
    }

    private void setDatabaseReference() {
        databaseReference = database.getReference ("AppData");
    }

    public void setReferences() {
        reviewsReference = databaseReference.child ("ReviewData");
        usersReference = databaseReference.child ("Users");
        usernamesReference = databaseReference.child ("Usernames");
    }

    public void setStorageReference() {
        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance ();
        storageReference = storage.getReference ();
    }

    public StorageReference getStorageReference() {
        return storageReference;
    }

    public void setCurrentFirebaseUser() {
        firebaseUser = FirebaseAuth.getInstance ().getCurrentUser ();
    }

    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    private void setCurrentUser() {
        currentUser = CurrentUser.getInstance ();
    }

    public void setCurrentUserReferences() {
        friendRequestsReference = databaseReference.child ("Users")
                .child (CurrentUser.getInstance ().getUsername ())
                .child ("User")
                .child (CurrentUser.getInstance ().getUserId ())
                .child ("PendingFriendRequests");

        myFriendsReference = databaseReference.child ("Users")
                .child (currentUser.getUsername ())
                .child ("User")
                .child (currentUser.getUserId ()).
                child ("Friends");
    }

    public void userSignedOut() {
        INSTANCE = null;
    }

    public boolean isUserSignedIn() {
        if (firebaseUser != null && firebaseUser.getUid ()!=null) return true;
        return false;
    }

    //****************************** Write ****************************************************************
    public void saveCurrentUserDataInFirebase() {
        databaseReference.child ("Users")
                .child (currentUser.getUsername ())
                .child ("User")
                .child (currentUser.getUserId ())
                .setValue (currentUser);

        databaseReference.child ("Usernames")
                .child (currentUser.getUserId ())
                .setValue (currentUser.getUsername ());
    }

    public void initCurrentUserFromFirebase() {
        setCurrentUserFromFirebase ();
        setUsernameFromFirebase ();
    }

    private void setCurrentUserFromFirebase() {
        currentUser.setUserId (firebaseUser.getUid ());
        currentUser.setName (firebaseUser.getDisplayName ());
        currentUser.setEmail (firebaseUser.getEmail ());
    }

    private void setUsernameFromFirebase() {
        usernamesReference.child (firebaseUser.getUid ()).addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUser.setUsername (snapshot.getValue (String.class));
                setCurrentUserReferences ();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void handleSignedInUser(Callback_handleSignedInUser callback_handleSignedInUser) {
        databaseReference.child ("Usernames").child (firebaseUser.getUid ()).addListenerForSingleValueEvent (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uId = snapshot.getValue (String.class);
                //TODO: check if user has username too
                if (uId != null && callback_handleSignedInUser!=null)
                    callback_handleSignedInUser.isUserExist (true);
                else {
                    assert callback_handleSignedInUser != null;
                    callback_handleSignedInUser.isUserExist (false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void acceptFriendRequestDB(GeneralUser friendRequestItem) {
        addToFriendsListDB (friendRequestItem);
        removeFromListsDB (friendRequestItem);
    }

    private void removeFromListsDB(GeneralUser friendRequestItem) {
        removeFromMyPendingRequestsDB (friendRequestItem);
        removeFromFriendsRequestsSentDB (friendRequestItem);
    }

    private void addToFriendsListDB(GeneralUser friendRequestItem) {
        addToMyFriendsDB (friendRequestItem);
        addToUserFriendsDB (friendRequestItem);
    }
    private void addFriendRequestToFirebase(GeneralUser generalUserList) {
        addToPendingFriendRequestsDB (generalUserList);
        addToFriendRequestsSentDB (generalUserList);

    }

    public void sendFriendRequest(GeneralUser generalUserList) {
        currentUser.sendFriendRequest (generalUserList);
        addFriendRequestToFirebase (generalUserList);
    }


    private void addToFriendRequestsSentDB(GeneralUser generalUserItem) {
        databaseReference.child ("Users")
                .child (currentUser.getUsername ())
                .child("User")
                .child (currentUser.getUserId ()).
                child ("FriendRequestsSent")
                .child(generalUserItem.getUserId ())
                .setValue (generalUserItem);
    }


    private void addToPendingFriendRequestsDB(GeneralUser generalUserItem) {
        databaseReference.child ("Users")
                .child (generalUserItem.getUsername ())
                .child ("User")
                .child (generalUserItem.getUserId ())
                .child ("PendingFriendRequests")
                .child (currentUser.getUserId ())
                .setValue (currentUser);
    }

    private void removeFromFriendsRequestsSentDB(GeneralUser friendRequestItem) {
        databaseReference.child ("Users")
                .child (friendRequestItem.getUsername ())
                .child ("User")
                .child (friendRequestItem.getUserId ()).
                child ("FriendRequestsSent")
                .child (currentUser.getUserId ())
                .removeValue ();

        Log.d ("pttt", "removed From Friends Requests Sent DB ");
    }

    private void removeFromMyPendingRequestsDB(GeneralUser friendRequestItem) {
        databaseReference.child ("Users")
                .child (currentUser.getUsername ())
                .child ("User")
                .child (currentUser.getUserId ())
                .child ("PendingFriendRequests")
                .child (friendRequestItem.getUserId ())
                .removeValue ();

        Log.d (Constants.LOG_TAG, "removed From MyPending Requests DB ");
    }

    private void addToUserFriendsDB(GeneralUser friendRequestItem) {
        usersReference.child (friendRequestItem.getUsername ())
                .child ("User")
                .child (friendRequestItem.getUserId ()).
                child ("Friends")
                .child (currentUser.getUserId ()).setValue (currentUser);
    }

    private void addToMyFriendsDB(GeneralUser friendRequestItem) {
        databaseReference.child ("Users")
                .child (currentUser.getUsername ())
                .child ("User")
                .child (currentUser.getUserId ()).
                child ("Friends")
                .child (friendRequestItem.getUserId ())
                .setValue (friendRequestItem);
    }

    public void declineFriendRequestDB(GeneralUser friendRequestItem) {
        removeFromListsDB (friendRequestItem);
    }


    public void addReviewDataToFirebase() {
        // we are use add value event listener method
        // which is called with database reference.
        databaseReference.child ("ReviewData").child (currentUser.getUserId ()).child (UUID.randomUUID ().toString ()).setValue (DataManager.getInstance ().getReviewData ());
    }


    //****************************** Read ****************************************************************
    public void getFriendsList(Callback_setMyFriends callback_setMyFriends) {
        friendsListValueEventListener = new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<GeneralUser> myFriendsList = new ArrayList<> ();
                for (DataSnapshot dataSnapshot : snapshot.getChildren ()) {
                    myFriendsList.add (dataSnapshot.getValue (GeneralUser.class));
                }
                //TODO: sort by date
                if (callback_setMyFriends != null) {
                    callback_setMyFriends.setMyFriendsList (myFriendsList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w (Constants.LOG_TAG, "Failed to read value.", error.toException ());
            }
        };
        myFriendsReference.addValueEventListener (friendsListValueEventListener);
    }



    public void getReviewsList(List<GeneralUser> myFriendsList, Callback_setReviews callback_setReviews) {
        for (GeneralUser friend : myFriendsList) {
            reviewsListValueEventListener = new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<ReviewData> reviewsList = new ArrayList<> ();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren ()) {
                        reviewsList.add (dataSnapshot.getValue (ReviewData.class));
                    }
                    reviewsList.sort ((o1, o2) -> o1.getDate ().compareTo (o2.getDate ()));

                    //TODO: sort by date
                    if (callback_setReviews != null)
                        callback_setReviews.setReviewList (reviewsList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w (Constants.LOG_TAG, "Failed to read value.", error.toException ());
                }
            };
            reviewsReference.child (friend.getUserId ()).addValueEventListener (reviewsListValueEventListener);
        }
    }

    public void getFriendRequestsList(Callback_setFriendRequests callback_setFriendRequests) {
        getUsernameFromId (firebaseUser.getUid (),new Callback_setUsername () {
            @Override
            public void setUsername(String username) {
                setCurrentUserReferences ();
                getFriendRequestsWithUsername (callback_setFriendRequests);
            }
        });
    }

    private void getFriendRequestsWithUsername(Callback_setFriendRequests callback_setFriendRequests) {
        friendRequestsListValueEventListener = new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<GeneralUser> friendRequestsList = new ArrayList<> ();
                for (DataSnapshot dataSnapshot : snapshot.getChildren ()) {
                    friendRequestsList.add (dataSnapshot.getValue (GeneralUser.class));
                }
                if (callback_setFriendRequests != null)
                    callback_setFriendRequests.setFriendRequestsList (friendRequestsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w (Constants.LOG_TAG, "Failed to read value.", error.toException ());
            }
        };
        friendRequestsReference.addValueEventListener (friendRequestsListValueEventListener);
    }


    public void getUsernameFromId(String userID,Callback_setUsername callback_setUsername) {
        usernamesReference.child (userID).addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.getValue (String.class);
                if (username != null && callback_setUsername != null) {
                    setCurrentUserReferences ();
                    callback_setUsername.setUsername (username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w ("pttt", "Failed to read value.", error.toException ());
            }
        });
    }

    public void getUsersListByUsername(String username, Callback_searchUserByUsername callback_searchUserByUsername) {
        usersReference.child (username).child ("User").addListenerForSingleValueEvent (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<GeneralUser> userList = new ArrayList<> ();
                for (DataSnapshot snapshot : dataSnapshot.getChildren ()) {
                    GeneralUser generalUser = snapshot.getValue (GeneralUser.class);
                    if (!generalUser.getUserId ().equals (DatabaseManager.getInstance ().firebaseUser.getUid ()))
                        userList.add (generalUser);
                }
                if (userList.size () != 0 && callback_searchUserByUsername != null)
                    callback_searchUserByUsername.setUsersList (userList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w (Constants.LOG_TAG, "Failed to read value.", error.toException ());
            }
        });
    }

    public void uploadFileToCloudStorage(Context context, Uri selectedImageUri) {
        if (selectedImageUri != null) {
            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog (context);
            progressDialog.setTitle ("Uploading...");
            progressDialog.show ();
            // Defining the child of storageReference
            StorageReference uploadReference =
                    storageReference.child (Constants.STORAGE_PATH + firebaseUser.getUid ());

            // adding listeners on upload
            // or failure of image
            uploadReference.putFile (selectedImageUri)
                    .addOnSuccessListener (new OnSuccessListener<UploadTask.TaskSnapshot> () {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Image uploaded successfully
                            // Dismiss dialog
                            progressDialog.dismiss ();
                            AlertUtils.showToast (context, "Image Uploaded!!");
                        }
                    })

                    .addOnFailureListener (new OnFailureListener () {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Error, Image not uploaded
                            progressDialog.dismiss ();
                            AlertUtils.showToast (context, "Failed " + e.getMessage ());
                        }
                    })
                    .addOnProgressListener (new OnProgressListener<UploadTask.TaskSnapshot> () {
                        // Progress Listener for loading
                        // percentage on the dialog box
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred () / taskSnapshot.getTotalByteCount ());
                            progressDialog.setMessage ("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }


    public void handleDeletedAuthUser(Context context,Callback_handleSignOut callback_handleSignOut) {
        usernamesReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid ()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@com.google.firebase.database.annotations.NotNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    AuthUI.getInstance ()
                            .signOut (context)
                            .addOnCompleteListener (new OnCompleteListener<Void> () {
                                public void onComplete(@NonNull Task<Void> task) {
                                    // user is now signed out
                                    DatabaseManager.getInstance ().userSignedOut ();
                                    callback_handleSignOut.isSignOut (true);
                                }
                            });
                    // user has sign out
                } else {
                    callback_handleSignOut.isSignOut (false);
                    // user still logged in
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    public StorageReference getNoImageStorageReference() {
        return getStorageReference ().child (Constants.STORAGE_PATH + Constants.NO_IMAGE_FILE);
    }
}

