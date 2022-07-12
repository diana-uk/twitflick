package com.diana_ukrainsky.twitflick.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.diana_ukrainsky.twitflick.R;
import com.diana_ukrainsky.twitflick.databinding.ActivityBottomNavigationBinding;
import com.diana_ukrainsky.twitflick.fragments.FriendsFeedFragment;
import com.diana_ukrainsky.twitflick.fragments.NotificationsFragment;
import com.diana_ukrainsky.twitflick.fragments.UserFeedFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class BottomNavigationActivity extends AppCompatActivity {

    final Fragment friendsFeedFragment = new FriendsFeedFragment ();
    final Fragment userFeedFragment = new UserFeedFragment ();
    final Fragment notificationsFragment = new NotificationsFragment();
    final FragmentManager fragmentManager = getSupportFragmentManager ();

    public static BottomNavigationActivity instance = null;

    private BottomNavigationView bottomNavigationView;

    private FloatingActionButton bottomNavigation_FB_addReview;
    private ActivityBottomNavigationBinding binding;

    private Fragment active = friendsFeedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        instance = this;
        setContentView (R.layout.activity_bottom_navigation);

        findViews ();
        setListeners ();
        initView ();
    }

    public static BottomNavigationActivity getInstance() {
        return instance;
    }


    private void setListeners() {
        bottomNavigation_FB_addReview.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (BottomNavigationActivity.this, SearchMovieActivity.class);
                startActivity (intent);
            }
        });

    }

    private void findViews() {
        binding = DataBindingUtil.setContentView (this, R.layout.activity_bottom_navigation);
        bottomNavigationView = findViewById (R.id.bottomNavigationView);
        bottomNavigation_FB_addReview = findViewById (R.id.bottomNavigation_FB_addReview);
    }


    private void initView() {
        getSupportFragmentManager ()
                .beginTransaction ()
                .replace (R.id.frameLayout_Bottom,
                new FriendsFeedFragment (),
                "friends_feed_fragment")
                .commit ();

        binding.bottomNavigationView.setSelectedItemId (R.id.bottomMenu_ITEM_friends);
        bottomNavigationView.setBackground (null);

        setFragments();

        binding.bottomNavigationView.setOnItemSelectedListener (new NavigationBarView.OnItemSelectedListener () {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId ()) {
                    case R.id.bottomMenu_ITEM_friends:
                        loadFragment (friendsFeedFragment);
                           // loadFragment (new FriendsFeedFragment (),"friends_feed_fragment");
                        break;
                    case R.id.bottomMenu_ITEM_user:
                        loadFragment (userFeedFragment);
                           // loadFragment (UserFeedFragment.newInstance (),"user_feed_fragment");
                        break;
                    case R.id.bottomMenu_ITEM_notifications:
                        loadFragment (notificationsFragment);
                        //loadFragment (new NotificationsFragment (),"notifications_fragment");
                        break;
//                    case R.id.bottomMenu_ITEM_filter:
//                        loadFragment(new FilterFragment ());
//                        break;
                }
                return true;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        fragmentManager.beginTransaction ().hide (active).show (fragment).commit ();
        active = fragment;
    }

    private void setFragments() {
        fragmentManager.beginTransaction().add(R.id.frameLayout_Bottom, userFeedFragment, "user_feed_fragment").hide(userFeedFragment).commit();
        fragmentManager.beginTransaction().add(R.id.frameLayout_Bottom, notificationsFragment, "notifications_fragment").hide(notificationsFragment).commit();
        fragmentManager.beginTransaction().add(R.id.frameLayout_Bottom,friendsFeedFragment, "friends_feed_fragment").commit();
    }

    public void loadFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction ();
        transaction.hide (fragmentManager.findFragmentByTag ("user_feed_fragment"));
        transaction.replace (R.id.frameLayout_Bottom, fragment,tag);
        transaction.commit ();
    }
}