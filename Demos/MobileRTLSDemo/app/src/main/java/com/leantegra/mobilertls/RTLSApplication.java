package com.leantegra.mobilertls;

import android.app.Application;

import com.leantegra.wibeat.sdk.LeantegraSDK;

public class RTLSApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LeantegraSDK.initialize(this, "your client id", "your client secret");
        LeantegraSDK.setServerAddress("CVO portal address");
    }
}
