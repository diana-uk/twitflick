package com.diana_ukrainsky.twitflick.logic;

import android.util.Log;

import androidx.annotation.NonNull;

import com.diana_ukrainsky.twitflick.callbacks.Callback_retrofitResponse;
import com.diana_ukrainsky.twitflick.models.MovieData;
import com.diana_ukrainsky.twitflick.models.MovieList;
import com.diana_ukrainsky.twitflick.retrofit.RetrofitService;
import com.diana_ukrainsky.twitflick.service.JsonApiMovies;
import com.diana_ukrainsky.twitflick.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitManager {
    // Design Pattern singleton
    private static RetrofitManager INSTANCE = null;

    private RetrofitService retrofitService;
    private JsonApiMovies jsonApiMovies;

    private RetrofitManager() {
        setRetrofitService ();
        setJsonPlaceholders ();
    }

    public static RetrofitManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RetrofitManager ();
        }
        return INSTANCE;
    }

    private void setRetrofitService() {
        retrofitService = new RetrofitService ();
    }

    private void setJsonPlaceholders() {
        jsonApiMovies = retrofitService.getRetrofit ().create (JsonApiMovies.class);
    }

    public void getMovieList(int currentPage, String title, final Callback_retrofitResponse<MovieList> listener) {
        Call<MovieList> call = jsonApiMovies.getMoviesByPage ("", Constants.API_KEY, title, currentPage);

        call.enqueue (new Callback<MovieList> () {
            @Override
            public void onResponse(@NonNull Call<MovieList> call, @NonNull Response<MovieList> response) {
                try {
                    if (!response.isSuccessful ()) {
                        Log.d ("pttt", "Response error  body : " + response.errorBody () + ", Response code: " + response.code ());
                    } else {
                        MovieList movieList = response.body ();
                        if (response.body () == null || response.body ().getTotalResults () == 0) {
                            Log.d ("pttt", "Response message: " + response.message () + ", Total results " + response.body ().getTotalResults () + ", Response code: " + response.code () + " Response body: " + response.body ()
                                    + "Error body: " + response.errorBody () + " Response: " + response.body ().getResponse () + " Error: " + response.body ().getError ());
                            listener.getResult (movieList);
                        } else {
                            movieList = response.body ();
                        }
                        listener.getResult (movieList);
                    }
                } catch (Exception e) {
                    // The response was no good...
                    Log.d (Constants.LOG_TAG, "Response error  body : " + response.errorBody () + ", Response code: " + response.code ());
                    Log.d (Constants.LOG_TAG, "exception: " + e.getMessage ());
                    listener.getResult (null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieList> call, @NonNull Throwable throwable) {
                // The response was no good...
                Log.d ("pttt", "Failure!!!, Message: " + throwable.getMessage ());
                listener.getResult (null);
            }
        });
    }

    private void searchMovie(String title) {
        Call<MovieData> call = jsonApiMovies.getMovieDetails ("", Constants.API_KEY, title);
        call.enqueue (new Callback<MovieData> () {

            @Override
            public void onResponse(Call<MovieData> call, Response<MovieData> response) {
                if (!response.isSuccessful ()) {
                    Log.d ("pttt", "hello" + response.message ());

                } else {
                    MovieData movie = response.body ();
                    Log.d ("pttt", "title: " + movie.getTitle ());
                }
            }

            @Override
            public void onFailure(Call<MovieData> call, Throwable t) {
                Log.d ("pttt", "Failure!!!, Message: " + t.getMessage ());
            }
        });
    }

    public void searchMovieByIMDbId(MovieData movieItem, final Callback_retrofitResponse<MovieData> listener) {
        Call<MovieData> call = jsonApiMovies.getMoviesByIMDbId ("",Constants.API_KEY,movieItem.getImdbId ());
        call.enqueue (new Callback<MovieData> () {

            @Override
            public void onResponse(Call<MovieData> call, Response<MovieData> response) {
                try {
                    if (!response.isSuccessful ()) {
                        Log.d ("pttt", "Response error  body : " + response.errorBody () + ", Response code: " + response.code ());
                    } else {
                        MovieData movieItem = response.body();
                        Log.d (Constants.LOG_TAG,"id: "+movieItem.getImdbId ()+"genres: "+movieItem.getGenre ());
                        listener.getResult (movieItem);
                    }
                } catch (Exception e) {
                    // The response was no good...
                    Log.d (Constants.LOG_TAG, "Response error  body : " + response.errorBody () + ", Response code: " + response.code ());
                    Log.d (Constants.LOG_TAG, "exception: " + e.getMessage ());
                    listener.getResult (null);
                }
            }
            @Override
            public void onFailure(Call<MovieData> call, Throwable t) {
                Log.d (Constants.LOG_TAG, "Failure!!!, Message: " + t.getMessage ());
            }
        });

    }

}
