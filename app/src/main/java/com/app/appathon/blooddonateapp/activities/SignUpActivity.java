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
    String username;
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
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    verifyPhone.setError("Invalid phone number.");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
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
        startActivity(new Intent(this, SignInActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signUp:
                if (validateForm()){
                    userSignUp();
                }
                break;
            default:
                break;
        }
    }

    private void userSignUp() {

        MaterialDialog.Builder builder  = new MaterialDialog.Builder(this)
                .title("Phone Verification")
                .customView(R.layout.dialog_code, true)
                .backgroundColor(Color.parseColor("#4D4D4D"))
                .titleColorRes(android.R.color.white)
                .positiveText("Submit")
                .canceledOnTouchOutside(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String code = verifyCode.getText().toString();
                        if (TextUtils.isEmpty(code)) {
                            verifyCode.setError("Cannot be empty.");
                            return;
                        }
                        verifyPhoneNumberWithCode(mVerificationId, code);
                    }
                });

        MaterialDialog dialog = builder.build();
        verifyCode = (EditText) dialog.findViewById(R.id.verify_code);
        verifyPhone = (EditText) dialog.findViewById(R.id.verify_phone);
        sendCode = (ImageButton) dialog.findViewById(R.id.btnSend);
        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validatePhoneNumber()) {
                    return;
                }
                startPhoneNumberVerification(verifyPhone.getText().toString());
            }
        });
        dialog.show();
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            onAuthSuccess(user);
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                verifyCode.setError("Invalid code.");
                            }
                        }
                    }
                });
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void onAuthSuccess(FirebaseUser user) {
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
        //write new user
        writeNewUser(user.getUid(), username, name, email, gender, user.getPhoneNumber(), area, bldGrp, dDate);

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

    private boolean validatePhoneNumber() {
        String phoneNumber = verifyPhone.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            verifyPhone.setError("Invalid phone number.");
            return false;
        }
        return true;
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
