package com.app.appathon.blooddonateapp.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Created by hp on 10/21/2016.
 */

public class GeocoderHandler extends Handler {
    private String locationAddress;
    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case 1:
                Bundle bundle = message.getData();
                locationAddress = bundle.getString("address");
                break;
            default:
                locationAddress = null;
        }
    }

    public String getLocationAddress(){
        return locationAddress;
    }
}
