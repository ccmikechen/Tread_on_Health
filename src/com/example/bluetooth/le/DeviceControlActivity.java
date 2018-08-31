/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.bluetooth.le;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import data.DataRecorder;
import data.NikeDataGetter;
import data.NikeDataLogger;
import data.NikeDatabase;
import sensor.Direction;
import triggers.BackPressTrigger;
import triggers.ForthBackSwingTrigger;
import triggers.ForthKickTrigger;
import triggers.ForthPressTrigger;
import triggers.JumpTrigger;
import triggers.LeftRightSwingTrigger;
import triggers.StepTrigger;
import triggers.TriggersManager;

public class DeviceControlActivity extends Activity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();
    
    public static final String DEVICE_CHARACTERISTIC = "DEVICE_CHARACTERISTIC";
    public static final String DEVICE_NAME = "DEVICE_NAME";
    public static final String DEVICE_PROPERTIES = "DEVICE_PROPERTIES";
    public static final String DEVICE_SERVICE = "DEVICE_SERVICE";
    public static final String DEVICE_UUID = "DEVICE_UUID";
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    
    //private final static UUID UART_SERIVCE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
	private static final UUID TX_CHAR_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
	private static final UUID RX_CHAR_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");


    private TextView mConnectionState;
    private TextView mDataField;
    private TextView mCharacteristicUUID;
    private String mDeviceName;
    private String mDeviceAddress;
    private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    
    private BluetoothGattCharacteristic characteristic;
    
    private BluetoothGattCharacteristic Tx;
    private BluetoothGattCharacteristic Rx;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private NikeDataLogger logger;

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);

            setDataGetters();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };


    private void setDataGetters() {
        TriggersManager triggerManager = new TriggersManager();

        NikeDataGetter rightDataGetter = new NikeDataGetter(Direction.RIGHT);
        DataRecorder rightDataRecorder = new DataRecorder();
        mBluetoothLeService.addRightDataGetter(rightDataGetter);
        mBluetoothLeService.addRightDataGetter(rightDataRecorder);

        triggerManager.addTrigger(new StepTrigger(rightDataGetter, Direction.RIGHT));
        triggerManager.addTrigger(new ForthPressTrigger(rightDataGetter));
        triggerManager.addTrigger(new BackPressTrigger(rightDataGetter));
        triggerManager.addTrigger(new ForthKickTrigger(rightDataGetter));
        triggerManager.addTrigger(new ForthBackSwingTrigger(rightDataGetter));
        triggerManager.addTrigger(new LeftRightSwingTrigger(rightDataGetter));
        triggerManager.addTrigger(new JumpTrigger(rightDataGetter));

        DataGetterManager.setRightDataGetter(rightDataGetter);

        /******************************************************************************/

        NikeDataGetter leftDataGetter = new NikeDataGetter(Direction.LEFT);
        DataRecorder leftDataRecorder = new DataRecorder();
        mBluetoothLeService.addLeftDataGetter(leftDataGetter);
        mBluetoothLeService.addLeftDataGetter(leftDataRecorder);

        triggerManager.addTrigger(new StepTrigger(leftDataGetter, Direction.LEFT));
        triggerManager.addTrigger(new ForthPressTrigger(leftDataGetter));
        triggerManager.addTrigger(new BackPressTrigger(leftDataGetter));
        triggerManager.addTrigger(new ForthKickTrigger(leftDataGetter));
        triggerManager.addTrigger(new ForthBackSwingTrigger(leftDataGetter));
        triggerManager.addTrigger(new LeftRightSwingTrigger(leftDataGetter));
        triggerManager.addTrigger(new JumpTrigger(leftDataGetter));

        DataGetterManager.setLeftDataGetter(leftDataGetter);

        NikeDatabase database = null;
        try {
            database = new NikeDatabase(this, "NikeSensor.db");
            logger = new NikeDataLogger(database, leftDataRecorder, rightDataRecorder);
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA), intent.getStringExtra(BluetoothLeService.CHARACTERISTIC_UUID));
            }
        }
    };

    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.
    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    if (mGattCharacteristics != null) {
                        characteristic = mGattCharacteristics.get(groupPosition).get(childPosition);
                        final int charaProp = characteristic.getProperties();
                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
                        	
                        }
                        return true;
                    }
                    return false;
                }
    };

    private void clearUI() {
        mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        mDataField.setText(R.string.no_data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
        mGattServicesList.setOnChildClickListener(servicesListClickListner);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataField = (TextView) findViewById(R.id.data_value);
        //***mCharacteristicUUID = (TextView) findViewById(R.id.characteristic_uuid);
        
        final Button openRx = null;//*** (Button)findViewById(R.id.b1);
        openRx.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View v) {
        		try {
        			if(Tx != null) {
        				if(mBluetoothLeService.setCharacteristicNotification(Rx, true)) Log.e("ERROR", "Opne Rx fail");
                    }
        		} catch(Exception e) {
        			Log.e("ERROR", e.getMessage());
        		}
			};
        });
        final Button closeRx = null;//***(Button)findViewById(R.id.b2);
        closeRx.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View v) {
        		try {
        			if(Tx != null) {
        				if(mBluetoothLeService.setCharacteristicNotification(Rx, false)) Log.e("ERROR", "Close Rx fail");
        			}
        		} catch(Exception e) {
        			Log.e("ERROR", e.getMessage());
        		}
			};
        });
        final Button chartButton = null;//***(Button)findViewById(R.id.chart_button);
        chartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(DeviceControlActivity.this, ChartActivity.class);
                startActivity(intent);
            }
        });
        final Button recordButton = null;//***(Button) findViewById(R.id.record_button);
        recordButton.setOnClickListener(new View.OnClickListener() {
            boolean isStartRecord = false;
            @Override
            public void onClick(View v) {
                if (isStartRecord) {
                    logger.stop();
                    logData(logger);
                    recordButton.setText("record");
                    isStartRecord = false;

                    //***openRx.setEnabled(true);
                    closeRx.setEnabled(true);
                    chartButton.setEnabled(true);
                } else {
                    logger.start();
                    recordButton.setText("stop");
                    isStartRecord = true;

                    //***openRx.setEnabled(false);
                    closeRx.setEnabled(false);
                    chartButton.setEnabled(false);
                }
            }
        });
        getActionBar().setTitle(mDeviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);


    }

    private void logData(final NikeDataLogger logger) {

        final boolean[] isLogged = new boolean[1];
        isLogged[0] = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        final EditText editText = new EditText(this);
        builder.setView(editText);

        final AlertDialog dialog = builder.create();
        dialog.show();

        Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dataName = editText.getText().toString();
                if (dataName.trim().equals("")) {
                    Toast.makeText(DeviceControlActivity.this, "This name is empty", Toast.LENGTH_SHORT).show();
                    editText.setText("");
                } else if (!logger.log(dataName)) {
                    Toast.makeText(DeviceControlActivity.this, "This name is exist", Toast.LENGTH_SHORT).show();
                    editText.setText("");
                } else {
                    dialog.dismiss();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy () {
            super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data, String uuid) {
        if (data != null) {
            mDataField.setText(data);
            mCharacteristicUUID.setText(uuid);
        }
    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for(BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                Log.e("Services", uuid);
                if(uuid.contains(TX_CHAR_UUID.toString())) {
                	Tx = gattCharacteristic;
                	mBluetoothLeService.TxChar = Tx;
                	Log.d("[debug]", "Tx found");
                } else if(uuid.contains(RX_CHAR_UUID.toString())) {
                	Rx = gattCharacteristic;
                	mBluetoothLeService.RxChar = Rx;
                	Log.d("[debug]", "Rx found");
                }

                currentCharaData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 },
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        mGattServicesList.setAdapter(gattServiceAdapter);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}
