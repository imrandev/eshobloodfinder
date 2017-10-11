package com.app.appathon.blooddonateapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.adapter.PlacesAutoCompleteAdapter;
import com.app.appathon.blooddonateapp.model.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etName, etEmail, bldGrpET;
    private AutoCompleteTextView areaET;
    private MaterialSpinner materialSpinner, genderSpinner;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;

    private ArrayList<Integer> month = new ArrayList<>();
    private ArrayList<String> gender = new ArrayList<>();
    private LatLng latLng;
    private EditText verifyCode,verifyPhone;
    private ImageButton sendCode;
    private String username;
    private MaterialDialog dialog;
    private static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Typeface ThemeFont = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeue.ttf");
        TextView signMe = (TextView) findViewById(R.id.sin_me);
        signMe.setTypeface(ThemeFont);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        for(int i=0; i<13; i++){
            month.add(i);
        }

        gender.add("Male");
        gender.add("Female");

        etName = (EditText) findViewById(R.id.sup_name);
        etEmail = (EditText) findViewById(R.id.sup_email);
        bldGrpET = (EditText) findViewById(R.id.input_blood);
        areaET = (AutoCompleteTextView) findViewById(R.id.input_area);
        FloatingActionButton btn_signUp = (FloatingActionButton) findViewById(R.id.btn_signUp);

        materialSpinner = (MaterialSpinner) findViewById(R.id.donateDate);
        genderSpinner = (MaterialSpinner) findViewById(R.id.gender);
        materialSpinner.setItems(month);
        genderSpinner.setItems(gender);

        PlacesAutoCompleteAdapter adapter = new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_list_item);
        adapter.notifyDataSetChanged();
        areaET.setAdapter(adapter);

        areaET.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String placeName = (String) parent.getItemAtPosition(position);

            }
        });

        btn_signUp.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        startActivity(new Intent(this, SignInActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, SignInActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signUp:
                if (validateForm()){
                    onAuthSuccess();
                }
                break;
            default:
                break;
        }
    }

    private void onAuthSuccess() {
        String email = etEmail.getText().toString();
        if(!TextUtils.isEmpty(email)){
            username = usernameFromEmail(email);
        }
        String name = etName.getText().toString();
        String bldGrp = bldGrpET.getText().toString();
        String area = areaET.getText().toString();

        latLng = getLocationFromAddress(area);

        int dDate = materialSpinner.getSelectedIndex();
        String gender = genderSpinner.getItems().get(genderSpinner.getSelectedIndex()).toString();

        String uId="";
        String phone="";
        if(mAuth!=null){
            uId = mAuth.getCurrentUser().getUid();
            phone = mAuth.getCurrentUser().getPhoneNumber();
        }
        //write new user
        writeNewUser(uId, username, name, email, gender, phone, area, bldGrp, dDate);

        //Go to MainActivity
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private boolean validateForm() {
        boolean result = true;

        if (TextUtils.isEmpty(etName.getText().toString())) {
            etName.setError("Required");
            result = false;
        } else {
            etName.setError(null);
        }

        if (TextUtils.isEmpty(areaET.getText().toString())) {
            areaET.setError("Required");
            result = false;
        } else {
            areaET.setError(null);
        }

        if (TextUtils.isEmpty(bldGrpET.getText().toString())) {
            bldGrpET.setError("Required");
            result = false;
        } else {
            bldGrpET.setError(null);
        }

//        if (TextUtils.isEmpty(numberET.getText().toString())) {
//            numberET.setError("Required");
//            result = false;
//        } else {
//            numberET.setError(null);
//        }

        return result;
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

    private void writeNewUser(String userId, String uname, String name, String email, String gender, String phone, String address, String blood, int date) {
        User user = new User(userId, uname, name, email, phone,
                address, blood, date, gender, latLng.latitude, latLng.longitude,"");
        mDatabase.child("users").child(userId).setValue(user);
        Toast.makeText(SignUpActivity.this, "Successfully account created", Toast.LENGTH_SHORT).show();
    }
}
