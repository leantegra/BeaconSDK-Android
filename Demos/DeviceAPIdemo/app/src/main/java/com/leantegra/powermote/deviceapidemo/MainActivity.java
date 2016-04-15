package com.leantegra.powermote.deviceapidemo;

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

import com.leantegra.powermote.sdk.device.OperationMode;
import com.leantegra.powermote.sdk.device.connection.PowerMoteCharacteristicType;
import com.leantegra.powermote.sdk.device.connection.PowerMoteConnection;
import com.leantegra.powermote.sdk.device.connection.PowerMoteError;
import com.leantegra.powermote.sdk.device.info.BaseDeviceInfo;

public class MainActivity extends AppCompatActivity {

    private PowerMoteConnection mPowerMoteConnection;

    private EditText mMacEditText;

    private Button mConnectButton;

    private ProgressBar mProgressBar;

    private RecyclerView mListView;

    private CharacteristicsListAdapter mAdapter;

    private BaseDeviceInfo mDeviceInfo;

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
                if (mPowerMoteConnection == null || !mPowerMoteConnection.isConnected()) {
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
        if (mPowerMoteConnection == null || !mPowerMoteConnection.getDeviceMACAddress().equals(MACAddress)) {
            //Create connection to PowerMote with MAC address
            mPowerMoteConnection = new PowerMoteConnection(this, MACAddress, new PowerMoteConnection.PowerMoteConnectionListener() {
                @Override
                public void onConnect() {
                    mConnectButton.setText(R.string.disconnect);
                    mProgressBar.setVisibility(View.GONE);
                    enableConnectUI();
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
                public void onError(PowerMoteError powerMoteError) {
                    mProgressBar.setVisibility(View.GONE);
                    enableConnectUI();
                    showErrorDialog(powerMoteError.toString());
                }
            });
        }
        mProgressBar.setVisibility(View.VISIBLE);
        disableConnectUI();
        //Connect
        mPowerMoteConnection.connect();
    }

    private void disconnect() {
        if (mPowerMoteConnection != null) {
            //close connection
            mPowerMoteConnection.disconnect();
        }
    }

    private void readDeviceInfo() {
        //read all characteristics
        mPowerMoteConnection.readDeviceInfo(new PowerMoteConnection.DeviceInfoReadListener() {
            @Override
            public void onSuccess(BaseDeviceInfo baseDeviceInfo) {
                mDeviceInfo = baseDeviceInfo;
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(PowerMoteError powerMoteError) {
                showErrorDialog(powerMoteError.toString());
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
            public void onClick(DialogInterface dialog, int which) {
                mPowerMoteConnection.writeOperationMode(operationModes[which], new PowerMoteConnection.PowerMoteWriteListener() {
                    @Override
                    public void onSuccess(PowerMoteCharacteristicType powerMoteCharacteristicType) {
                        Toast.makeText(MainActivity.this,"Successful!",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(PowerMoteError powerMoteError, PowerMoteCharacteristicType powerMoteCharacteristicType) {
                        showErrorDialog(powerMoteCharacteristicType.name() + ": " + powerMoteError.name());
                    }
                });
            }
        });
        builder.show();
    }

    private void showTXLevelDialog() {
        String[] values = new String[8];
        for (int i = 0; i < 8; i++) {
            values[i] = String.valueOf(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.tx_power_level);
        builder.setItems(values, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {
                mPowerMoteConnection.writeTxPowerLevel(which, new PowerMoteConnection.PowerMoteWriteListener() {
                    @Override
                    public void onSuccess(PowerMoteCharacteristicType powerMoteCharacteristicType) {
                        mDeviceInfo = new BaseDeviceInfo.Builder(mDeviceInfo).setTxPowerLevel(which).build();
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(PowerMoteError powerMoteError, PowerMoteCharacteristicType powerMoteCharacteristicType) {
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
                mPowerMoteConnection.writeAdvertisingInterval(Integer.parseInt(values[which]), new PowerMoteConnection.PowerMoteWriteListener() {
                    @Override
                    public void onSuccess(PowerMoteCharacteristicType powerMoteCharacteristicType) {
                        mDeviceInfo = new BaseDeviceInfo.Builder(mDeviceInfo)
                                .setAdvertisingInterval(Integer.parseInt(values[which]))
                                .build();
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(PowerMoteError powerMoteError, PowerMoteCharacteristicType powerMoteCharacteristicType) {
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
                    showTXLevelDialog();
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
                    subTitle = mDeviceInfo.getOperationMode().toString();
                }
                break;

                case 1: {
                    titleResID = R.string.tx_power_level;
                    subTitle = String.valueOf(mDeviceInfo.getTxPowerLevel());
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
