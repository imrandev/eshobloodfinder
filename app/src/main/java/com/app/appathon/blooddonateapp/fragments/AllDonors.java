package com.app.appathon.blooddonateapp.fragments;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.appathon.blooddonateapp.OnBackPressedListener;
import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.adapter.CustomListAdapter;
import com.app.appathon.blooddonateapp.adapter.DBAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllDonors#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllDonors extends Fragment implements OnBackPressedListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DBAdapter dbHelper;
    private Cursor cursor;


    public AllDonors() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AllDonors.
     */
    // TODO: Rename and change types and number of parameters
    public static AllDonors newInstance(String param1, String param2) {
        AllDonors fragment = new AllDonors();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private RecyclerView recyclerView;
    private CustomListAdapter customListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_all_donors, container, false);
        dbHelper = new DBAdapter(getContext());
        dbHelper.open();

        cursor = dbHelper.fetchAllInfo();

        dbHelper.close();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.allDonor);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(new CustomListAdapter(getContext(),cursor));

        return rootView;
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragmentList = getFragmentManager().getFragments();
        if (fragmentList != null) {
            //TODO: Perform your logic to pass back press here
            for(Fragment fragment : fragmentList){
                if(fragment instanceof OnBackPressedListener){
                    ((OnBackPressedListener)fragment).onBackPressed();
                }
            }
        }
    }
}
