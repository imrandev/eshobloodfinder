package com.app.appathon.blooddonateapp.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.adapter.DBAdapter;
import com.jaredrummler.materialspinner.MaterialSpinner;

public class SignUpActivity extends AppCompatActivity {

    private DBAdapter dbHelper;
    Context con;

    EditText nameET, emailET, pwd, numberET, bldGrpET, districtET, areaET;
    Button signUp;
    private MaterialSpinner materialSpinner;

    String[] monthArray = {"1","2","3","4","5","6","7","8","9","10","11","12"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        con = this;
        dbHelper = new DBAdapter(con);

        nameET = (EditText)findViewById(R.id.input_name);
        emailET = (EditText)findViewById(R.id.input_email);
        numberET = (EditText)findViewById(R.id.input_phone);
        bldGrpET = (EditText)findViewById(R.id.input_blood);
        districtET = (EditText)findViewById(R.id.input_district);
        areaET = (EditText)findViewById(R.id.input_area);
        signUp = (Button)findViewById(R.id.btn_signup);
        materialSpinner = (MaterialSpinner) findViewById(R.id.donateDate);
        materialSpinner.setItems(monthArray);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameET.getText().toString();
                String email = emailET.getText().toString();
                String number = numberET.getText().toString();
                String bldGrp = bldGrpET.getText().toString();
                String area = areaET.getText().toString();
                String dist = districtET.getText().toString();
                String dDate = materialSpinner.getText().toString();

                dbHelper.open();
                dbHelper.insertInfo(name, email, bldGrp, number, dist, area, dDate);
                dbHelper.close();

                Toast.makeText(con, "Data Successfully Inserted", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    @Override
    public void onBackPressed(){
        startActivity(new Intent(SignUpActivity.this,MainActivity.class));
        overridePendingTransition(R.anim.anim_enter,R.anim.anim_leave);
        finish();
        //super.onBackPressed();
    }
}
