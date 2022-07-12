package com.diana_ukrainsky.twitflick.logic;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.diana_ukrainsky.twitflick.callbacks.Callback_getUser;
import com.diana_ukrainsky.twitflick.callbacks.Callback_handleSignOut;
import com.diana_ukrainsky.twitflick.callbacks.Callback_handleSignedInUser;
import com.diana_ukrainsky.twitflick.callbacks.Callback_searchUserByUsername;
import com.diana_ukrainsky.twitflick.callbacks.Callback_setFriendRequests;
import com.diana_ukrainsky.twitflick.callbacks.Callback_setMyFriends;
import com.diana_ukrainsky.twitflick.callbacks.Callback_setReviews;
import com.diana_ukrainsky.twitflick.callbacks.Callback_setUsername;
import com.diana_ukrainsky.twitflick.models.CurrentUser;
import com.diana_ukrainsky.twitflick.models.FriendRequestData;
import com.diana_ukrainsky.twitflick.models.GeneralUser;
import com.diana_ukrainsky.twitflick.models.ReviewData;
import com.diana_ukrainsky.twitflick.models.User;
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
import java.util.HashMap;
import java.util.List;
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
    private ValueEventListener userValueEventListener;


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
                .child ("pendingFriendRequests");

        myFriendsReference = databaseReference.child ("Users")
                .child (currentUser.getUsername ())
                .child ("User")
                .child (currentUser.getUserId ()).
                child ("friends");
    }

    public void userSignedOut() {
        INSTANCE = null;
    }

    public boolean isUserSignedIn() {
        if (firebaseUser != null && firebaseUser.getUid () != null) return true;
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

        setUserOnlineDB();
    }

    public void initCurrentUserFromFirebase() {
        setCurrentUserFromFirebase ();
        setUsernameFromFirebase ();
    }

    private void setUserOnlineDB() {
            HashMap<String,Object> isOnline = new HashMap<> ();
            isOnline.put ("online",true);
            usersReference
                    .child (currentUser.getUsername ())
                    .child ("User")
                    .child (currentUser.getUserId ())
                    .child ("attributes")
                    .updateChildren (isOnline);
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
                setCurrentUserListener ();
                setUserOnlineDB();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w (Constants.LOG_TAG, "Failed to read value.", error.toException ());
            }
        });
    }

    private void setCurrentUserListener() {
        getUser (firebaseUser.getUid (), currentUser.getUsername (), new Callback_getUser () {
            @Override
            public void getUser(GeneralUser generalUser) {
                if (generalUser != null) {
                    currentUser.setAttributes (generalUser.getAttributes ())
                            .setFriends (generalUser.getFriends ())
                            .setFriendRequestsSent (generalUser.getFriendRequestsSent ())
                            .setPendingFriendRequests (generalUser.getPendingFriendRequests ());
                }
            }
        });

    }


    public void handleSignedInUser(Callback_handleSignedInUser callback_handleSignedInUser) {
        databaseReference.child ("Usernames").child (firebaseUser.getUid ()).addListenerForSingleValueEvent (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uId = snapshot.getValue (String.class);
                //TODO: check if user has username too
                if (uId != null && callback_handleSignedInUser != null)
                    callback_handleSignedInUser.isUserExist (true);
                else {
                    assert callback_handleSignedInUser != null;
                    callback_handleSignedInUser.isUserExist (false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w (Constants.LOG_TAG, "Failed to read value.", error.toException ());
            }
        });
    }

    public void acceptFriendRequestDB(GeneralUser generalUser) {
        addToFriendsListDB (generalUser);
        removeFromListsDB (generalUser);
    }

    public void removeFromListsDB(GeneralUser generalUser) {
        removeFromMyPendingRequestsDB (generalUser);
        removeFromFriendsRequestsSentDB (generalUser);
    }

    public void addToFriendsListDB(GeneralUser generalUser) {
        addToMyFriendsDB (generalUser);
        addToUserFriendsDB (generalUser);
    }

    public void addFriendRequestToFirebase(FriendRequestData friendRequestSent, FriendRequestData pendingFriendRequest) {
        addToPendingFriendRequestsDB (friendRequestSent, pendingFriendRequest);
        addToFriendRequestsSentDB (friendRequestSent);
    }


    private void addToFriendRequestsSentDB(FriendRequestData friendRequestData) {
        databaseReference.child ("Users")
                .child (currentUser.getUsername ())
                .child ("User")
                .child (currentUser.getUserId ()).
                child ("friendRequestsSent")
                .setValue (DataManager.getInstance ().getFriendRequestSentByKey (friendRequestData.getUserId ()));
    }


    private void addToPendingFriendRequestsDB(FriendRequestData friendRequestSent, FriendRequestData pendingFriendRequest) {
        HashMap<String, FriendRequestData> pendingFriendRequestMap = new HashMap<> ();
        pendingFriendRequestMap.put (currentUser.getUserId (), pendingFriendRequest);
        databaseReference.child ("Users")
                .child (friendRequestSent.getUsername ())
                .child ("User")
                .child (friendRequestSent.getUserId ())
                .child ("pendingFriendRequests")
                .setValue (pendingFriendRequestMap);
    }

    private void removeFromFriendsRequestsSentDB(GeneralUser generalUser) {
        databaseReference.child ("Users")
                .child (generalUser.getUsername ())
                .child ("User")
                .child (generalUser.getUserId ()).
                child ("friendRequestsSent")
                .child (currentUser.getUserId ())
                .removeValue ();

        Log.d (Constants.LOG_TAG, "removed From Friends Requests Sent DB ");
    }

    private void removeFromMyPendingRequestsDB(GeneralUser generalUser) {
        databaseReference.child ("Users")
                .child (currentUser.getUsername ())
                .child ("User")
                .child (currentUser.getUserId ())
                .child ("pendingFriendRequests")
                .child (generalUser.getUserId ())
                .removeValue ();

        Log.d (Constants.LOG_TAG, "removed From MyPending Requests DB ");
    }

    private void addToUserFriendsDB(GeneralUser generalUser) {
        HashMap<String, Object> friend = new HashMap<> ();
        friend.put (currentUser.getUserId (), true);
        usersReference.child (generalUser.getUsername ())
                .child ("User")
                .child (generalUser.getUserId ()).
                child ("friends")
                .updateChildren (friend);

      //  increaseNumOfFriends (generalUser);
    }

    private void addToMyFriendsDB(GeneralUser generalUser) {
        HashMap<String, Object> friend = new HashMap<> ();
        friend.put (generalUser.getUserId (), true);
        databaseReference.child ("Users")
                .child (currentUser.getUsername ())
                .child ("User")
                .child (currentUser.getUserId ()).
                child ("friends")
                .updateChildren (friend);

       // increaseNumOfFriends (currentUser);
    }

    private void increaseNumOfFriends(User user) {
        int numberOfFriends = ((Number) user.getAttributes ().get ("NumberOfFriends")).intValue ();
        numberOfFriends++;
        user.getAttributes ().put("NumberOfFriends",numberOfFriends);

        usersReference
                .child (user.getUsername ())
                .child ("User")
                .child (user.getUserId ())
                .child ("attributes")
                .updateChildren (user.getAttributes ());
    }

    public void declineFriendRequestDB(GeneralUser generalUser) {
        removeFromListsDB (generalUser);
    }


    public void addReviewDataToFirebase() {
        // we are use add value event listener method
        // which is called with database reference.
        databaseReference.child ("ReviewData").child (currentUser.getUserId ()).child (UUID.randomUUID ().toString ()).setValue (DataManager.getInstance ().getReviewData ());
    }

    public void setUserOfflineDB() {
        HashMap<String,Object> isOnline = new HashMap<> ();
        isOnline.put ("online",false);
        usersReference
                .child (currentUser.getUsername ())
                .child ("User")
                .child (currentUser.getUserId ())
                .child ("attributes")
                .updateChildren (isOnline);
    }
    //****************************** Read ****************************************************************
    public void getFriendsList(Callback_setMyFriends callback_setMyFriends) {
        List<GeneralUser> friends = new ArrayList<> ();

        friendsListValueEventListener = new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren ()) {
                    String userId = dataSnapshot.getKey ();
                    //Boolean isActive = dataSnapshot.getValue (Boolean.class);
                    Log.d (Constants.LOG_TAG, "key: " + userId);

                    getUsernameFromId (userId, new Callback_setUsername () {
                        @Override
                        public void setUsername(String username) {
                            if (username != null) {
                                getUser (userId, username, new Callback_getUser () {
                                    @Override
                                    public void getUser(GeneralUser generalUser) {
                                        if (generalUser != null) {
                                            Log.d ("pttt", "getUser: ");
                                            friends.add (generalUser);
                                            if (callback_setMyFriends != null && friends.size () == snapshot.getChildrenCount ())
                                                callback_setMyFriends.setMyFriendsList (friends);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w (Constants.LOG_TAG, "Failed to read value.", error.toException ());
            }
        };
        myFriendsReference.addValueEventListener (friendsListValueEventListener);
    }

    public void getUser(String userId, String username, Callback_getUser callback_getUser) {
        userValueEventListener = new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GeneralUser generalUser = snapshot.getValue (GeneralUser.class);
                if (callback_getUser != null && generalUser != null)
                    callback_getUser.getUser (generalUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w (Constants.LOG_TAG, "Failed to read value.", error.toException ());
            }
        };
        usersReference.child (username).child ("User").child (userId).addValueEventListener (userValueEventListener);
    }

    public void getReviewsList(GeneralUser friend, Callback_setReviews callback_setReviews) {
        reviewsListValueEventListener = new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ReviewData> reviewsList = new ArrayList<> ();
                for (DataSnapshot dataSnapshot : snapshot.getChildren ()) {
                    reviewsList.add (dataSnapshot.getValue (ReviewData.class));
                }
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

    public void getFriendRequestsList(Callback_setFriendRequests callback_setFriendRequests) {
        getUsernameFromId (firebaseUser.getUid (), new Callback_setUsername () {
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
                List<FriendRequestData> friendRequestList = new ArrayList<> ();
                for (DataSnapshot dataSnapshot : snapshot.getChildren ()) {
                    friendRequestList.add (dataSnapshot.getValue (FriendRequestData.class));
                }
                if (callback_setFriendRequests != null)
                    callback_setFriendRequests.setFriendRequestsList (friendRequestList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w (Constants.LOG_TAG, "Failed to read value.", error.toException ());
            }
        };
        friendRequestsReference.addValueEventListener (friendRequestsListValueEventListener);
    }

    public void getUsernameFromId(String userID, Callback_setUsername callback_setUsername) {
        usernamesReference.child (userID).addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.getValue (String.class);
                if (username != null && callback_setUsername != null) {
                    //TODO: remove next line
                    setCurrentUserReferences ();
                    callback_setUsername.setUsername (username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w (Constants.LOG_TAG, "Failed to read value.", error.toException ());
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

    public void handleDeletedAuthUser(Context context, Callback_handleSignOut callback_handleSignOut) {
        usernamesReference.child (FirebaseAuth.getInstance ().getCurrentUser ().getUid ()).addListenerForSingleValueEvent (new ValueEventListener () {
            @Override
            public void onDataChange(@com.google.firebase.database.annotations.NotNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists ()) {
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
                Log.w (Constants.LOG_TAG, "Failed to read value.", databaseError.toException ());
            }
        });
    }

    //****************************** Firebase Storage ****************************************************************
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

    public StorageReference getNoImageStorageReference() {
        return getStorageReference ().child (Constants.STORAGE_PATH + Constants.NO_IMAGE_FILE);
    }
}

