package com.leantegra.wibeat.monitoringapidemo;

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

import com.leantegra.wibeat.sdk.monitoring.ScanServiceManager;
import com.leantegra.wibeat.sdk.monitoring.distance.ProximityZone;
import com.leantegra.wibeat.sdk.monitoring.info.BaseFrame;
import com.leantegra.wibeat.sdk.monitoring.info.Region;
import com.leantegra.wibeat.sdk.monitoring.listeners.MonitoringListener;
import com.leantegra.wibeat.sdk.monitoring.listeners.ScanServiceConsumer;
import com.leantegra.wibeat.sdk.monitoring.service.ScanError;

import static com.leantegra.wibeat.sdk.monitoring.config.ScanConfig.SCAN_MODE_LOW_LATENCY;

public class MainActivity extends AppCompatActivity implements ScanServiceConsumer {

    private static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;

    private ScanServiceManager mScanServiceManager;

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
        if (mScanServiceManager != null) {
            //Stop scan
            mScanServiceManager.stopScan();
            //Stop scan service
            mScanServiceManager.unbind();
            mScanServiceManager = null;
        }
    }

    private void startMonitoring() {
        //Create scan service manager
        mScanServiceManager = new ScanServiceManager(this, this);
        //Set foreground scan period
        mScanServiceManager.setForegroundScanPeriod(5000, 0);
        //Set scan mode
        mScanServiceManager.setScanMode(SCAN_MODE_LOW_LATENCY);
        //Set monitoring listener
        mScanServiceManager.setMonitoringListener(new MonitoringListener() {
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
        mScanServiceManager.addMonitoringRegion(new Region.Builder(1)
                .addAddress("56:49:85:50:2E:6F")
                .setProximityZone(ProximityZone.NEAR).build());
        //Connect to scan service
        mScanServiceManager.bind();
    }

    private void showErrorDialog(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.error);
        builder.setMessage(error);
        builder.show();
    }

    @Override
    public void onBind() {

    }

    @Override
    public void onUnbind() {

    }

    @Override
    public void onError(ScanError scanError) {
        showErrorDialog(scanError.toString());
    }
}
