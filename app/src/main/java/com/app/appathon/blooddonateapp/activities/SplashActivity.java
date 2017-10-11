package com.app.appathon.blooddonateapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.app.appathon.blooddonateapp.R;

public class SplashActivity extends Activity {

    SharedPreferences myPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View mSplashImage = findViewById(R.id.splash);
        View mSplashText = findViewById(R.id.splashText);
        Animation splashAnimImage = AnimationUtils.loadAnimation(this, R.anim.splash_anim_img);
        splashAnimImage.setInterpolator(new AccelerateDecelerateInterpolator());
        Animation splashAnimText = AnimationUtils.loadAnimation(this, R.anim.splash_anim);
        splashAnimText.setInterpolator(new AccelerateDecelerateInterpolator());
        mSplashText.startAnimation(splashAnimText);
        mSplashImage.startAnimation(splashAnimImage);

        myPref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);

        int SPLASH_DISPLAY_LENGTH = 700;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(myPref.getInt("CheckLog", 0)==0){
                    startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
                    finish();
                }
                else{
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
                    finish();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
