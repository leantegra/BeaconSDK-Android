package com.leantegra.wibeat.deviceapidemo;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.leantegra.wibeat.sdk.device.DeviceStatus;
import com.leantegra.wibeat.sdk.device.OperationMode;
import com.leantegra.wibeat.sdk.device.connection.WiBeatCharacteristicType;
import com.leantegra.wibeat.sdk.device.connection.WiBeatConnection;
import com.leantegra.wibeat.sdk.device.connection.WiBeatError;

import com.leantegra.wibeat.sdk.device.info.DeviceInfo;

public class MainActivity extends AppCompatActivity {

    private WiBeatConnection mWiBeatConnection;

    private EditText mMacEditText;

    private Button mConnectButton;

    private ProgressBar mProgressBar;

    private RecyclerView mListView;

    private CharacteristicsListAdapter mAdapter;

    private DeviceInfo mDeviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMacEditText = (EditText) findViewById(R.id.macEditText);
        mConnectButton = (Button) findViewById(R.id.connectButton);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        //Set on click listener to connect/disconnect button
        mConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWiBeatConnection == null || !mWiBeatConnection.isConnected()) {
                    connect();
                } else {
                    disconnect();
                    mProgressBar.setVisibility(View.VISIBLE);
                    disableConnectUI();
                }
            }
        });
        initListView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disconnect();
    }

    private void connect() {
        String MACAddress = mMacEditText.getText().toString().toUpperCase();
        if (mWiBeatConnection == null || !mWiBeatConnection.getDeviceMACAddress().equals(MACAddress)) {
            //Create connection to WiBeat with MAC address
            mWiBeatConnection = new WiBeatConnection(this, MACAddress, new WiBeatConnection.WiBeatConnectionListener() {
                @Override
                public void onConnect() {
                    mConnectButton.setText(R.string.disconnect);
                    readDeviceInfo();
                }

                @Override
                public void onDisconnect() {
                    mConnectButton.setText(R.string.connect);
                    mProgressBar.setVisibility(View.GONE);
                    enableConnectUI();
                    mDeviceInfo = null;
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onError(WiBeatError powerMoteError) {
                    mProgressBar.setVisibility(View.GONE);
                    enableConnectUI();
                    showErrorDialog(powerMoteError.toString());
                }
            });
        }
        mProgressBar.setVisibility(View.VISIBLE);
        disableConnectUI();
        //Connect
        mWiBeatConnection.connect();
    }

    private void disconnect() {
        if (mWiBeatConnection != null) {
            //close connection
            mWiBeatConnection.disconnect();
        }
    }

    private void readDeviceInfo() {
        //read all characteristics
        mWiBeatConnection.readDeviceInfo(new WiBeatConnection.DeviceInfoReadListener() {
            @Override
            public void onSuccess(DeviceInfo baseDeviceInfo) {
                enableConnectUI();
                mDeviceInfo = baseDeviceInfo;
                mAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(WiBeatError powerMoteError) {
                showErrorDialog(powerMoteError.toString());
                enableConnectUI();
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void enableConnectUI() {
        mConnectButton.setEnabled(true);
        mMacEditText.setEnabled(true);
    }

    private void disableConnectUI() {
        mConnectButton.setEnabled(false);
        mMacEditText.setEnabled(false);
    }

    private void showErrorDialog(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.error);
        builder.setMessage(error);
        builder.show();
    }

    private void initListView() {
        mListView = (RecyclerView) findViewById(R.id.listView);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CharacteristicsListAdapter();
        mListView.setAdapter(mAdapter);
    }

    private void showOperationModeDialog() {
        final OperationMode[] operationModes = OperationMode.values();
        String[] values = new String[operationModes.length];
        for (int i = 0; i < operationModes.length; i++) {
            values[i] = operationModes[i].toString();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.operation_mode);
        builder.setItems(values, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {
                mWiBeatConnection.writeOperationMode(operationModes[which], new WiBeatConnection.WiBeatWriteListener() {
                    @Override
                    public void onSuccess(WiBeatCharacteristicType powerMoteCharacteristicType) {
                        mDeviceInfo = new DeviceInfo.Builder(mDeviceInfo).setDeviceStatus(
                                new DeviceStatus(operationModes[which],
                                        mDeviceInfo.getDeviceStatus().isButtonLock(),
                                        mDeviceInfo.getDeviceStatus().getBatteryLevel(),
                                        mDeviceInfo.getDeviceStatus().isLedTest())).build();
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(WiBeatError powerMoteError, WiBeatCharacteristicType powerMoteCharacteristicType) {
                        showErrorDialog(powerMoteCharacteristicType.name() + ": " + powerMoteError.name());
                    }
                });
            }
        });
        builder.show();
    }

    private void showRadioTXPowerDialog() {
        final int[] values = new int[]{-15, -12, -8, -5, -2, 1, 5, 8};
        String[] strings = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            strings[i] = String.valueOf(values[i]);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.radio_tx_power);
        builder.setItems(strings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {
                mWiBeatConnection.writeRadioTXPower(values[which], new WiBeatConnection.WiBeatWriteListener() {
                    @Override
                    public void onSuccess(WiBeatCharacteristicType powerMoteCharacteristicType) {
                        mDeviceInfo = new DeviceInfo.Builder(mDeviceInfo).setRadioTXPower(values[which]).build();
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(WiBeatError powerMoteError, WiBeatCharacteristicType powerMoteCharacteristicType) {
                        showErrorDialog(powerMoteCharacteristicType.name() + ": " + powerMoteError.name());
                    }
                });
            }
        });
        builder.show();
    }

    private void showAdvertisingIntervalDialog() {
        final String[] values = new String[]{"100", "500", "1000", "2000"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.advertising_interval);
        builder.setItems(values, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {
                mWiBeatConnection.writeAdvertisingInterval(Integer.parseInt(values[which]), new WiBeatConnection.WiBeatWriteListener() {
                    @Override
                    public void onSuccess(WiBeatCharacteristicType powerMoteCharacteristicType) {
                        mDeviceInfo = new DeviceInfo.Builder(mDeviceInfo)
                                .setAdvertisingInterval(Integer.parseInt(values[which]))
                                .build();
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(WiBeatError powerMoteError, WiBeatCharacteristicType powerMoteCharacteristicType) {
                        showErrorDialog(powerMoteCharacteristicType.name() + ": " + powerMoteError.name());
                    }
                });
            }
        });
        builder.show();
    }

    private class CharacteristicsListAdapter extends RecyclerView.Adapter<ViewHolder> implements View.OnClickListener {
        @Override
        public int getItemCount() {
            if (mDeviceInfo == null) {
                return 0;
            }
            //
            return 4;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_charact_item_list, parent, false);
            view.setOnClickListener(this);
            return new ViewHolder(view);
        }

        @Override
        public void onClick(View v) {
            int index = mListView.getChildAdapterPosition(v);
            switch (index) {
                case 0: {
                    showOperationModeDialog();
                }
                break;

                case 1: {
                    showRadioTXPowerDialog();
                }
                break;

                case 2: {
                    showAdvertisingIntervalDialog();
                }
                break;
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int titleResID;
            String subTitle;
            switch (position) {
                case 0: {
                    titleResID = R.string.operation_mode;
                    subTitle = mDeviceInfo.getDeviceStatus().getOperationMode().name();
                }
                break;

                case 1: {
                    titleResID = R.string.radio_tx_power;
                    subTitle = mDeviceInfo.getRadioTXPower() + "dBm";
                }
                break;

                case 2: {
                    titleResID = R.string.advertising_interval;
                    subTitle = String.valueOf(mDeviceInfo.getAdvertisingInterval()) + "ms";
                }
                break;

                case 3: {
                    titleResID = R.string.name;
                    subTitle = mDeviceInfo.getName();
                }
                break;

                default: {
                    titleResID = R.string.app_name;
                    subTitle = "";
                }
                break;
            }
            holder.mTextView.setText(titleResID);
            holder.mTextView2.setText(subTitle);
        }
    }
}
