package com.diana_ukrainsky.twitflick.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieList {
    @SerializedName("Search")
    private List<MovieData> movies;

    @SerializedName("Response")
    private String response;

    @SerializedName("Error")
    private String error;

    private int totalResults;

    public List<MovieData> getMovies() {
        return movies;
    }

    public void setMovies(List<MovieData> movies) {
        this.movies = movies;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
