package com.app.appathon.blooddonateapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.fragments.LocatingDonors;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class MainActivity extends AppCompatActivity implements BackHandledFragment.BackHandlerInterface {

    private BackHandledFragment selectedFragment;
    private static final String[] LOCATION = {
            "Dhaka", "Chittagong", "Rajshahi", "Barisal", "Comilla", "Sylhet", "Khulna"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Handle Toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        //set the back arrow in the toolbar
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withSliderBackgroundColorRes(R.color.drawerColor)
                .withTranslucentStatusBar(false)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Locating Donors")
                                .withIcon(GoogleMaterial.Icon.gmd_google_maps)
                                .withIconColor(Color.WHITE)
                                .withTextColor(Color.WHITE)
                                .withSelectedTextColor(Color.WHITE)
                                .withSelectedColor(getResources().getColor(R.color.selectedColor))
                                .withIdentifier(1),
                        new PrimaryDrawerItem()
                                .withName("Enquiry in Hospitals")
                                .withIcon(FontAwesome.Icon.faw_home)
                                .withIconColor(Color.WHITE)
                                .withTextColor(Color.WHITE)
                                .withSelectedTextColor(Color.WHITE)
                                .withSelectedColor(getResources().getColor(R.color.selectedColor))
                                .withIdentifier(2),
                        new PrimaryDrawerItem()
                                .withName("Favorites")
                                .withIcon(FontAwesome.Icon.faw_heart)
                                .withIconColor(Color.WHITE)
                                .withTextColor(Color.WHITE)
                                .withSelectedTextColor(Color.WHITE)
                                .withSelectedColor(getResources().getColor(R.color.selectedColor))
                                .withIdentifier(3),
                        new SectionDrawerItem()
                                .withName("More...")
                                .withTextColor(Color.WHITE),
                        new SecondaryDrawerItem()
                                .withName("Settings")
                                .withIcon(GoogleMaterial.Icon.gmd_settings)
                                .withIconColor(Color.WHITE)
                                .withTextColor(Color.WHITE)
                                .withSelectedTextColor(Color.WHITE)
                                .withSelectedColor(getResources().getColor(R.color.selectedColor))
                                .withIdentifier(4),
                        new SectionDrawerItem()
                                .withName("SOCIAL")
                                .withTextColor(Color.WHITE),
                        new SecondaryDrawerItem()
                                .withName("Facebook")
                                .withIcon(FontAwesome.Icon.faw_facebook)
                                .withIconColor(Color.WHITE)
                                .withTextColor(Color.WHITE)
                                .withSelectedTextColor(Color.WHITE)
                                .withSelectedColor(getResources().getColor(R.color.selectedColor))
                                .withIdentifier(5),
                        new SecondaryDrawerItem()
                                .withName(R.string.drawer_item_contact)
                                .withIcon(GoogleMaterial.Icon.gmd_format_color_fill)
                                .withIconColor(Color.WHITE)
                                .withTextColor(Color.WHITE)
                                .withSelectedTextColor(Color.WHITE)
                                .withSelectedColor(getResources().getColor(R.color.selectedColor))
                                .withTag("Bullhorn"),
                        new SecondaryDrawerItem()
                                .withName("Messaging")
                                .withIcon(GoogleMaterial.Icon.gmd_email)
                                .withIconColor(Color.WHITE)
                                .withTextColor(Color.WHITE)
                                .withSelectedTextColor(Color.WHITE)
                                .withSelectedColor(getResources().getColor(R.color.selectedColor))
                                .withTag("Bullhorn"),
                        new SectionDrawerItem().withName("Account")
                                .withTextColor(Color.WHITE),
                        new SecondaryDrawerItem()
                                .withName("Sign up")
                                .withIcon(GoogleMaterial.Icon.gmd_sign_in)
                                .withIconColor(Color.WHITE)
                                .withTextColor(Color.WHITE)
                                .withSelectedTextColor(Color.WHITE)
                                .withSelectedColor(getResources().getColor(R.color.selectedColor))
                                .withIdentifier(7)
                ) // add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {

                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            Fragment fragment = null;
                            if (drawerItem.getIdentifier() == 1) {
                                fragment = new LocatingDonors();
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                                toolbar.setSubtitle("Locating Donors");
                            } else if (drawerItem.getIdentifier() == 7) {
                                startActivity(new Intent(MainActivity.this,SignUpActivity.class));
                            } else if (drawerItem.getIdentifier() == 3) {
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
    public void onBackPressed() {
        if(selectedFragment == null || !selectedFragment.onBackPressed()) {
            // Selected fragment did not consume the back press event.
            super.onBackPressed();
        }
    }

    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.selectedFragment = selectedFragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);

        MenuItem item = menu.findItem(R.id.spinner);
        MaterialSpinner spinner = (MaterialSpinner) MenuItemCompat.getActionView(item);
        spinner.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        spinner.setItems(LOCATION);
        return true;
    }
}
