package com.leantegra.mobilertls;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.leantegra.wibeat.sdk.rtls.map.IndoorMapView;

public class MainActivity extends AppCompatActivity {

    private IndoorMapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = (IndoorMapView) findViewById(R.id.map);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Start show user location(WiBeat installation is needed)
            mapView.startLocationUpdates();
        } else {
            String[] permisions = {Manifest.permission.ACCESS_COARSE_LOCATION};
            requestPermissions(permisions, 1);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Stop show user location
        mapView.stopLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //Start show user location(WiBeat installation is needed)
            mapView.startLocationUpdates();
        }
    }
}
