package com.app.appathon.blooddonateapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.appathon.blooddonateapp.OnBackPressedListener;
import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.fragments.EnquiryHospitals;
import com.app.appathon.blooddonateapp.fragments.LocatingDonors;
import com.app.appathon.blooddonateapp.services.FetchAddressIntentService;
import com.app.appathon.blooddonateapp.utils.Constants;
import com.app.appathon.blooddonateapp.utils.GeocoderHandler;
import com.app.appathon.blooddonateapp.utils.LocationAddress;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,
        ResultCallback<LocationSettingsResult> {

    protected static final String TAG = "activity-main";

    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";

    private FragmentTransaction fragmentTransaction;
    private static final String[] LOCATION = null;
    private ArrayList<String> LOCATION_LIST;
    private MaterialSpinner spinner;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    /**
     * Tracks whether the user has requested an address. Becomes true when the user requests an
     * address and false when the address (or an error message) is delivered.
     * The user requests an address by pressing the Fetch Address button. This may happen
     * before GoogleApiClient connects. This activity uses this boolean to keep track of the
     * user's intent. If the value is true, the activity tries to fetch the address as soon as
     * GoogleApiClient connects.
     */
    protected boolean mAddressRequested;

    /**
     * The formatted location address.
     */
    protected String mAddressOutput;

    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;

    /**
     * Displays the location address.
     */
    protected TextView mLocationAddressTextView;

    /**
     * Visible while the address is being fetched.
     */
    ProgressBar mProgressBar;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    protected LocationSettingsRequest mLocationSettingsRequest;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    protected LocationRequest locationRequest;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    protected Boolean mRequestingLocationUpdates;

    private AddressResultReceiver addressResultReceiver;

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
                                .withIcon(FontAwesome.Icon.faw_hospital_o)
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
                                //FragmentManager fragmentManager = getSupportFragmentManager();
                                //fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right);
                                fragmentTransaction.replace(R.id.fragment_container,fragment).commit();
                                toolbar.setSubtitle("Locating Donors");
                            } else if (drawerItem.getIdentifier() == 7) {
                                startActivity(new Intent(MainActivity.this,SignUpActivity.class));
                                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
                            } else if (drawerItem.getIdentifier() == 2) {
                                fragment = new EnquiryHospitals();
                                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right);
                                fragmentTransaction.replace(R.id.fragment_container,fragment).commit();
                                toolbar.setSubtitle("Enquiry in Hospitals");
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
        updateValuesFromBundle(savedInstanceState);


        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        checkLocationSettings();

        startIntentService();

        updateUIWidgets();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);

        MenuItem item = menu.findItem(R.id.spinner);
        LOCATION_LIST = new ArrayList<String>();
        spinner = (MaterialSpinner) MenuItemCompat.getActionView(item);
        spinner.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        return true;
    }

    /**
     * Updates fields based on data stored in the bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Check savedInstanceState to see if the address was previously requested.
            if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)) {
                mAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
            }
            // Check savedInstanceState to see if the location address string was previously found
            // and stored in the Bundle. If it was found, display the address string in the UI.
            if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)) {
                mAddressOutput = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
                displayAddressOutput();
            }
        }
    }

    /**
     * Builds a GoogleApiClient. Uses {@code #addApi} to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Runs when user clicks the Fetch Address button. Starts the service to fetch the address if
     * GoogleApiClient is connected.
     */
    public void fetchAddressButtonHandler(View view) {
        // We only start the service to fetch the address if GoogleApiClient is connected.
        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            startIntentService();
        }
        // If GoogleApiClient isn't connected, we process the user's request by setting
        // mAddressRequested to true. Later, when GoogleApiClient connects, we launch the service to
        // fetch the address. As far as the user is concerned, pressing the Fetch Address button
        // immediately kicks off the process of getting the address.
        mAddressRequested = true;
        updateUIWidgets();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                // Determine whether a Geocoder is available.
                if (!Geocoder.isPresent()) {
                    Toast.makeText(this, R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
                    return;
                }
                // It is possible that the user presses the button to get the address before the
                // GoogleApiClient object successfully connects. In such a case, mAddressRequested
                // is set to true, but no attempt is made to fetch the address (see
                // fetchAddressButtonHandler()) . Instead, we start the intent service here if the
                // user has requested an address, since we now have a connection to GoogleApiClient.
                if (mAddressRequested) {
                    startIntentService();
                }
            }
        } catch (SecurityException ex) {
            Log.i(TAG, ex.getMessage());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save whether the address has been requested.
        savedInstanceState.putBoolean(ADDRESS_REQUESTED_KEY, mAddressRequested);

        // Save the address string.
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mAddressOutput);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }

    /**
     * Updates the address in the UI.
     */
    protected void displayAddressOutput() {
        //mLocationAddressTextView.setText(mAddressOutput);
        LOCATION_LIST.add(mAddressOutput);
        spinner.setItems(LOCATION_LIST);

    }

    /**
     * Toggles the visibility of the progress bar. Enables or disables the Fetch Address button.
     */
    private void updateUIWidgets() {
//        if (mAddressRequested) {
//            mProgressBar.setVisibility(ProgressBar.VISIBLE);
//        } else {
//            mProgressBar.setVisibility(ProgressBar.GONE);
//        }
    }

    /**
     * Shows a toast with the given text.
     */
    protected void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "All location settings are satisfied.");
                startIntentService();
                //startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    protected void startLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,
                    mLocationRequest,
                    this
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    mRequestingLocationUpdates = true;
                }
            });
        } catch (SecurityException ex) {
            Log.i(TAG, ex.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {

            if (resultCode == RESULT_OK) {

                Toast.makeText(getApplicationContext(), "GPS enabled", Toast.LENGTH_LONG).show();
            } else {

                Toast.makeText(getApplicationContext(), "GPS is not enabled", Toast.LENGTH_LONG).show();
            }

        }
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                showToast(getString(R.string.address_found));
            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;
            updateUIWidgets();
        }
    }
}
