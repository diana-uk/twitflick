package com.diana_ukrainsky.twitflick.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import com.diana_ukrainsky.twitflick.R;
import com.diana_ukrainsky.twitflick.logic.DataManager;
import com.diana_ukrainsky.twitflick.logic.DatabaseManager;
import com.diana_ukrainsky.twitflick.models.MovieData;
import com.diana_ukrainsky.twitflick.models.ReviewData;
import com.diana_ukrainsky.twitflick.models.CurrentUser;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class AddReviewActivity extends AppCompatActivity {
    private MaterialTextView addReview_TXT_movieName;
    private AppCompatImageView addReview_IMG_movieImage;
    private RatingBar addReview_RB_ratingBar;
    private EditText addReview_TF_reviewText;
    private ImageButton addReview_IMGBTN_postReview;

    private Bundle bundle;

    private ReviewData reviewData;
    private CurrentUser currentUser;

    // creating a variable for our
    // Firebase Database.
    FirebaseDatabase firebaseDatabase;

    // creating a variable for our Database
    // Reference for Firebase.
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_add_review);

        initData ();
        getSelectedMovie ();

        findViews ();
        initUI ();
        setListeners ();
    }

    private void initData() {
        currentUser = CurrentUser.getInstance ();
    }

    private void getSelectedMovie() {
        bundle = new Bundle ();
        bundle = getIntent ().getExtras ();
        String movieDataJson = bundle.getString ("MOVIE_DATA");
        MovieData movieData = new Gson ().fromJson (movieDataJson, MovieData.class);
        DataManager.getInstance ().setMovieData (movieData);
    }

    private void findViews() {
        addReview_TXT_movieName = findViewById (R.id.addReview_TXT_movieName);
        addReview_IMG_movieImage = findViewById (R.id.addReview_IMG_movieImage);
        addReview_RB_ratingBar = findViewById (R.id.addReview_RB_ratingBar);
        addReview_TF_reviewText = findViewById (R.id.addReview_TF_reviewText);
        addReview_IMGBTN_postReview = findViewById (R.id.addReview_IMGBTN_postReview);
    }

    private void initUI() {
        addReview_TXT_movieName.setText (DataManager.getInstance ().getMovieData ().getTitle ());
        setMovieImageUI ();
    }

    private void setListeners() {
        addReview_IMGBTN_postReview.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                String reviewText = addReview_TF_reviewText.getText ().toString ();
                float ratingBarValue = addReview_RB_ratingBar.getRating ();
                // below line is for checking weather the
                // edittext fields are empty or not.
                if (TextUtils.isEmpty (reviewText) || ratingBarValue == 0) {
                    // if the text fields are empty
                    // then show the below message.
                    Toast.makeText (AddReviewActivity.this, "Please add some data.", Toast.LENGTH_SHORT).show ();
                } else {
                    Log.d ("pttt", "genres string:  " + DataManager.getInstance ().getMovieData ().getGenre ());
                    // Init Review Data so it can be saved in firebase
                    DataManager.getInstance ().initReviewData (reviewText,ratingBarValue);

                    // Save in Shared Preferences and in firebase
                    saveReviewData ();
                    finishBelowActivities();
                }
            }
        });
        addReview_RB_ratingBar.setOnRatingBarChangeListener (new RatingBar.OnRatingBarChangeListener () {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {


            }
        });
    }

    private void finishBelowActivities() {
        SearchMovieActivity.getInstance().finish();
        finish ();
    }

    private void setMovieImageUI() {
        Picasso.get ()
                .load (DataManager.getInstance ().getMovieData ().getPoster ())
                .resize (130, 160)
                .into (addReview_IMG_movieImage);
    }



    private void saveReviewData() {
       // dataManager.saveReviewData ();
        DatabaseManager.getInstance ().addReviewDataToFirebase ();
    }


}