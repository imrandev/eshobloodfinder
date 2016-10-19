package com.app.appathon.blooddonateapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.appathon.blooddonateapp.OnBackPressedListener;
import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.adapter.TabsAdapter;
import com.app.appathon.blooddonateapp.model.TabsItem;

import java.util.ArrayList;
import java.util.List;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocatingDonors#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocatingDonors extends Fragment implements MaterialTabListener,OnBackPressedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    MaterialTabHost tabHost;
    ViewPager viewPager;
    private List<TabsItem> mTabs = new ArrayList<>();
    private TabsAdapter tAdapter;

    public LocatingDonors() {
        // Required empty public constructor
        createTabsItem();
    }

    private void createTabsItem() {
        mTabs.add(new TabsItem("Available", new AvailableDonors()));
        mTabs.add(new TabsItem("All", new AllDonors()));
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LocatingDonors.
     */
    // TODO: Rename and change types and number of parameters
    public static LocatingDonors newInstance(String param1, String param2) {
        LocatingDonors fragment = new LocatingDonors();
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
        View rootView = inflater.inflate(R.layout.fragment_locating_donors, container, false);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        //Adding TabHost
        tabHost = (MaterialTabHost) rootView.findViewById(R.id.materialTabHost);

         //Set an Adapter for the View Pager
        tAdapter = new TabsAdapter(getChildFragmentManager(),mTabs);
        viewPager.setOffscreenPageLimit(mTabs.size());
        viewPager.setAdapter(tAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // when user do a swipe the selected tab change
                tabHost.setSelectedNavigationItem(position);
            }
        });

        // insert all tabs from pagerAdapter data
        for (int i = 0; i < tAdapter.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab()
                            .setText(tAdapter.getPageTitle(i))
                            .setTabListener(this)
            );
        }
        return rootView;
    }

    @Override
    public void onTabSelected(MaterialTab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab tab) {

    }

    @Override
    public void onTabUnselected(MaterialTab tab) {

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
