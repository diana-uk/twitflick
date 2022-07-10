package com.diana_ukrainsky.twitflick.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diana_ukrainsky.twitflick.R;
import com.diana_ukrainsky.twitflick.logic.DataManager;
import com.diana_ukrainsky.twitflick.logic.DatabaseManager;
import com.diana_ukrainsky.twitflick.models.GeneralUser;
import com.diana_ukrainsky.twitflick.utils.Constants;
import com.diana_ukrainsky.twitflick.utils.ImageUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    List<GeneralUser> generalUserData;
    Context context;

    public UserAdapter(List<GeneralUser> generalUserData, Context context) {
        this.generalUserData = generalUserData;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from (parent.getContext ());
        View view = layoutInflater.inflate (R.layout.user_item_list, parent, false);
        UserAdapter.ViewHolder viewHolder = new ViewHolder (view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final GeneralUser generalUserItem = generalUserData.get (position);
        setUserImageUI (generalUserItem.getUserId (),holder.userItem_CIMG_userCircularImage);
        holder.userItem_TXT_userName.setText (generalUserItem.getName ());

        holder.userItem_IMGBTN_sendRequest.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if (!DataManager.getInstance ().checkIfRequestSent (generalUserItem)) {
                    DataManager.getInstance ().sendFriendRequest (generalUserItem);
                    Toast.makeText (context, "Friend request sent", Toast.LENGTH_SHORT).show ();
                } else
                    Toast.makeText (context, "Friend request was already sent", Toast.LENGTH_SHORT).show ();
            }
        });
    }

    private void setUserImageUI(String userId, ImageView userItem_cimg_userCircularImage) {
        StorageReference userStorageReference = DatabaseManager.getInstance ().getStorageReference ().child (Constants.STORAGE_PATH + userId);
        userStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri> () {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                ImageUtils.setImageUI (context,uri,userItem_cimg_userCircularImage);

            }
        }).addOnFailureListener(new OnFailureListener () {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // File not found
                setNoImageUI(userItem_cimg_userCircularImage);
            }
        });
    }


    private void setNoImageUI(ImageView userItem_CIMG_userCircularImage) {
        userItem_CIMG_userCircularImage.setImageResource (R.drawable.ic_no_picture);
    }


    @Override
    public int getItemCount() {
        return generalUserData.size ();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userItem_CIMG_userCircularImage;
        MaterialTextView userItem_TXT_userName;
        ImageButton userItem_IMGBTN_sendRequest;

        public ViewHolder(@NonNull View itemView) {
            super (itemView);

            userItem_CIMG_userCircularImage = itemView.findViewById (R.id.userItem_CIMG_userCircularImage);
            userItem_TXT_userName = itemView.findViewById (R.id.userItem_TXT_userName);
            userItem_IMGBTN_sendRequest = itemView.findViewById (R.id.userItem_IMGBTN_sendRequest);
        }

    }
}

