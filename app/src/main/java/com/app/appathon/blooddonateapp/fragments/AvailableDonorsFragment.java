package com.app.appathon.blooddonateapp.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.appathon.blooddonateapp.R;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;

import br.com.mauker.materialsearchview.MaterialSearchView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AvailableDonorsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AvailableDonorsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FloatingActionButton mFabButton;
    private static final String[] ANDROID_VERSIONS = {
            "All", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
    };

    public AvailableDonorsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AvailableDonorsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AvailableDonorsFragment newInstance(String param1, String param2) {
        AvailableDonorsFragment fragment = new AvailableDonorsFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_available_donors, container, false);
        mFabButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        final MaterialSearchView searchView = (MaterialSearchView) rootView.findViewById(R.id.search_view);
        mFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.openSearch();
            }
        });
        mFabButton.show();

        MaterialSpinner spinner = (MaterialSpinner) rootView.findViewById(R.id.mSpinner);
        spinner.setItems(ANDROID_VERSIONS);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            }
        });
        spinner.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {

            @Override public void onNothingSelected(MaterialSpinner spinner) {
                Snackbar.make(spinner, "Nothing selected", Snackbar.LENGTH_LONG).show();
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewOpened() {

            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
        return rootView;
    }
}
