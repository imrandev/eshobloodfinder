package com.app.appathon.blooddonateapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.app.appathon.blooddonateapp.R;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }
    @Override
    public void onBackPressed(){
        startActivity(new Intent(SignUpActivity.this,MainActivity.class));
        overridePendingTransition(R.anim.anim_enter,R.anim.anim_leave);
        finish();
        //super.onBackPressed();
    }
}
