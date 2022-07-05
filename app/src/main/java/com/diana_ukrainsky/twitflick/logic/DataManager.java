package com.diana_ukrainsky.twitflick.logic;

import com.diana_ukrainsky.twitflick.data.ReviewsDao;
import com.diana_ukrainsky.twitflick.models.GeneralUser;
import com.diana_ukrainsky.twitflick.models.Genre;
import com.diana_ukrainsky.twitflick.models.MovieData;
import com.diana_ukrainsky.twitflick.models.ReviewData;
import com.diana_ukrainsky.twitflick.models.CurrentUser;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DataManager {
    public static DataManager INSTANCE = null;

    private MovieData movieData;
    private ReviewData reviewData;
    private CurrentUser currentUser;
    private GeneralUser generalUser;

    private ReviewsDao reviewsDao;


    private DataManager() {
        movieData = new MovieData ();
        reviewData = new ReviewData ();
        reviewsDao = new ReviewsDao ();
        generalUser = new GeneralUser ();
        currentUser = CurrentUser.getInstance ();
    }

    public static DataManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataManager ();
        }
        return INSTANCE;
    }

    public MovieData getMovieData() {
        return movieData;
    }

    public void setMovieData(MovieData movieData) {
        this.movieData = movieData;
    }

    public ReviewData getReviewData() {
        return reviewData;
    }

    public void saveReviewData() {
        // Read all user reviews
        List<ReviewData> reviews = reviewsDao.readReviews ();
        // Add the new record
        reviews.add (reviewData);
        // Save all records
        reviewsDao.saveReviews (reviews);
    }

    public void saveCurrentUserData(String username) {
        currentUser.setUsername (username);
        currentUser.setUserId (DatabaseManager.getInstance ().getFirebaseUser ().getUid ());
        currentUser.setName (DatabaseManager.getInstance ().getFirebaseUser ().getDisplayName ());
        currentUser.setEmail (DatabaseManager.getInstance ().getFirebaseUser ().getEmail ());
    }

    public void initReviewData(String reviewText, float ratingBarValue) {
        movieData = DataManager.getInstance ().getMovieData ();
        List<String> genresList = Arrays.asList (movieData.getGenre ().split (",", -1));

        for (String genreStr : genresList)
            DataManager.getInstance ().getReviewData ().getGenresList ().add (new Genre (genreStr));

        DataManager.getInstance ().getReviewData ().setMovieName (movieData.getTitle ())
                .setMovieDate (movieData.getReleaseDate ())
                .setMovieImageUrl (movieData.getPoster ())
                .setRating (ratingBarValue)
                .setReviewText (reviewText)
                .setDate (new Date ())
                .setUserID (DatabaseManager.getInstance ().getFirebaseUser ().getUid ());
    }

    public boolean checkIfRequestSent(GeneralUser generalUser) {
        if (!currentUser.getFriendRequestSent ().containsKey (generalUser.getUserId ()))
            return false;

        return true;
    }
}

