package com.leantegra.wibeat.rangingapidemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.util.ArrayMap;

import com.leantegra.wibeat.sdk.monitoring.MonitoringManager;
import com.leantegra.wibeat.sdk.monitoring.info.BaseFrame;
import com.leantegra.wibeat.sdk.monitoring.info.EddystoneUrlFrame;
import com.leantegra.wibeat.sdk.monitoring.info.IBeaconFrame;
import com.leantegra.wibeat.sdk.monitoring.info.TagFrame;
import com.leantegra.wibeat.sdk.monitoring.listeners.ScanListener;
import com.leantegra.wibeat.sdk.monitoring.listeners.ServiceConnectionListener;
import com.leantegra.wibeat.sdk.monitoring.service.ScanError;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;

    private RecyclerView mListView;

    private Adapter mAdapter;

    private ArrayMap<String, BaseFrame> mFoundedDeviceMap = new ArrayMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        mListView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new Adapter();
        mListView.setAdapter(mAdapter);
        ((SimpleItemAnimator) mListView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Check coarse location permission
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            startRanging();
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
                    startRanging();
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

    private void showErrorDialog(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.error);
        builder.setMessage(error);
        builder.show();
    }


    private void startRanging() {
        //Set foreground scan period
        MonitoringManager.INSTANCE.setForegroundScanPeriod(5000, 0);
        //Set scan mode
        MonitoringManager.INSTANCE.setScanMode(MonitoringManager.SCAN_MODE_LOW_LATENCY);
        //Set scan listener or use setRangingListener()
        MonitoringManager.INSTANCE.setScanListener(new ScanListener() {
            @Override
            public void onScanResult(BaseFrame baseFrame) {
                String key = baseFrame.getBluetoothDevice().getAddress();
                int index = mFoundedDeviceMap.indexOfKey(key);
                mFoundedDeviceMap.put(key, baseFrame);
                if (index >= 0) {
                    mAdapter.notifyItemChanged(index);
                } else {
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
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

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public int getItemCount() {
            return mFoundedDeviceMap.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_device_item_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            BaseFrame frame = mFoundedDeviceMap.valueAt(position);
            holder.mTextView.setText(frame.getBluetoothDevice().getAddress());
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(String.format(Locale.getDefault(), "Dist. %.1fm", frame.getDistance()));
            stringBuilder.append("\n");
            switch (frame.getType()) {
                case EDDYSTONE_URL: {
                    stringBuilder.append(String.format(Locale.getDefault(), "Url: %s", ((EddystoneUrlFrame) frame).getURL()));
                }
                break;

                case I_BEACON: {
                    stringBuilder.append(String.format(Locale.getDefault(), "Major id: %s; Minor id: %s",
                            ((IBeaconFrame) frame).getMajor(), ((IBeaconFrame) frame).getMinor()));
                }
                break;

                case TAG: {
                    stringBuilder.append(String.format(Locale.getDefault(), "Device id: %s; Network id: %s",
                            ((TagFrame) frame).getDeviceID(), ((TagFrame) frame).getNetworkID()));
                }
                break;
            }
            holder.mTextView2.setText(stringBuilder.toString());
            holder.mTextView4.setText(frame.getProximityZone().toString());
            switch (frame.getProximityZone()){
                case IMMEDIATE:{
                    holder.itemView.setBackgroundResource(R.color.colorImmediate);
                }break;

                case NEAR:{
                    holder.itemView.setBackgroundResource(R.color.colorNear);
                }break;

                case FAR:{
                    holder.itemView.setBackgroundResource(R.color.colorFar);
                }break;

                default:{
                    holder.itemView.setBackgroundColor(Color.WHITE);
                }break;
            }
            holder.mTextView3.setText(frame.getType().toString());
        }
    }
}
