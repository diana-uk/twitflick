package com.diana_ukrainsky.twitflick.service;

import com.diana_ukrainsky.twitflick.models.MovieList;
import com.diana_ukrainsky.twitflick.models.MovieData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface JsonApiMovies {
    @GET
    Call<MovieData> getMovieDetails(@Url String urlstring, @Query (value="apikey") String apikey, @Query (value="t") String item);

    @GET
    Call<MovieList> getMoviesByPage(@Url String urlstring, @Query (value="apikey") String apikey,@Query (value="s") String item, @Query (value="page") int page);

    @GET
    Call<MovieData> getMoviesByIMDbId(@Url String urlstring, @Query (value="apikey") String apikey,@Query (value="i") String i);
}
