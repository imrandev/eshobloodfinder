package com.app.appathon.blooddonateapp.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.adapter.PlacesAutoCompleteAdapter;
import com.app.appathon.blooddonateapp.helper.InterstitialAdsHelper;
import com.app.appathon.blooddonateapp.model.User;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;
import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity {

    private EditText etName, etEmail, donateET;
    private AutoCompleteTextView areaET;
    private MaterialSpinner genderSpinner,bldGrpSpinner;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ImageView calender;

    private InterstitialAdsHelper interAdsActivity;

    private ArrayList<String> bldType = new ArrayList<>();
    private String[] bldGrp = {"A+","A-","B+","B-","AB+","AB-","O+","O-"};
    private ArrayList<String> gender = new ArrayList<>();
    private String username;

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

        interAdsActivity = new InterstitialAdsHelper(this);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Typeface ThemeFont = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeue.ttf");
        TextView signMe = (TextView) findViewById(R.id.sin_me);
        signMe.setTypeface(ThemeFont);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        for(int i=0; i<bldGrp.length; i++){
            bldType.add(bldGrp[i]);
        }

        gender.add("Male");
        gender.add("Female");

        etName = (EditText) findViewById(R.id.sup_name);
        etEmail = (EditText) findViewById(R.id.sup_email);
        bldGrpSpinner = (MaterialSpinner) findViewById(R.id.input_blood);
        areaET = (AutoCompleteTextView) findViewById(R.id.input_area);
        donateET = (EditText) findViewById(R.id.donateDate);
        calender = (ImageView) findViewById(R.id.calender);
        donateET.setEnabled(false);

        genderSpinner = (MaterialSpinner) findViewById(R.id.gender);
        genderSpinner.setItems(gender);
        bldGrpSpinner.setItems(bldType);

        PlacesAutoCompleteAdapter adapter = new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_list_item);
        adapter.notifyDataSetChanged();
        areaET.setAdapter(adapter);

        areaET.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String placeName = (String) parent.getItemAtPosition(position);

            }
        });

        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

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

    private void onAuthSuccess() {
        String email = etEmail.getText().toString();
        if(!TextUtils.isEmpty(email)){
            username = usernameFromEmail(email);
        }
        String name = etName.getText().toString();
        String bldGrp = bldGrpSpinner.getItems().get(bldGrpSpinner.getSelectedIndex()).toString();
        String area = areaET.getText().toString();

        String dDate;
        if (TextUtils.isEmpty(donateET.getText().toString())){
            dDate = "Never";
        } else {
            dDate = donateET.getText().toString();
        }
        String gender = genderSpinner.getItems().get(genderSpinner.getSelectedIndex()).toString();

        String uId="";
        String phone="";
        if(mAuth!=null){
            uId = mAuth.getCurrentUser().getUid();
            phone = mAuth.getCurrentUser().getPhoneNumber();
        }
        //write new user
        writeNewUser(uId, username, name, email, gender, phone, area, bldGrp, dDate);

        interAdsActivity.launchInter();
        interAdsActivity.loadInterstitial();
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

    private void showDatePicker() {
        CalendarDatePickerDialogFragment dialog = new CalendarDatePickerDialogFragment()
                .setOnDateSetListener(dateSetListener)
                .setThemeDark();
        dialog.show(getSupportFragmentManager(), "DATE_PICKER_TAG");
    }

    CalendarDatePickerDialogFragment.OnDateSetListener dateSetListener = new CalendarDatePickerDialogFragment.OnDateSetListener() {
        @Override
        public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
            // Set date from user input.
            monthOfYear = monthOfYear +1;
            String date_of_birth = dayOfMonth + "/" + monthOfYear + "/" + year;
            donateET.setText(date_of_birth);
        }
    };

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
//        if (TextUtils.isEmpty(numberET.getText().toString())) {
//            numberET.setError("Required");
//            result = false;
//        } else {
//            numberET.setError(null);
//        }

        return result;
    }

    private void writeNewUser(String userId, String uname, String name, String email, String gender, String phone, String address, String blood, String date) {
        User user = new User(userId, uname, name, email, phone,
                address, blood, date, gender, 0, 0,"");
        mDatabase.child("users").child(userId).setValue(user);
        Toast.makeText(SignUpActivity.this, "Successfully account created", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.signup_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_add:
                if (validateForm()){
                    onAuthSuccess();
                }
                return true;
            default:
                return false;
        }
    }
}
