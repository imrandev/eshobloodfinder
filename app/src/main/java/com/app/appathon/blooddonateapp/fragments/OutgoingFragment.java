package com.app.appathon.blooddonateapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.adapter.OutgoingAdapter;
import com.app.appathon.blooddonateapp.database.FirebaseDatabaseHelper;
import com.app.appathon.blooddonateapp.model.Inbox;
import com.app.appathon.blooddonateapp.model.User;
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
public class OutgoingFragment extends Fragment implements FirebaseDatabaseHelper.OutgoingInboxInterface {

    private ArrayList<User> outMsgList = new ArrayList<>();
    private RecyclerView outgoingView;
    private ArrayList<Inbox> outgoingList = new ArrayList<>();
    private ArrayList<String> msg_count = new ArrayList<>();

    public OutgoingFragment() {
        // Required empty public constructor
    }

    public static OutgoingFragment newInstance(){
        OutgoingFragment fragment = new OutgoingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_outgoing, container, false);


        outgoingView = (RecyclerView) rootView.findViewById(R.id.inbox_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        outgoingView.setLayoutManager(layoutManager);

        FirebaseDatabaseHelper databaseHelper = new FirebaseDatabaseHelper(getActivity(), this);
        databaseHelper.getUserOutgoingInboxData();

        return rootView;
    }

    @Override
    public void getOutgoingInboxData(String id, String email, List<Inbox> inboxes, List<User> users, List<String> count) {
        outMsgList.addAll(users);
        outgoingList.addAll(inboxes);
        msg_count.addAll(count);
        outgoingView.setAdapter(new OutgoingAdapter(outMsgList, outgoingList, getActivity(), msg_count));
    }

    @Override
    public void onFirebaseInternalError(String error) {

    }
}
