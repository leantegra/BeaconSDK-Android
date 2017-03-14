package com.leantegra.mobilertls;

import android.app.Application;

import com.leantegra.wibeat.sdk.LeantegraSDK;

/**
 * Created by tania on 19.01.17.
 */

public class RTLSApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LeantegraSDK.initialize(this, "mobilertls_mobile", "8eb70dae-8e8a-47b6-bde2-fe1a52b392be");
        LeantegraSDK.setServerAddress("35.157.31.110");
    }
}
