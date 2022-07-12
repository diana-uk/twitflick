package com.diana_ukrainsky.twitflick.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.diana_ukrainsky.twitflick.R;
import com.diana_ukrainsky.twitflick.adapters.FriendRequestAdapter;
import com.diana_ukrainsky.twitflick.callbacks.Callback_setFriendRequests;
import com.diana_ukrainsky.twitflick.databinding.FragmentNotificationsBinding;
import com.diana_ukrainsky.twitflick.logic.DatabaseManager;
import com.diana_ukrainsky.twitflick.models.FriendRequestData;
import com.diana_ukrainsky.twitflick.models.GeneralUser;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationsFragment extends Fragment {
    private MaterialTextView notificationsFragment_TXT_noFriendRequests;

    private List<GeneralUser> friendRequestsData;

    private FragmentNotificationsBinding binding;
    private View view;

    private RecyclerView notificationsFragment_RV_recyclerView;
    private FriendRequestAdapter friendRequestAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationsFragment newInstance(String param1, String param2) {
        NotificationsFragment fragment = new NotificationsFragment ();
        Bundle args = new Bundle ();
        args.putString (ARG_PARAM1, param1);
        args.putString (ARG_PARAM2, param2);
        fragment.setArguments (args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        initData();
        setFriendRequestsUI ();

        
        if (getArguments () != null) {
            mParam1 = getArguments ().getString (ARG_PARAM1);
            mParam2 = getArguments ().getString (ARG_PARAM2);
        }
    }

    private void setFriendRequestsUI() {
        DatabaseManager.getInstance ().getFriendRequestsList (new Callback_setFriendRequests () {
            @Override
            public void setFriendRequestsList(List<FriendRequestData> friendRequestsList) {
                if(friendRequestsList.isEmpty ())
                    notificationsFragment_TXT_noFriendRequests.setVisibility (View.VISIBLE);
                else
                    notificationsFragment_TXT_noFriendRequests.setVisibility (View.INVISIBLE);

                friendRequestAdapter = new FriendRequestAdapter (friendRequestsList,getContext ());
                notificationsFragment_RV_recyclerView.setAdapter (friendRequestAdapter);
            }
        });
    }

    private void initData() {
        friendRequestsData = new ArrayList<> ();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notifications, container, false);
        view = binding.getRoot();

        findViews();
        setRecyclerView();

        return view;
    }

    private void setRecyclerView() {
        notificationsFragment_RV_recyclerView.setHasFixedSize (true);
        notificationsFragment_RV_recyclerView.setLayoutManager (new LinearLayoutManager (getActivity ()));
    }

    private void findViews() {
        notificationsFragment_TXT_noFriendRequests = view.findViewById (R.id.notificationsFragment_TXT_noFriendRequests);
        notificationsFragment_RV_recyclerView = view.findViewById (R.id.notificationsFragment_RV_recyclerView);
    }
}