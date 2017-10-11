package com.app.appathon.blooddonateapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.app.appathon.blooddonateapp.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class SplashActivity extends Activity {

    SharedPreferences myPref;
    private InterstitialAd interstitialAd;
    boolean exitApp = false;

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
                launchInter();
                loadInterstitial();
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

    public void launchInter(){
        interstitialAd =new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.Interstitial));
        //Set the adListener
        interstitialAd.setAdListener(new AdListener() {

            public void onAdLoaded() {
                showAdInter();
            }
            public void onAdFailedToLoad(int errorCode) {
                String message = String.format("onAdFailedToLoad(%s)", getErrorReason(errorCode));

            }
            @Override
            public void onAdClosed() {
                if (exitApp)
                    finish();
            }
        });

    }

    private void showAdInter(){
        if(interstitialAd.isLoaded()){
            interstitialAd.show();
        } else{
            Log.d("", "ad was not ready to shown");
        }
    }

    public void loadInterstitial(){
        AdRequest adRequest= new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("INSERT_YOUR_HASH_DEVICE_ID")
                .build();
        //Load this Interstitial ad
        interstitialAd.loadAd(adRequest);
    }

    //Get a string error
    private String getErrorReason(int errorCode){

        String errorReason="";
        switch(errorCode){
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                errorReason="Internal Error";
                break;
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                errorReason="Invalid Request";
                break;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                errorReason="Network Error";
                break;
            case AdRequest.ERROR_CODE_NO_FILL:
                errorReason="No Fill";
                break;
        }
        return errorReason;
    }


}
