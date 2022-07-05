package com.diana_ukrainsky.twitflick.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diana_ukrainsky.twitflick.R;
import com.diana_ukrainsky.twitflick.callbacks.Callback_setUsername;
import com.diana_ukrainsky.twitflick.logic.DatabaseManager;
import com.diana_ukrainsky.twitflick.models.Genre;
import com.diana_ukrainsky.twitflick.models.ReviewData;
import com.diana_ukrainsky.twitflick.utils.AlertUtils;
import com.diana_ukrainsky.twitflick.utils.Constants;
import com.diana_ukrainsky.twitflick.utils.ImageUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    List<ReviewData> myReviewData;
    Context context;

    public ReviewAdapter(List<ReviewData> myReviewData, Context context) {
        this.myReviewData = myReviewData;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from (parent.getContext ());
        View view = layoutInflater.inflate (R.layout.review_item_list, parent, false);
        ViewHolder viewHolder = new ViewHolder (view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ReviewData reviewDataItem = myReviewData.get (position);

        holder.reviewItemList_TXT_movieName.setText (reviewDataItem.getMovieName ());
        holder.reviewItemList_TXT_movieDate.setText (reviewDataItem.getMovieDate ());
        holder.reviewItemList_TXT_reviewedMovieName.setText (reviewDataItem.getMovieName ());
        setUsernameUI (reviewDataItem.getUserID (),holder.reviewItemList_TXT_reviewerName);
        holder.reviewItemList_TXT_reviewText.setText (reviewDataItem.getReviewText ());
        holder.reviewItemList_RB_ratingBar.setRating (reviewDataItem.getRating ());
        setUserImageUI (reviewDataItem.getUserID (),holder.reviewItemList_CIMG_imageViewCircular);
        ImageUtils.setImageUI (holder.reviewItemList_IMG_movieImage, reviewDataItem.getMovieImageUrl ());

        setGenresUI (holder.genresArr, reviewDataItem.getGenresList ());

        holder.itemView.setOnClickListener (v -> AlertUtils.showToast (context, reviewDataItem.getMovieName ()));
    }

    private void setUsernameUI(String userID, MaterialTextView reviewItemList_txt_reviewerName) {
        DatabaseManager.getInstance ().getUsernameFromId (userID,new Callback_setUsername () {
            @Override
            public void setUsername(String username) {
                if (username != null)
                    reviewItemList_txt_reviewerName.setText (username);
                else
                    reviewItemList_txt_reviewerName.setText (context.getString (R.string.no_username));
            }
        });
    }

    private void setUserImageUI(String userID,ImageView reviewItemList_CIMG_imageViewCircular) {
        StorageReference userStorageReference = DatabaseManager.getInstance ().getStorageReference ().child (Constants.STORAGE_PATH + userID);
        userStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri> () {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                ImageUtils.setImageUI (context,userStorageReference,reviewItemList_CIMG_imageViewCircular);

            }
        }).addOnFailureListener(new OnFailureListener () {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // File not found
                setNoImageUI(reviewItemList_CIMG_imageViewCircular);
            }
        });
    }

    private void setNoImageUI(ImageView reviewItemList_CIMG_imageViewCircular) {
        ImageUtils.setImageUI (context, DatabaseManager.getInstance ().getNoImageStorageReference (), reviewItemList_CIMG_imageViewCircular);
    }


    private void setGenresUI(MaterialTextView[] genresArr, List<Genre> genresList) {
        for (int i = 0; i < 4; i++) {
            if (genresList.size () <= (i + 1))
                genresArr[i].setVisibility (View.INVISIBLE);

            else
                genresArr[i].setText (genresList.get (i).getGenre ());
        }
    }

    @Override
    public int getItemCount() {
        return myReviewData.size ();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView reviewItemList_IMG_movieImage;
        ImageView reviewItemList_CIMG_imageViewCircular;
        MaterialTextView reviewItemList_TXT_movieName;
        MaterialTextView reviewItemList_TXT_movieDate;
        MaterialTextView reviewItemList_TXT_reviewerName;
        MaterialTextView reviewItemList_TXT_reviewedMovieName;
        MaterialTextView reviewItemList_TXT_reviewText;
        RatingBar reviewItemList_RB_ratingBar;
        MaterialTextView[] genresArr;


        public ViewHolder(@NonNull View itemView) {
            super (itemView);
            genresArr = new MaterialTextView[4];

            reviewItemList_IMG_movieImage = itemView.findViewById (R.id.reviewItemList_IMG_movieImage);
            reviewItemList_CIMG_imageViewCircular = itemView.findViewById (R.id.reviewItemList_CIMG_imageViewCircular);
            reviewItemList_TXT_movieName = itemView.findViewById (R.id.reviewItemList_TXT_movieName);
            reviewItemList_TXT_movieDate = itemView.findViewById (R.id.reviewItemList_TXT_movieDate);
            reviewItemList_TXT_reviewerName = itemView.findViewById (R.id.reviewItemList_TXT_reviewerName);
            reviewItemList_TXT_reviewedMovieName = itemView.findViewById (R.id.reviewItemList_TXT_reviewedMovieName);
            reviewItemList_TXT_reviewText = itemView.findViewById (R.id.reviewItemList_TXT_reviewText);
            reviewItemList_RB_ratingBar = itemView.findViewById (R.id.reviewItemList_RB_ratingBar);

            for (int i = 0; i < 4; i++) {
                String textViewName = "reviewItemList_TXT_genre" + (i + 1);
                int textId = itemView.getResources ().getIdentifier (textViewName, "id", itemView.getContext ().getPackageName ());
                genresArr[i] = itemView.findViewById (textId);
            }
        }
    }


}
