package com.app.appathon.blooddonateapp.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.app.BloodApplication;
import com.app.appathon.blooddonateapp.database.FirebaseDatabaseHelper;
import com.app.appathon.blooddonateapp.helper.ConnectivityReceiver;
import com.app.appathon.blooddonateapp.interfaces.TrackUserLocation;
import com.app.appathon.blooddonateapp.model.User;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.nlopez.smartlocation.OnActivityUpdatedListener;
import io.nlopez.smartlocation.OnGeofencingTransitionListener;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.geofencing.utils.TransitionGeofence;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;

public class NearbyDonorActivity extends AppCompatActivity implements OnMapReadyCallback,
        ConnectivityReceiver.ConnectivityReceiverListener, ConnectivityReceiver.GpsStatusReceiverListener,
        GoogleMap.OnInfoWindowClickListener, FirebaseDatabaseHelper.AvailableDonorInterface,
        OnLocationUpdatedListener, OnActivityUpdatedListener, OnGeofencingTransitionListener {

    private View snackView;
    private boolean isConnected, isGpsEnabled;
    private GoogleMap gMap;
    private ArrayList<User> userArrayList = new ArrayList<>();
    private LocationGooglePlayServicesProvider provider;
    private boolean submitPressed = true;

    private static final int LOCATION_PERMISSION_ID = 1001;
    private String userId;
    private String SUBMIT_PRESSED = "OPEN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_donor);

        //Initializing Firebase Database Reference
        FirebaseDatabaseHelper databaseHelper = new FirebaseDatabaseHelper(this, this);
        databaseHelper.getAvailableUserListData();

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        isConnected = ConnectivityReceiver.isConnected();
        isGpsEnabled = ConnectivityReceiver.isGPSConnected();
        snackView = findViewById(R.id.mapFragment);

        if (gMap == null) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    SupportMapFragment mapFragment = new SupportMapFragment();
                    FragmentManager fm = getSupportFragmentManager();
                    fm.beginTransaction()
                            .replace(R.id.map, mapFragment).commit();
                    mapFragment.getMapAsync(NearbyDonorActivity.this);
                }
            }, 700);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        startActivity(new Intent(NearbyDonorActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(NearbyDonorActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
        outState.putBoolean(SUBMIT_PRESSED, submitPressed);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        setSnackMessage(isConnected);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;
        gMap.setOnInfoWindowClickListener(this);

        if (ContextCompat.checkSelfPermission(NearbyDonorActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NearbyDonorActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
            return;
        } else {
            startLocation();
        }
        showLast();
    }

    private void startLocation() {

        provider = new LocationGooglePlayServicesProvider();
        provider.setCheckLocationSettings(true);
        SmartLocation smartLocation = new SmartLocation.Builder(this).logging(true).build();
        smartLocation.location(provider).start(this);
        smartLocation.activity().start(this);
    }

    private void showLast() {
        Location lastLocation = SmartLocation.with(this).location().getLastLocation();
        if (lastLocation != null) {
            getMapData(gMap, new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
        }

        DetectedActivity detectedActivity = SmartLocation.with(this).activity().getLastActivity();
        if (detectedActivity != null) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_ID && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocation();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (provider != null) {
            provider.onActivityResult(requestCode, resultCode, data);
        }
    }

    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng latLng = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            double lat = location.getLatitude();
            double lng = location.getLongitude();

            latLng = new LatLng(lat, lng);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return latLng;
    }

    // Showing the status in SnackBar
    private void setSnackMessage(boolean isConnected) {
        if (!isConnected) {
            String message = "GPS is disabled";
            showSnackMessage(message);
        } else {
            String message = "GPS is enabled";
            showSnackMessage(message);
        }
    }

    private void showSnackMessage(String message) {
        int color = Color.RED;
        int TIME_OUT = Snackbar.LENGTH_INDEFINITE;


        Snackbar snackbar = Snackbar
                .make(snackView, message, TIME_OUT);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (submitPressed) {
            // register connection status listener
            BloodApplication.getInstance().setConnectivityListener(this);
            BloodApplication.getInstance().setPermissionListener(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SmartLocation.with(NearbyDonorActivity.this).location().stop();
        ComponentName component = new ComponentName(this, ConnectivityReceiver.class);
        //Disable
        getPackageManager().setComponentEnabledSetting(
                component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED , PackageManager.DONT_KILL_APP);
    }

    @Override
    public void onGpsConnectionChanged(boolean isGPSEnabled) {
        if (isGPSEnabled){
            startLocation();
            showLast();
        }
        setSnackMessage(isGPSEnabled);
    }

    public void getMapData(GoogleMap gMap, LatLng latlng) {
        //zoom to current position:
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latlng).zoom(16).build();
        gMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        final Typeface ThemeFont = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeue.ttf");

        if (!userArrayList.isEmpty()) {
            for (int i = 0; i < userArrayList.size(); i++) {
                String blood = userArrayList.get(i).getBloodGroup();
                String userName = userArrayList.get(i).getName();
                String donateDate = String.valueOf(userArrayList.get(i).getLastDonate());

                MarkerOptions markerOption = new MarkerOptions();
                markerOption.snippet("Blood Group : " + blood
                        + "\n"
                        + "Last Donate : " + donateDate
                        + " month(s) ago"
                );

                LatLng latLng = new LatLng(userArrayList.get(i).getLat(), userArrayList.get(i).getLng());
                markerOption.position(latLng);
                markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                markerOption.title(userName);

                final int j = i;

                gMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker arg0) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {

                        View info = View.inflate(NearbyDonorActivity.this,R.layout.item_info_window, null);

                        TextView title = (TextView) info.findViewById(R.id.mtitle);
                        title.setText(marker.getTitle());

                        TextView snippet = (TextView) info.findViewById(R.id.date);
                        snippet.setText(marker.getSnippet());
                        snippet.setTypeface(ThemeFont);

                        TextView button = (TextView) info.findViewById(R.id.msg_thumb);
                        button.setText(String.valueOf(marker.getTitle().charAt(0)));
                        button.setTypeface(ThemeFont);

                        return info;
                    }
                });
                Marker marker = gMap.addMarker(markerOption);
                marker.showInfoWindow();
            }
        } else {

        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        int position = Integer.parseInt(marker.getId().replace("m", ""));
        try {
            goToUserProfile(position);
        } catch (ArrayIndexOutOfBoundsException ex) {
            showSnackMessage(ex.getMessage());
        }
    }

    private void goToUserProfile(int position){
        String email = userArrayList.get(position).email;
        Intent intent = new Intent(NearbyDonorActivity.this, UserProfileActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityUpdated(DetectedActivity detectedActivity) {

    }

    @Override
    public void onGeofenceTransition(TransitionGeofence transitionGeofence) {

    }

    @Override
    public void onLocationUpdated(Location location) {
        showLocation(location);
    }

    private void showLocation(Location location) {
        if (location != null) {
            getMapData(gMap, new LatLng(location.getLatitude(), location.getLongitude()));
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("users").child(userId).child("lat").setValue(location.getLatitude());
            mDatabase.child("users").child(userId).child("lng").setValue(location.getLongitude());
        } else {
            String message = "Sorry! Internal Error!";
            showSnackMessage(message);
        }
    }

    @Override
    public void getAvailableDonorInfo(String id, String email, List<User> users) {
        userArrayList.addAll(users);
        userId = id;
    }

    @Override
    public void onFirebaseInternalError(String error) {
        showSnackMessage(error);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nearby, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_filter:
                return true;
            default:
                return false;
        }
    }
}
