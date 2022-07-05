package com.diana_ukrainsky.twitflick.models;

public class Genre {

    public static final String HASHTAG = "#";

    private String genre;

    public Genre() {
    }

    public Genre(String genre) {
        setGenre (genre);
    }

    public String getGenre() {
        return genre;
    }

    public Genre setGenre(String genre) {
        this.genre =HASHTAG + genre;
        return this;
    }
}
