package com.leantegra.powermote.backgroundmonitoringapidemo;

import android.Manifest;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import com.leantegra.powermote.sdk.monitoring.BackgroundManager;
import com.leantegra.powermote.sdk.monitoring.MonitoringManager;
import com.leantegra.powermote.sdk.monitoring.distance.ProximityZone;
import com.leantegra.powermote.sdk.monitoring.info.BaseFrame;
import com.leantegra.powermote.sdk.monitoring.info.Region;
import com.leantegra.powermote.sdk.monitoring.service.ScanError;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Artem Drozd on 19.04.16.
 */
public class MyApplication extends Application implements BackgroundManager.BackgroundScanListener {

    private BackgroundManager mBackgroundManager;

    @Override
    public void onCreate() {
        super.onCreate();
        //Check coarse location permission
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            startMonitoring();
        }
    }

    public void startMonitoring() {
        //Set background scan period
        MonitoringManager.INSTANCE.setBackgroundScanPeriod(5000, 25000);
        //Init region list
        ArrayList<Region> regionArrayList = new ArrayList<>(1);
        regionArrayList.add(new Region.Builder(1)
                .setAddress("F1:45:87:51:CD:5F")
                .setProximityZone(ProximityZone.NEAR).build());
        //Init background manager
        mBackgroundManager = new BackgroundManager(this, regionArrayList, this);
        //Start scan
        mBackgroundManager.startScan();
    }

    @Override
    public void onEnterRegion(Region region, BaseFrame baseFrame) {
        showNotification(String.format(Locale.getDefault(), "Id %d", region.getRegionId()), getString(R.string.enter_region));
    }

    @Override
    public void onExitRegion(Region region, BaseFrame baseFrame) {
        showNotification(String.format(Locale.getDefault(), "Id %d", region.getRegionId()), getString(R.string.exit_region));
    }

    @Override
    public void onError(ScanError scanError) {
        //Handle error
    }

    public void showNotification(String title, String message) {
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }
}
