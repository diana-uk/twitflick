package com.diana_ukrainsky.twitflick.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.diana_ukrainsky.twitflick.R;
import com.diana_ukrainsky.twitflick.adapters.UserAdapter;
import com.diana_ukrainsky.twitflick.callbacks.Callback_setMyFriends;
import com.diana_ukrainsky.twitflick.logic.DatabaseManager;
import com.diana_ukrainsky.twitflick.models.GeneralUser;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MyFriendsFragment extends Fragment implements  UserAdapter.ItemClickListener {

    private RecyclerView fragmentMyFriends_RV_recyclerView;
    private List<GeneralUser> generalUserList;
    private UserAdapter userAdapter;

    private View view;

    private BottomNavigationView bottomNavigationView;
    private BottomAppBar bottomAppBar;
    private FloatingActionButton bottomNavigation_FB_addReview;

    public MyFriendsFragment() {
        // Required empty public constructor
    }

    public static MyFriendsFragment newInstance() {
        MyFriendsFragment fragment = new MyFriendsFragment ();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate (R.layout.fragment_my_friends, container, false);

        generalUserList = new ArrayList<> ();

        findViews(view);
        setAdapter ();
        setRecyclerView ();
        setViewUI ();

        return view;
    }


    private void setAdapter() {
        userAdapter = new UserAdapter (generalUserList,this,getContext ());
    }

    private void findViews(View view) {
        bottomNavigationView = getActivity ().findViewById (R.id.bottomNavigationView);
        bottomAppBar = getActivity ().findViewById (R.id.bottomAppBar);
        bottomNavigation_FB_addReview = getActivity ().findViewById (R.id.bottomNavigation_FB_addReview);
        fragmentMyFriends_RV_recyclerView = view.findViewById (R.id.fragmentMyFriends_RV_recyclerView);
    }

    private void setRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager (getActivity ());
        fragmentMyFriends_RV_recyclerView.setLayoutManager (linearLayoutManager);
        fragmentMyFriends_RV_recyclerView.setAdapter (userAdapter);
    }

    private void setViewUI() {
        setVisibilityBottomNav ();

        DatabaseManager.getInstance ().getFriendsList (new Callback_setMyFriends () {
            @Override
            public void setMyFriendsList(List<GeneralUser> myFriendsList) {
                if (myFriendsList != null) {
                    userAdapter.addAll (myFriendsList);
                }
            }
        });
    }

    private void setVisibilityBottomNav() {
        bottomNavigationView.setVisibility (View.GONE);
        bottomAppBar.setVisibility (View.GONE);
        bottomNavigation_FB_addReview.setVisibility (View.GONE);
    }

    @Override
    public void onItemClick(GeneralUser generalUser) {
        String generalUserJson = new Gson ().toJson (generalUser);
        Fragment fragment = FriendDetailsFragment.newInstance (generalUserJson);

        FragmentTransaction transaction = getActivity ().getSupportFragmentManager ().beginTransaction ();
        transaction.replace (((ViewGroup)getView().getParent()).getId(),fragment,"friends_details_fragment");
        transaction.addToBackStack (null);
        transaction.commit ();
    }


    @Override
    public void onDestroy() {
        super.onDestroy ();
        bottomNavigation_FB_addReview.setVisibility (View.VISIBLE);
        bottomNavigationView.setVisibility (View.VISIBLE);
        bottomAppBar.setVisibility (View.VISIBLE);
    }

}