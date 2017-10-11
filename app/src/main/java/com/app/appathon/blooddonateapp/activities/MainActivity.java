package com.app.appathon.blooddonateapp.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.app.appathon.blooddonateapp.Config.Config;
import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.fragments.LocatingDonors;
import com.app.appathon.blooddonateapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;


public class MainActivity extends AppCompatActivity implements ValueEventListener, SearchView.OnQueryTextListener {

    private FragmentTransaction fragmentTransaction;
    public MaterialSpinner spinner;
    private Fragment fragment;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private TextView headerText;
    private SearchView searchView;
    public FragmentCommunicator fragmentCommunicator;
    public int someIntValue =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Handle Toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        final Typeface ThemeFont = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeue.ttf");

        //Initializing Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").addValueEventListener(this);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        //Setting The Tag for sending notification
        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        String userID = status.getSubscriptionStatus().getUserId();
        OneSignal.setSubscription(true);
        mDatabase.child("users").child(firebaseUser.getUid()).child("sendNotification").setValue(userID);

        //Inflating NavHeader
        View navHeader = View.inflate(this, R.layout.navbar_head, null);
        headerText = (TextView)navHeader.findViewById(R.id.user_name);
        headerText.setTypeface(ThemeFont);

        //Initializing Navigation Drawer
        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHeader(navHeader)
                .withTranslucentStatusBar(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Find Donors")
                                .withIcon(GoogleMaterial.Icon.gmd_search)
                                .withIconColor(Color.GRAY)
                                .withTextColor(Color.GRAY)
                                .withSelectedTextColor(Color.DKGRAY)
                                .withTypeface(ThemeFont)
                                .withIdentifier(1),
                        new PrimaryDrawerItem()
                                .withName("Inbox")
                                .withIcon(GoogleMaterial.Icon.gmd_inbox)
                                .withIconColor(Color.GRAY)
                                .withTextColor(Color.GRAY)
                                .withSelectedTextColor(Color.DKGRAY)
                                .withTypeface(ThemeFont)
                                .withIdentifier(2),
                        new SectionDrawerItem().withName("More")
                                .withTypeface(ThemeFont)
                                .withTextColor(Color.GRAY),
                        new PrimaryDrawerItem()
                                .withName("My Profile")
                                .withIcon(GoogleMaterial.Icon.gmd_account)
                                .withIconColor(Color.GRAY)
                                .withTextColor(Color.GRAY)
                                .withSelectedTextColor(Color.DKGRAY)
                                .withTypeface(ThemeFont)
                                .withIdentifier(3),
                        new PrimaryDrawerItem()
                                .withName("Sign Out")
                                .withIcon(FontAwesome.Icon.faw_sign_out)
                                .withIconColor(Color.GRAY)
                                .withTextColor(Color.GRAY)
                                .withSelectedTextColor(Color.DKGRAY)
                                .withTypeface(ThemeFont)
                                .withIdentifier(5)
                ) // add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {

                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == 1) {
                                fragment = LocatingDonors.newInstance();
                                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
                                fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
                                toolbar.setSubtitle("Find Donors");
                            } else if (drawerItem.getIdentifier() == 3) {
                                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                finish();
                            } else if (drawerItem.getIdentifier() == 5) {
                                FirebaseAuth.getInstance().signOut();
                                OneSignal.setSubscription(false);
                                startActivity(new Intent(MainActivity.this, SignInActivity.class));
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                finish();
                            } else if (drawerItem.getIdentifier() == 2) {
                                startActivity(new Intent(MainActivity.this,InboxActivity.class));
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                finish();
                            }
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withSelectedItem(1)
                // build only the view of the Drawer (don't inflate it automatically in our layout which is done with .build())
                .build();
        result.setSelection(1, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_map:
                startActivity(new Intent(MainActivity.this, NearbyDonorActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                finish();
                return true;
            case R.id.menu_search:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
            User uData = snapshot.getValue(User.class);
            if (uData != null){
                if (snapshot.getKey().equals(firebaseUser.getUid())){
                    String displayName = uData.getName();
                    headerText.setText(displayName);
                    Config.CURRENT_USERNAME = uData.getName();
                    Config.CURRENT_USER_PHONE = uData.getPhone();
                }
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        fragmentCommunicator.passDataToFragment(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public interface FragmentCommunicator {
        void passDataToFragment(String value);
    }
}
