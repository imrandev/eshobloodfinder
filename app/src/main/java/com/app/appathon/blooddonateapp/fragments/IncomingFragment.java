package com.app.appathon.blooddonateapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.activities.InboxActivity;
import com.app.appathon.blooddonateapp.adapter.InboxAdapter;
import com.app.appathon.blooddonateapp.database.FirebaseDatabaseHelper;
import com.app.appathon.blooddonateapp.model.Inbox;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class IncomingFragment extends Fragment implements FirebaseDatabaseHelper.IncomingInboxInterface {

    private RecyclerView incomingView;
    private ArrayList<Inbox> incomingList = new ArrayList<>();
    private ArrayList<String> msg_count = new ArrayList<>();

    public IncomingFragment() {
        // Required empty public constructor
    }

    public static IncomingFragment newInstance(){
        IncomingFragment fragment = new IncomingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_incoming, container, false);

        incomingView = (RecyclerView) rootView.findViewById(R.id.inbox_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        incomingView.setLayoutManager(layoutManager);

        FirebaseDatabaseHelper databaseHelper = new FirebaseDatabaseHelper(getActivity(), this);
        databaseHelper.getUserIncomingInboxData();
        return rootView;
    }

    @Override
    public void getIncomingInboxData(String id, String email, List<Inbox> inboxes, List<String> count) {
        incomingList.addAll(inboxes);
        msg_count.addAll(count);
        incomingView.setAdapter(new InboxAdapter(incomingList, getActivity(), msg_count));
    }

    @Override
    public void onFirebaseInternalError(String error) {

    }
}
