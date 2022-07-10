package com.diana_ukrainsky.twitflick.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diana_ukrainsky.twitflick.R;
import com.diana_ukrainsky.twitflick.logic.DataManager;
import com.diana_ukrainsky.twitflick.logic.DatabaseManager;
import com.diana_ukrainsky.twitflick.models.FriendRequestData;
import com.diana_ukrainsky.twitflick.models.GeneralUser;
import com.diana_ukrainsky.twitflick.utils.AlertUtils;
import com.diana_ukrainsky.twitflick.utils.Constants;
import com.diana_ukrainsky.twitflick.utils.ImageUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder>  {
    List<FriendRequestData> friendRequestData;
    Context context;

    public FriendRequestAdapter(List<FriendRequestData> friendRequestData, Context context) {
        this.friendRequestData = friendRequestData;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from (parent.getContext ());
        View view = layoutInflater.inflate (R.layout.friend_request_item_list,parent,false);
        FriendRequestAdapter.ViewHolder viewHolder = new ViewHolder (view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final FriendRequestData friendRequestItem  = friendRequestData.get (position);
        setUserImageUI (friendRequestItem.getUserId (),holder.friendRequestItem_CIMG_userCircularImage);
        holder.friendRequestItem_TXT_userName.setText (friendRequestItem.getUsername ());
        // What happens when Friend Request accepted
        holder.friendRequestItem_IMGBTN_accept.setOnClickListener (v -> {
            DataManager.getInstance ().acceptFriendRequest(friendRequestItem);
            AlertUtils.showToast (context, "Friend Request Accepted !");
        });
        // What happens when Friend Request declined
        holder.friendRequestItem_IMGBTN_decline.setOnClickListener (v -> {
            DataManager.getInstance ().declineFriendRequest(friendRequestItem);
            AlertUtils.showToast (context, "Friend Request Declined !");
        });
    }

    private void setUserImageUI(String userId, ImageView friendRequestItem_CIMG_userCircularImage) {
        StorageReference userStorageReference = DatabaseManager.getInstance ().getStorageReference ().child (Constants.STORAGE_PATH + userId);
        userStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri> () {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                ImageUtils.setImageUI (context,uri,friendRequestItem_CIMG_userCircularImage);

            }
        }).addOnFailureListener(new OnFailureListener () {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // File not found
                setNoImageUI(friendRequestItem_CIMG_userCircularImage);
            }
        });
    }

    private void setNoImageUI(ImageView friendRequestItem_CIMG_userCircularImage) {
        friendRequestItem_CIMG_userCircularImage.setImageResource (R.drawable.ic_no_picture);
    }

    @Override
    public int getItemCount() {
        return friendRequestData.size ();
    }


    public class ViewHolder extends  RecyclerView.ViewHolder {
        ImageView friendRequestItem_CIMG_userCircularImage;
        MaterialTextView friendRequestItem_TXT_userName;
        MaterialTextView friendRequestItem_TXT_dateSent;
        ImageButton friendRequestItem_IMGBTN_accept;
        ImageButton friendRequestItem_IMGBTN_decline;

        public ViewHolder(@NonNull View itemView) {
            super (itemView);
            friendRequestItem_CIMG_userCircularImage = itemView.findViewById (R.id.friendRequestItem_CIMG_userCircularImage);
            friendRequestItem_TXT_userName = itemView.findViewById (R.id.friendRequestItem_TXT_userName);
//            friendRequestItem_TXT_dateSent = itemView.findViewById (R.id.friendRequestItem_TXT_dateSent);
            friendRequestItem_IMGBTN_accept = itemView.findViewById (R.id.friendRequestItem_IMGBTN_accept);
            friendRequestItem_IMGBTN_decline = itemView.findViewById (R.id.friendRequestItem_IMGBTN_decline);
        }
    }
}
