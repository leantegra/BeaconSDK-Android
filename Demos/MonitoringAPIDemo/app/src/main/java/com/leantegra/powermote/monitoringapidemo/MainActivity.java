package com.leantegra.powermote.monitoringapidemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.leantegra.powermote.sdk.monitoring.MonitoringManager;
import com.leantegra.powermote.sdk.monitoring.distance.ProximityZone;
import com.leantegra.powermote.sdk.monitoring.info.BaseFrame;
import com.leantegra.powermote.sdk.monitoring.info.Region;
import com.leantegra.powermote.sdk.monitoring.listeners.MonitoringListener;
import com.leantegra.powermote.sdk.monitoring.listeners.ServiceConnectionListener;
import com.leantegra.powermote.sdk.monitoring.service.ScanError;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Check coarse location permission
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            startMonitoring();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startMonitoring();
                } else {
                    showErrorDialog(getString(R.string.error_permission));
                }
            }
            break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Stop scan
        MonitoringManager.INSTANCE.stopScan();
        //Stop scan service
        MonitoringManager.INSTANCE.unbind();
    }

    private void startMonitoring() {
        //Set foreground scan period
        MonitoringManager.INSTANCE.setForegroundScanPeriod(5000, 0);
        //Set scan mode
        MonitoringManager.INSTANCE.setScanMode(MonitoringManager.SCAN_MODE_LOW_LATENCY);
        //Set monitoring listener
        MonitoringManager.INSTANCE.setMonitoringListener(new MonitoringListener() {
            @Override
            public void onEnterRegion(Region region, BaseFrame baseFrame) {
                ((TextView) findViewById(R.id.textView)).setText(R.string.enter_region);
                Toast.makeText(MainActivity.this, R.string.enter_region, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onExitRegion(Region region, BaseFrame baseFrame) {
                ((TextView) findViewById(R.id.textView)).setText(R.string.exit_region);
                Toast.makeText(MainActivity.this, R.string.exit_region, Toast.LENGTH_SHORT).show();
            }
        });
        //Add region to monitoring
        MonitoringManager.INSTANCE.addMonitoringRegion(new Region.Builder(1)
                .addAddress("F1:45:87:51:CD:5F")
                .setProximityZone(ProximityZone.NEAR).build());
        //Create scan service
        MonitoringManager.INSTANCE.bind(this.getApplicationContext(), new ServiceConnectionListener() {
            @Override
            public void onBind() {
                //Start scan
                MonitoringManager.INSTANCE.startScan();
            }

            @Override
            public void onUnbind() {

            }

            @Override
            public void onError(ScanError scanError) {
                showErrorDialog(scanError.toString());
            }
        });
    }

    private void showErrorDialog(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.error);
        builder.setMessage(error);
        builder.show();
    }

}
