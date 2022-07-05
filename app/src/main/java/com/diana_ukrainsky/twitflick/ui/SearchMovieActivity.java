package com.diana_ukrainsky.twitflick.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.diana_ukrainsky.twitflick.R;
import com.diana_ukrainsky.twitflick.adapter.MovieAdapter;
import com.diana_ukrainsky.twitflick.models.MovieList;
import com.diana_ukrainsky.twitflick.models.MovieData;
import com.diana_ukrainsky.twitflick.retrofit.RetrofitService;
import com.diana_ukrainsky.twitflick.service.JsonApiMovies;
import com.diana_ukrainsky.twitflick.utils.AlertUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchMovieActivity extends AppCompatActivity {
    public static SearchMovieActivity instance = null;

    private LinearLayoutManager linearLayoutManager;

    private MovieAdapter movieAdapter;
    private  RetrofitService retrofitService;
    //********** Pagination *************
    private ProgressBar searchMovie_PB_progressBar;
    private final int MAX_RESULTS_IN_PAGE=10;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int totalPages;
    private int currentPage = PAGE_START;
    // ********************************
    private SearchView searchMovie_SV_searchMovie;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        instance = this;
        setContentView (R.layout.activity_search_movie);

        findViews ();
        setViews();
        setAdapter();
        setSearchView ();
        setListeners ();
    }

    private void setAdapter() {
        movieAdapter = new MovieAdapter (this);
        recyclerView.setAdapter (movieAdapter);
    }

    private void setViews() {
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setHasFixedSize (true);
        recyclerView.setLayoutManager (linearLayoutManager);
        searchMovie_PB_progressBar.setVisibility(View.INVISIBLE);
    }

    private void loadMoreItems(String movieName) {
        recyclerView.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                searchMovie_PB_progressBar.setVisibility(View.VISIBLE);
                isLoading = true;
                currentPage += 1;
                loadNextPage(movieName);
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    private void loadNextPage(String title) {
        retrofitService = new RetrofitService ();

        JsonApiMovies jsonApiMovies = retrofitService.getRetrofit ().create (JsonApiMovies.class);

        Call<MovieList> call = jsonApiMovies.getMoviesByPage ("", "407f78ba", title, currentPage);
        call.enqueue (new Callback<MovieList> () {

            @Override
            public void onResponse(Call<MovieList> call, Response<MovieList> response) {
                if (!response.isSuccessful ()) {
                    Log.d ("pttt", "Response error  body : " + response.errorBody () + ", Response code: " + response.code ());
                } else {
                    if (response.body () == null || response.body ().getTotalResults () == 0) {
                        AlertUtils.showToast (getApplicationContext (), response.body ().getError ());
                        Log.d ("pttt", "Response message: " + response.message () + ", Total results " + response.body ().getTotalResults () + ", Response code: " + response.code () + " Response body: " + response.body ()
                                + "Error body: " + response.errorBody () + " Response: " + response.body ().getResponse () + " Error: " + response.body ().getError ());
                    } else {
                        movieAdapter.removeLoadingFooter();
                        isLoading = false;

                        totalPages = response.body ().getTotalResults ()/MAX_RESULTS_IN_PAGE;

                        MovieList moviesList = response.body ();
                        movieAdapter.addAll (moviesList.getMovies ());

                        if (currentPage != totalPages) movieAdapter.addLoadingFooter();
                        else isLastPage = true;
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieList> call, Throwable t) {
                Log.d ("pttt", "Failure!!!, Message: " + t.getMessage ());
            }
        });
    }

    private void setSearchView() {
        searchMovie_SV_searchMovie.clearFocus ();
        searchMovie_SV_searchMovie.setOnQueryTextListener (new SearchView.OnQueryTextListener () {
            @Override
            public boolean onQueryTextSubmit(String title) {
                searchMovie_PB_progressBar.setVisibility(View.VISIBLE);
                filterList (title);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String movieName) {
                return false;
            }
        });
    }

    private void filterList(String title) {
        if (title.isEmpty ())
            AlertUtils.showToast (getApplicationContext (), "Movie Name is required");
        else {
            // *** Use of pagination ***
            loadMoreItems (title);
            loadFirstPage(title);
            //**********************
        }
    }

    private void loadFirstPage(String title) {
        retrofitService = new RetrofitService ();

        JsonApiMovies jsonApiMovies = retrofitService.getRetrofit ().create (JsonApiMovies.class);

        Call<MovieList> call = jsonApiMovies.getMoviesByPage ("", "407f78ba", title, currentPage);
        call.enqueue (new Callback<MovieList> () {

            @Override
            public void onResponse(Call<MovieList> call, Response<MovieList> response) {
                if (!response.isSuccessful ()) {
                    Log.d ("pttt", "Response error  body : " + response.errorBody () + ", Response code: " + response.code ());
                } else {
                    if (response.body () == null || response.body ().getTotalResults () == 0) {
                        AlertUtils.showToast (getApplicationContext (), response.body ().getError ());
                        Log.d ("pttt", "Response message: " + response.message () + ", Total results " + response.body ().getTotalResults () + ", Response code: " + response.code () + " Response body: " + response.body ()
                                + "Error body: " + response.errorBody () + " Response: " + response.body ().getResponse () + " Error: " + response.body ().getError ());
                    } else {
                        totalPages = response.body ().getTotalResults ()/MAX_RESULTS_IN_PAGE;
                        MovieList moviesList = response.body ();
                        searchMovie_PB_progressBar.setVisibility(View.GONE);
                        movieAdapter.updateAll (moviesList.getMovies ());
                        recyclerView.setAdapter (movieAdapter);

                        if (currentPage <= totalPages) movieAdapter.addLoadingFooter();
                        else isLastPage = true;
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieList> call, Throwable t) {
                Log.d ("pttt", "Failure!!!, Message: " + t.getMessage ());
            }
        });
    }

    public static SearchMovieActivity getInstance() {
        return instance;
    }

    private void setListeners() {

    }

    private void updateAdapter(MovieList moviesList) {
        List<MovieData> movieList = new ArrayList<> ();
        for (MovieData movie:moviesList.getMovies ()) {
            movieList.add (movie);
        }
        movieAdapter = new MovieAdapter (movieList,getApplicationContext ());
        recyclerView.setAdapter (movieAdapter);
    }

    private void searchMovie() {
        RetrofitService retrofitService = new RetrofitService ();

        JsonApiMovies jsonApiMovies = retrofitService.getRetrofit ().create (JsonApiMovies.class);

        Call<MovieData> call = jsonApiMovies.getMovieDetails ("", "407f78ba", "MAN");
        call.enqueue (new Callback<MovieData> () {

            @Override
            public void onResponse(Call<MovieData> call, Response<MovieData> response) {
                if (!response.isSuccessful ()) {
                    Log.d ("pttt", "hello" + response.message ());

                } else {
                    MovieData movie = response.body ();
                    Log.d ("pttt", "title: " + movie.getTitle ().toString ());
                    //    initData (movie);
                }
            }

            @Override
            public void onFailure(Call<MovieData> call, Throwable t) {
                Log.d ("pttt", "Failure!!!, Message: " + t.getMessage ());
            }
        });
    }

    private void findViews() {
        searchMovie_SV_searchMovie = findViewById (R.id.searchMovie_SV_searchMovie);
        searchMovie_PB_progressBar = findViewById (R.id.searchMovie_PB_progressBar);
        recyclerView = findViewById (R.id.searchMovie_RV_recyclerView);

    }

}