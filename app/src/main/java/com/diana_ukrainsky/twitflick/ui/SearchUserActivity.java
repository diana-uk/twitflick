package com.diana_ukrainsky.twitflick.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.diana_ukrainsky.twitflick.R;
import com.diana_ukrainsky.twitflick.adapters.UserAdapter;
import com.diana_ukrainsky.twitflick.callbacks.Callback_searchUserByUsername;
import com.diana_ukrainsky.twitflick.logic.DatabaseManager;
import com.diana_ukrainsky.twitflick.models.GeneralUser;
import com.diana_ukrainsky.twitflick.utils.AlertUtils;
import com.diana_ukrainsky.twitflick.utils.Constants;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class SearchUserActivity extends AppCompatActivity {
    private MaterialTextView searchUser_TXT_searchResult;
    private  SearchView searchUser_SV_searchUser;

    private RecyclerView recyclerView;
    private  UserAdapter userAdapter;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_search_user);

        initData ();
        findViews ();
        setSearchView();
        setRecyclerView ();
        setListeners ();
    }

    private void setSearchView() {
        searchUser_SV_searchUser.clearFocus ();
        searchUser_SV_searchUser.setOnQueryTextListener (new SearchView.OnQueryTextListener () {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String username) {
                filterList(username);

                return false;
            }
        });
    }

    private void filterList(String text) {
        if (!text.isEmpty ())
            getUsersByUsernameList (text);
        else
            AlertUtils.showToast (getApplicationContext (),getString(R.string.username_required));
    }


    private void initData() {
        firebaseDatabase = FirebaseDatabase.getInstance ();
        databaseReference = firebaseDatabase.getReference ("AppData");
    }

    private void setRecyclerView() {
        recyclerView.setHasFixedSize (true);
        recyclerView.setLayoutManager (new LinearLayoutManager (this));
    }

    private void setListeners() {
    }

    private void getUsersByUsernameList(String username) {
        DatabaseManager.getInstance ().getUsersListByUsername (username, new Callback_searchUserByUsername () {
            @Override
            public void setUsersList(List<GeneralUser> usersList) {
                if(usersList != null) {
                    //  userAdapter.setFilteredList (usersList);
                    searchUser_TXT_searchResult.setVisibility (View.INVISIBLE);
                    setAdapter (usersList);

                }
                else
                    searchUser_TXT_searchResult.setText (String.format ("%s%s", getString (R.string.no_users_found_with_username), username));
            }
        });
    }

    private void findViews() {
        searchUser_SV_searchUser = findViewById (R.id.searchUser_SV_searchUser);
        searchUser_TXT_searchResult = findViewById (R.id.searchUser_TXT_searchResult);
        recyclerView = findViewById (R.id.searchUser_RV_recyclerView);
    }

    private void setAdapter(List<GeneralUser> usersList) {
        userAdapter = new UserAdapter (usersList, this);
        recyclerView.setAdapter (userAdapter);
    }

}