package com.app.appathon.blooddonateapp.activities;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.adapter.InboxAdapter;
import com.app.appathon.blooddonateapp.adapter.TabsAdapter;
import com.app.appathon.blooddonateapp.fragments.AllDonors;
import com.app.appathon.blooddonateapp.fragments.AvailableDonors;
import com.app.appathon.blooddonateapp.fragments.IncomingFragment;
import com.app.appathon.blooddonateapp.fragments.OutgoingFragment;
import com.app.appathon.blooddonateapp.model.Inbox;
import com.app.appathon.blooddonateapp.model.TabsItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class InboxActivity extends AppCompatActivity implements MaterialTabListener {

    private MaterialTabHost tabHost;
    private ViewPager viewPager;
    private List<TabsItem> mTabs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mTabs.add(new TabsItem("Received", IncomingFragment.newInstance()));
        mTabs.add(new TabsItem("Sent", OutgoingFragment.newInstance()));

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        //Adding TabHost
        tabHost = (MaterialTabHost) findViewById(R.id.materialTabHost);

        //Set an Adapter for the View Pager
        TabsAdapter tAdapter = new TabsAdapter(getSupportFragmentManager(), mTabs);
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
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        startActivity(new Intent(InboxActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        finish();
        return true;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        startActivity(new Intent(InboxActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        finish();
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
}
