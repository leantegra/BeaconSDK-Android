package com.leantegra.mobilertls;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.os.Bundle;

import com.leantegra.wibeat.sdk.rtls.map.OnVenueMapReadyCallback;
import com.leantegra.wibeat.sdk.rtls.map.VenueMap;
import com.leantegra.wibeat.sdk.rtls.map.VenueMapFragment;

public class MainActivity extends Activity {

    private VenueMap venueMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VenueMapFragment venueMapFragment = (VenueMapFragment) getFragmentManager().findFragmentById(R.id.map);
        venueMapFragment.getVenueMapAsync(new OnVenueMapReadyCallback() {
            @Override
            public void onMapReady(VenueMap venueMap) {
                MainActivity.this.venueMap = venueMap;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                        checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //Start show user location(WiBeat installation is needed)
                    venueMap.setUserLocationEnabled(true);
                } else {
                    String[] permisions = {Manifest.permission.ACCESS_COARSE_LOCATION};
                    requestPermissions(permisions, 1);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Stop show user location
        if (venueMap != null) {
            venueMap.setUserLocationEnabled(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //Start show user location(WiBeat installation is needed)
            venueMap.setUserLocationEnabled(true);
        }
    }
}
