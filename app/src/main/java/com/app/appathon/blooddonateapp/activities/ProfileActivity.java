package com.app.appathon.blooddonateapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.adapter.PlacesAutoCompleteAdapter;
import com.app.appathon.blooddonateapp.model.ProfileSecurity;
import com.app.appathon.blooddonateapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements ValueEventListener {

    private FirebaseAuth mAuth;
    private ArrayList<User> userArrayList = new ArrayList<>();
    private TextView name, email, bloodType, phone, address, lastDonate, state, gender;
    private FirebaseUser firebaseUser;
    private Switch mSwitch;
    private View snackView;
    private String user_phone, user_area;
    private int user_donate;
    private MaterialSpinner materialSpinner;
    private ArrayList<Integer> month = new ArrayList<>();
    private EditText phoneEdit;
    private AutoCompleteTextView auto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        for(int i=0; i<13; i++){
            month.add(i);
        }

        //Initializing Firebase Database Reference
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        //Custom font
        Typeface Helvetica = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeue.ttf");

        //Initializing Layout Components
        name = (TextView) findViewById(R.id.profile_name);
        email = (TextView) findViewById(R.id.user_email);
        bloodType = (TextView) findViewById(R.id.user_blood);
        phone = (TextView) findViewById(R.id.user_phone);
        lastDonate = (TextView) findViewById(R.id.donate_date);
        address = (TextView) findViewById(R.id.user_area);
        mSwitch = (Switch) findViewById(R.id.security_switch);
        snackView = findViewById(R.id.activity_profile);
        state = (TextView) findViewById(R.id.state);
        gender = (TextView) findViewById(R.id.user_gender);

        name.setTypeface(Helvetica);
        email.setTypeface(Helvetica);
        bloodType.setTypeface(Helvetica);
        phone.setTypeface(Helvetica);
        lastDonate.setTypeface(Helvetica);
        address.setTypeface(Helvetica);
        gender.setTypeface(Helvetica);

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ProfileSecurity security = new ProfileSecurity(isChecked);
                    mDatabase.child("users").child(firebaseUser.getUid()).child("security").setValue(security);
                    state.setText("Private");
                } else {
                    ProfileSecurity security = new ProfileSecurity(isChecked);
                    mDatabase.child("users").child(firebaseUser.getUid()).child("security").setValue(security);
                    state.setText("Public");
                }
            }
        });

        mDatabase.child("users").addValueEventListener(this);

    }
    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        finish();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (!userArrayList.isEmpty()){
            userArrayList.clear();
        }

        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
            User user = snapshot.getValue(User.class);
            if (snapshot.getKey().equals(firebaseUser.getUid())){
                user_phone = user.getPhone();
                user_donate = user.getLastDonate();
                user_area = user.getAddress();
                name.setText(user.getName());
                email.setText(user.getEmail());
                bloodType.setText(user.getBloodGroup());
                phone.setText(user.getPhone());
                address.setText(user.getAddress());
                gender.setText(user.getGender());
                lastDonate.setText(String.valueOf(user.getLastDonate()));

                if (snapshot.hasChild("security")){
                    DataSnapshot data = snapshot.child("security");
                    boolean isVisible = Boolean.parseBoolean(data.child("phoneHidden").getValue().toString());
                    if (isVisible){
                        mSwitch.setChecked(true);
                    } else {
                        mSwitch.setChecked(false);
                    }
                }
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        showSnackMessage(databaseError.getMessage());
    }

    private void showSnackMessage(String message) {
        int color = Color.RED;
        int TIME_OUT = Snackbar.LENGTH_SHORT;

        Snackbar snackbar = Snackbar
                .make(snackView, message, TIME_OUT);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_edit:
                initUpdateLayout();
                return true;
            default:
                return false;
        }
    }

    private void initUpdateLayout() {
        MaterialDialog.Builder builder  = new MaterialDialog.Builder(this)
                .title("Edit Profile")
                .customView(R.layout.edit_profile, true)
                .titleColorRes(android.R.color.darker_gray)
                .positiveText("Update")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        updateProfile();
                    }
                });

        MaterialDialog dialog = builder.build();
        phoneEdit = (EditText) dialog.findViewById(R.id.input_phone);
        phoneEdit.setText(user_phone);

        materialSpinner = (MaterialSpinner) dialog.findViewById(R.id.donateDate);
        materialSpinner.setItems(month);
        materialSpinner.setSelectedIndex(user_donate);

        auto = (AutoCompleteTextView) dialog.findViewById(R.id.input_area);
        auto.setText(user_area);
        PlacesAutoCompleteAdapter adapter = new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_list_item);
        adapter.notifyDataSetChanged();
        auto.setAdapter(adapter);
        dialog.show();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void updateProfile() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        String userId = user.getUid();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        //update phone
        String phone = phoneEdit.getText().toString();
        database.child("users").child(userId).child("phone").setValue(phone);

        //update last donate
        int donate = Integer.parseInt(materialSpinner.getItems().get(materialSpinner.getSelectedIndex()).toString());
        database.child("users").child(userId).child("lastDonate").setValue(donate);

        //update area
        String area = auto.getText().toString();
        database.child("users").child(userId).child("address").setValue(area);
    }
}
