package com.diana_ukrainsky.twitflick.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diana_ukrainsky.twitflick.R;
import com.diana_ukrainsky.twitflick.fragments.MyFriendsFragment;
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
    private List<GeneralUser> generalUserData;
    private ItemClickListener clickListener;
    private Context context;

    public UserAdapter(List<GeneralUser> generalUserData, Context context) {
        this.generalUserData = generalUserData;
        this.context = context;
    }

    public UserAdapter(List<GeneralUser> generalUserData, ItemClickListener clickListener, Context context) {
        this.generalUserData = generalUserData;
        this.clickListener = clickListener;
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
        setViewUI (holder);

        setUserImageUI (generalUserItem.getUserId (), holder.userItem_CIMG_userCircularImage);
        holder.userItem_TXT_userName.setText (generalUserItem.getName ());
        //holder.userItem_TXT_NumOfFriends.setText (String.valueOf (generalUserItem.getAttributes ().get ("NumberOfFriends")));
        holder.userItem_TXT_username.setText (generalUserItem.getUsername ());

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

        holder.userItem_CV_userCard.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick (generalUserItem);
            }
        });
    }

    private void setViewUI(ViewHolder holder) {
        FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
        MyFriendsFragment myFriendsFragment = (MyFriendsFragment) manager.findFragmentByTag (Constants.MY_FRIENDS_FRAGMENT);
        if (myFriendsFragment != null && myFriendsFragment.isVisible())
            holder.userItem_IMGBTN_sendRequest.setVisibility (View.GONE);
    }

    private void setUserImageUI(String userId, ImageView userItem_cimg_userCircularImage) {
        StorageReference userStorageReference = DatabaseManager.getInstance ().getStorageReference ().child (Constants.STORAGE_PATH + userId);
        userStorageReference.getDownloadUrl ().addOnSuccessListener (new OnSuccessListener<Uri> () {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                if (context == null) {
                    return;
                }
                ImageUtils.setImageUI (context, uri, userItem_cimg_userCircularImage);

            }
        }).addOnFailureListener (new OnFailureListener () {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // File not found
                setNoImageUI (userItem_cimg_userCircularImage);
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

    public void add(GeneralUser generalUser) {
        generalUserData.add (generalUser);
        notifyItemInserted (generalUserData.size () - 1);
    }

    public void addAll(List<GeneralUser> generalUserList) {
        int previousContentSize = generalUserData.size ();
        for (GeneralUser generalUser : generalUserList) {
            add (generalUser);
        }
        notifyItemRangeInserted (previousContentSize, generalUserData.size ());
    }

    public void updateAll(List<GeneralUser> generalUserList) {
        generalUserData.retainAll (generalUserList);
        generalUserList.removeAll (generalUserData);
        for (GeneralUser generalUser : generalUserList) {
            add (generalUser);
        }
        notifyItemRangeChanged (0, generalUserData.size ());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userItem_CIMG_userCircularImage;
        MaterialTextView userItem_TXT_userName;
        ImageButton userItem_IMGBTN_sendRequest;
        CardView userItem_CV_userCard;
        MaterialTextView userItem_TXT_NumOfFriends;
        MaterialTextView userItem_TXT_username;

        public ViewHolder(@NonNull View itemView) {
            super (itemView);

            userItem_CIMG_userCircularImage = itemView.findViewById (R.id.userItem_CIMG_userCircularImage);
            userItem_TXT_userName = itemView.findViewById (R.id.userItem_TXT_fullName);
            userItem_IMGBTN_sendRequest = itemView.findViewById (R.id.userItem_IMGBTN_sendRequest);
            userItem_CV_userCard = itemView.findViewById (R.id.userItem_CV_userCard);
            //userItem_TXT_NumOfFriends = itemView.findViewById (R.id.userItem_TXT_NumOfFriends);
            userItem_TXT_username = itemView.findViewById (R.id.userItem_TXT_username);
        }
    }

    public interface ItemClickListener {
        public void onItemClick(GeneralUser generalUser);
    }

}

