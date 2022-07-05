package com.diana_ukrainsky.twitflick.ui;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.diana_ukrainsky.twitflick.R;
import com.diana_ukrainsky.twitflick.callbacks.Callback_handleSignOut;
import com.diana_ukrainsky.twitflick.callbacks.Callback_handleSignedInUser;
import com.diana_ukrainsky.twitflick.logic.DatabaseManager;
import com.diana_ukrainsky.twitflick.utils.AlertUtils;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SignInOptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_sign_in_options);
        findViews();
        checkIfUserSignedIn();
    }

    private void findViews() {

    }

    private List<AuthUI.IdpConfig> getSelectedProviders() {
        // Choose authentication providers
        return Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
//                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.TwitterBuilder().build());
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();

        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            DatabaseManager.getInstance().setCurrentFirebaseUser ();
            handleSignedInUser ();
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            if (response == null) {
                // User pressed back button
                AlertUtils.showToast(getApplicationContext(),getString(R.string.sign_in_cancelled));
                return;
            }

            if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                AlertUtils.showToast(getApplicationContext(),getString(R.string.no_internet_connection));
                return;
            }
            AlertUtils.showToast(getApplicationContext(),getString(R.string.unknown_error));
        }
    }

    private void startUserDetailsActivity() {
        Intent intent = new Intent (this,UserDetailsActivity.class);
        startActivity (intent);
    }

    private void checkIfUserSignedIn() {
        if (DatabaseManager.getInstance ().isUserSignedIn()) {
            handleDeletedUser ();
        }
        else
            launchSignInActivity();
    }

    private void handleDeletedUser() {
        DatabaseManager.getInstance ().handleDeletedAuthUser (getApplicationContext (), new Callback_handleSignOut () {
            @Override
            public void isSignOut(boolean isSignedOut) {
                if(isSignedOut)
                    launchSignInActivity();
                else {
                    DatabaseManager.getInstance ().initCurrentUserFromFirebase ();
                    DatabaseManager.getInstance ().setReferences ();
                    startBottomNavigationActivity();
                }
            }
        });

    }

    private void launchSignInActivity() {
// Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(getSelectedProviders())
                .setLogo (R.drawable.ic_twitflick_icon)
                .setTosAndPrivacyPolicyUrls ("https://firebase.google.com/docs/auth/android/firebaseui","")
                .build();
        signInLauncher.launch(signInIntent);
    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract (),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult> () {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

    private void startBottomNavigationActivity() {
        Intent intent = new Intent (this, BottomNavigationActivity.class);
        startActivity (intent);
    }

    private void handleSignedInUser() {
        DatabaseManager.getInstance ().handleSignedInUser (new Callback_handleSignedInUser () {
            @Override
            public void isUserExist(boolean isExist) {
                if(!isExist)
                    startUserDetailsActivity();
                else {
                    DatabaseManager.getInstance().setCurrentFirebaseUser ();
                    DatabaseManager.getInstance ().initCurrentUserFromFirebase ();
                    DatabaseManager.getInstance ().setReferences ();
                    startBottomNavigationActivity ();
                    finish();
                }
            }
        });

    }


}