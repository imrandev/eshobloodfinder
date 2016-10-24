package com.app.appathon.blooddonateapp.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.app.appathon.blooddonateapp.OnBackPressedListener;
import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.activities.MainActivity;
import com.app.appathon.blooddonateapp.adapter.AvailableAdapter;
import com.app.appathon.blooddonateapp.adapter.CustomListAdapter;
import com.app.appathon.blooddonateapp.adapter.DBAdapter;
import com.app.appathon.blooddonateapp.utils.GeocoderHandler;
import com.app.appathon.blooddonateapp.utils.LocationAddress;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import br.com.mauker.materialsearchview.MaterialSearchView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AvailableDonors#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AvailableDonors extends Fragment implements OnBackPressedListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FloatingActionButton mFabButton,mSearchFab,mListFab,mGPSFab;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private static final String[] BLOOD_TYPE = {
            "All", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
    };
    private Boolean isFabOpen = false;
    private MaterialSearchView searchView;
    private PopupMenu popup;

    private DBAdapter dbHelper;
    private Cursor cursor;
    private RecyclerView recyclerView;

    public AvailableDonors() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AvailableDonors.
     */
    // TODO: Rename and change types and number of parameters
    public static AvailableDonors newInstance(String param1, String param2) {
        AvailableDonors fragment = new AvailableDonors();
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
        mSearchFab = (FloatingActionButton) rootView.findViewById(R.id.fab1);
        mListFab = (FloatingActionButton) rootView.findViewById(R.id.fab2);
        mGPSFab = (FloatingActionButton) rootView.findViewById(R.id.fab3);

        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_backward);

        searchView = (MaterialSearchView) rootView.findViewById(R.id.search_view);
        mFabButton.setOnClickListener(onFabButtonListener);
        mSearchFab.setOnClickListener(onFabButtonListener);
        mListFab.setOnClickListener(onFabButtonListener);
        mGPSFab.setOnClickListener(onFabButtonListener);

        dbHelper = new DBAdapter(getContext());
        dbHelper.open();

        cursor = dbHelper.fetchAllInfo();

        dbHelper.close();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.availableDonor);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(new AvailableAdapter(getContext(),cursor));



        ArrayList arrayList = new ArrayList<String>(Arrays.asList(BLOOD_TYPE));

        //MaterialBetterSpinner spinner = (MaterialBetterSpinner ) rootView.findViewById(R.id.mSpinner);
        //spinner.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,arrayList));
        /*spinner.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        spinner.setItems(BLOOD_TYPE);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            }
        });
        spinner.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {

            @Override public void onNothingSelected(MaterialSpinner spinner) {
                Snackbar.make(spinner, "Nothing selected", Snackbar.LENGTH_LONG).show();
            }
        });*/

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

    public void animateFAB(){

        if(isFabOpen){

            mFabButton.startAnimation(rotate_backward);
            mSearchFab.startAnimation(fab_close);
            mListFab.startAnimation(fab_close);
            mSearchFab.setClickable(false);
            mListFab.setClickable(false);
            isFabOpen = false;

        } else {

            mFabButton.startAnimation(rotate_forward);
            mSearchFab.startAnimation(fab_open);
            mListFab.startAnimation(fab_open);
            mSearchFab.setClickable(true);
            mListFab.setClickable(true);
            isFabOpen = true;

        }
    }

    private View.OnClickListener onFabButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            int id = v.getId();
            switch (id){
                case R.id.fab:
                    if (searchView.isOpen()) {
                        searchView.closeSearch();
                        animateFAB();
                    }
                    animateFAB();
                    break;
                case R.id.fab1:
                    if (searchView.isOpen()) {
                        searchView.closeSearch();
                    } else {
                        searchView.openSearch();
                    }
                    animateFAB();
                    break;
                case R.id.fab2:
                    popup = new PopupMenu(getContext(), v);
                    popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            Toast.makeText(getContext(), "Clicked popup menu item " + item.getTitle(),
                                    Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    });
                    popup.show();
                    animateFAB();
                    break;
            }
        }
    };

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
