package com.leantegra.powermote.notificationdemo;

import android.app.Application;
import android.app.Notification;
import android.content.Intent;
import android.media.RingtoneManager;
import android.widget.Toast;

import com.leantegra.powermote.sdk.LeantegraSDK;
import com.leantegra.powermote.sdk.cloud.analytics.AdvertisingNotificationManager;
import com.leantegra.powermote.sdk.monitoring.service.ScanError;

/**
 * @author Artem Drozd, Leantegra Inc.
 */
public class MyApplication extends Application implements AdvertisingNotificationManager.AdvertisingNotificationManagerListener {

    private AdvertisingNotificationManager mManager;

    @Override
    public void onCreate() {
        super.onCreate();
        //Initialize SDK
        LeantegraSDK.initialize("m2m", "m2m");
        //Create content intent
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        //Create notification builder
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Title")
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        //Initialize manager
        mManager = new AdvertisingNotificationManager(getApplicationContext(), intent, builder, this);
        //Start detect PowerMote
        mManager.startScan();
    }

    @Override
    public void onError(ScanError scanError) {
        Toast.makeText(this, scanError.toString(), Toast.LENGTH_SHORT).show();
    }

}
