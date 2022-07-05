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
    public static BottomNavigationActivity instance = null;

    private BottomNavigationView bottomNavigationView;

    private FloatingActionButton bottomNavigation_FB_addReview;
    private ActivityBottomNavigationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        instance = this;
        setContentView (R.layout.activity_bottom_navigation);

        findViews();
        setListeners();

        initView ();
    }

    public static BottomNavigationActivity getInstance(){
        return instance;
    }


    private void setListeners() {
        bottomNavigation_FB_addReview.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (BottomNavigationActivity.this,SearchMovieActivity.class);
                startActivity (intent);
            }
        });

    }

    private void findViews() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bottom_navigation);
        bottomNavigationView = findViewById (R.id.bottomNavigationView);
        bottomNavigation_FB_addReview = findViewById (R.id.bottomNavigation_FB_addReview);
    }


    private void initView() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_Bottom, new FriendsFeedFragment ()).commit();
        binding.bottomNavigationView.setSelectedItemId(R.id.bottomMenu_ITEM_friends);
        bottomNavigationView.setBackground (null);

        binding.bottomNavigationView.setOnItemSelectedListener (new NavigationBarView.OnItemSelectedListener () {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.bottomMenu_ITEM_friends:
                        loadFragment(new FriendsFeedFragment ());
                        break;
                    case R.id.bottomMenu_ITEM_user:
                        loadFragment(new UserFeedFragment ());
                        break;
                    case R.id.bottomMenu_ITEM_notifications:
                        loadFragment(new NotificationsFragment ());
                        break;
//                    case R.id.bottomMenu_ITEM_filter:
//                        loadFragment(new FilterFragment ());
//                        break;
                }
                return true;
            }
        });
    }

    public void loadFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout_Bottom, fragment);
        transaction.commit();
    }
}