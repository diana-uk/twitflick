package com.diana_ukrainsky.twitflick.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diana_ukrainsky.twitflick.R;
import com.diana_ukrainsky.twitflick.logic.DataManager;
import com.diana_ukrainsky.twitflick.models.MovieData;
import com.diana_ukrainsky.twitflick.retrofit.RetrofitService;
import com.diana_ukrainsky.twitflick.service.JsonApiMovies;
import com.diana_ukrainsky.twitflick.ui.AddReviewActivity;
import com.diana_ukrainsky.twitflick.utils.ImageUtils;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // ***Pagination Implementation***
    private static final int LOADING = 0;
    private static final int ITEM = 1;
    private boolean isLoadingAdded = false;
    // *******************************

    private List<MovieData> movieDataList;
    private Context context;

    private Bundle bundle;

    public MovieAdapter(Context context) {
        this.context = context;
        movieDataList = new ArrayList<> ();
    }

    public MovieAdapter(List<MovieData> movieDataList, Context context) {
        this.movieDataList = movieDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater layoutInflater = LayoutInflater.from (parent.getContext ());

        switch (viewType) {
            case ITEM:
                View viewItem = layoutInflater.inflate (R.layout.movie_item_list, parent, false);
                viewHolder = new MovieViewHolder (viewItem);
                break;
            case LOADING:
                View viewLoading = layoutInflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingViewHolder(viewLoading);
                break;
        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final MovieData movieItem = movieDataList.get (position);
        switch (getItemViewType(position)) {
            case ITEM:
                MovieViewHolder movieViewHolder = (MovieViewHolder) holder;

                movieViewHolder.movieItemList_TXT_movieName.setText (movieItem.getTitle ());
                movieViewHolder.movieItemList_TXT_movieDate.setText (movieItem.getReleaseDate ());

                ImageUtils.setImageUI (movieViewHolder.movieItemList_IMG_movieImage,movieItem.getPoster ());

                movieViewHolder.itemView.setOnClickListener (new View.OnClickListener () {
                    @Override
                    public void onClick(View v) {
                        Log.d ("pttt", "movie clicked title:  "+movieItem.getTitle ()+" genres: "+movieItem.getGenre ());
                        searchMovieByIMDbId(movieItem);
                    }
                });
                break;

            case LOADING:
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void searchMovieByIMDbId(MovieData movie) {
            RetrofitService retrofitService = new RetrofitService ();

            JsonApiMovies jsonApiMovies = retrofitService.getRetrofit ().create (JsonApiMovies.class);

            Call<MovieData> call = jsonApiMovies.getMoviesByIMDbId ("","407f78ba",movie.getImdbId ());
            call.enqueue (new Callback<MovieData> () {

                @Override
                public void onResponse(Call<MovieData> call, Response<MovieData> response) {
                    if(!response.isSuccessful()) {
                        Log.d ("pttt","hello"+response.message ());
                    }
                    else {
                        MovieData movie = response.body();
                        Log.d ("pttt","id: "+movie.getImdbId ()+"genres: "+movie.getGenre ());

                        putInBundle (movie);
                        startAddReviewActivity ();
                    }
                }
                @Override
                public void onFailure(Call<MovieData> call, Throwable t) {
                    Log.d ("pttt", "Failure!!!, Message: " + t.getMessage ());
                }
            });

        }

    private void saveReviewData() {
        DataManager.getInstance ().saveReviewData();
    }

    private void putInBundle(MovieData movieData) {
        String movieDataJson = new Gson ().toJson (movieData);
        bundle = new Bundle ();
        bundle.putString ("MOVIE_DATA", movieDataJson);
    }

    private void startAddReviewActivity() {
        Intent intent = new Intent (context.getApplicationContext (), AddReviewActivity.class);
        intent.putExtras (bundle);
        context.startActivity (intent);
    }

    @Override
    public int getItemCount() {
        return movieDataList.size ();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == movieDataList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new MovieData ());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = movieDataList.size() - 1;
        MovieData result = getItem(position);

        if (result != null) {
            movieDataList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void add(MovieData movie) {
        movieDataList.add(movie);
        notifyItemInserted(movieDataList.size() - 1);
    }

    public void addAll(List<MovieData> movieResults) {
        int previousContentSize = movieDataList.size ();
        for (MovieData movie : movieResults) {
                add(movie);
        }
        notifyItemRangeInserted (previousContentSize,movieDataList.size ());
    }

    public void updateAll(List<MovieData> movieResults) {
        movieDataList.retainAll (movieResults);
        movieResults.removeAll (movieDataList);
        for (MovieData movie : movieResults) {
            add(movie);
        }
        notifyItemRangeChanged (0,movieDataList.size ());
    }

    public void clearAll() {
        int previousContentSize = movieDataList.size ();
        movieDataList.clear ();
        notifyItemRangeRemoved(0, previousContentSize);
    }

    public MovieData getItem(int position) {
        return movieDataList.get(position);
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView movieItemList_IMG_movieImage;
        TextView movieItemList_TXT_movieName;
        TextView movieItemList_TXT_movieDate;


        public MovieViewHolder(@NonNull View itemView) {
            super (itemView);
            movieItemList_IMG_movieImage = itemView.findViewById (R.id.movieItemList_IMG_movieImage);
            movieItemList_TXT_movieName = itemView.findViewById (R.id.movieItemList_TXT_movieName);
            movieItemList_TXT_movieDate = itemView.findViewById (R.id.movieItemList_TXT_movieDate);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.loadmore_progress);

        }
    }

}
