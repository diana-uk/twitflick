package com.diana_ukrainsky.twitflick.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReviewData {
    private String userID;
    private String movieName;
    private String movieDate;
    private String reviewText;
    private String movieImageUrl;
    private float rating;
    private List<Genre> genresList;
    private Date date;

    public ReviewData() {
       date = new Date ();
        genresList = new ArrayList<> ();
    }

    public String getMovieName() {
        return movieName;
    }

    public ReviewData setMovieName(String movieName) {
        this.movieName = movieName;
        return this;
    }

    public String getMovieDate() {
        return movieDate;
    }

    public ReviewData setMovieDate(String movieDate) {
        this.movieDate = movieDate;
        return this;
    }

    public String getReviewText() {
        return reviewText;
    }

    public ReviewData setReviewText(String reviewText) {
        this.reviewText = reviewText;
        return this;
    }

    public String getMovieImageUrl() {
        return movieImageUrl;
    }

    public ReviewData setMovieImageUrl(String movieImageUrl) {
        this.movieImageUrl = movieImageUrl;
        return this;
    }

    public float getRating() {
        return rating;
    }

    public ReviewData setRating(float rating) {
        this.rating = rating;
        return this;
    }

    public List<Genre> getGenresList() {
        return genresList;
    }

    public ReviewData setGenresList(List<Genre> genresList) {
        this.genresList = genresList;
        return this;
    }

    public String getUserID() {
        return userID;
    }

    public ReviewData setUserID(String userID) {
        this.userID = userID;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public ReviewData setDate(Date date) {
        this.date = date;
        return this;
    }
}
