package com.example.incentive_spirometer_and_dvt_application.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;


import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.incentive_spirometer_and_dvt_application.R;
import com.example.incentive_spirometer_and_dvt_application.helpers.BluetoothService;
import com.example.incentive_spirometer_and_dvt_application.helpers.DeviceListAdapter;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothTestActivity extends AppCompatActivity {
    private static final String TAG = "BTActivity";
    private static String uniqueID;
    private static final String PREFERRED_UNIQUE_ID = "PREF_UNIQUE_ID";

    BluetoothAdapter btAdapter;
    Button enableDisableDiscoverable;
    Button startAcceptThread;

    TextView recievedText;

    BluetoothService bluetoothConnection;

    BluetoothDevice btDevice;
    public ArrayList<BluetoothDevice> btDevices;
    public ArrayList<BluetoothDevice> listOfBonds;
    public DeviceListAdapter deviceListAdapter;
    public DeviceListAdapter listOfBondAdapter;

    ListView newDevices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_test);
        uniqueID = id(BluetoothTestActivity.this);

        Log.d(TAG, "onCreate: UUID is " + uniqueID);

        Button onOff = (Button) findViewById(R.id.btnOnOff);
        enableDisableDiscoverable = (Button) findViewById(R.id.discoverableOnOff);
        newDevices = (ListView) findViewById(R.id.newDevices);
        btDevices = new ArrayList<>();

        startAcceptThread = (Button) findViewById(R.id.startAcceptThread);
        recievedText = (TextView) findViewById(R.id.recievedText);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        newDevices.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                btAdapter.cancelDiscovery();

                Log.d(TAG, "onItemClick: you clicked on a device");
                String deviceName = btDevices.get(position).getName();
                String deviceAddress = btDevices.get(position).getAddress();

                Log.d(TAG, "onItemClick: device name: " + deviceName);
                Log.d(TAG, "onItemClick: device address: " + deviceAddress);

                Log.d(TAG, "onItemClick: trying to pair with: " + deviceName);
                btDevices.get(position).createBond();
                btDevice = btDevices.get(position);
                bluetoothConnection = new BluetoothService(BluetoothTestActivity.this, UUID.fromString(uniqueID));
            }
        });

        onOff.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                enableDisableBt();
            }
        });

        startAcceptThread.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                bluetoothConnection = new BluetoothService(BluetoothTestActivity.this, UUID.fromString(uniqueID));
                //startBtConnection();
            }
        });

        ListView bondedList = (ListView) findViewById(R.id.bondedDevices);
        listOfBonds = new ArrayList<>();
        listOfBondAdapter = new DeviceListAdapter(this, R.layout.device_adapter_view, listOfBonds);
        getBondedDevices();
        bondedList.setAdapter(listOfBondAdapter);

        bondedList.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String deviceName = listOfBonds.get(position).getName();
                String deviceAddress = listOfBonds.get(position).getAddress();

                Log.d(TAG, "onItemClick: you clicked the following item: " + deviceName + ": " + deviceAddress);
                btDevice = listOfBonds.get(position);
                bluetoothConnection = new BluetoothService(BluetoothTestActivity.this, UUID.fromString(uniqueID));
            }
        });
    }

    public void getBondedDevices(){
        if(btAdapter != null){
            Set<BluetoothDevice> bondedDevices = btAdapter.getBondedDevices();
            if(bondedDevices.size() > 0){
                listOfBonds.addAll(bondedDevices);
            }
        }
        listOfBondAdapter.notifyDataSetChanged();
    }


    public synchronized static String id(Context context){
        if(uniqueID == null){
            SharedPreferences sharedPrefs = context.getSharedPreferences(PREFERRED_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREFERRED_UNIQUE_ID, null);

            if(uniqueID == null){
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREFERRED_UNIQUE_ID, uniqueID);
                editor.apply();
            }
        }

        return uniqueID;
    }


    public void startBtConnection(){
        Log.d(TAG, "startBtConnection: " + (btDevice == null) + " and UUID is " + UUID.fromString(uniqueID));
        bluetoothConnection.startClient(btDevice);
    }

    public void enableDisableBt(){
        if(btAdapter == null){
            Log.d(TAG, "enableDisableBt: does not have bluetooth available");
        }
        if(!btAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBt: enabling bt");
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(intent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
        if(btAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBt: disabling bt");
            btAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
    }

    public void enableDisableDiscoverable(View view) {
        Log.d(TAG, "btnEnableDisable_Discoverable: Making device discoverable for 300 seconds.");

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        IntentFilter intentFilter = new IntentFilter(btAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReceiver2,intentFilter);

    }

    public void discoverDevices(View view){
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");
        int requestCode = 1;

        if(btAdapter.isDiscovering()){
            btAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");

            checkBTPermissions();
        }
        if(!btAdapter.isDiscovering()){
            checkBTPermissions();
        }
    }

    //need to look at this (requires api lvl 23)
    private void checkBTPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1 && permissions[0].equals(Manifest.permission.BLUETOOTH_ADMIN) && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "onRequestPermissionsResult: We got permissions soooooo wtf bro");
            btAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter();
            discoverDevicesIntent.addAction(BluetoothDevice.ACTION_FOUND);
            discoverDevicesIntent.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            discoverDevicesIntent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
            
            Log.d(TAG, "discoverDevices: " + btAdapter.isDiscovering());
        }

    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(btAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, btAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    /**
     * Broadcast Receiver for changes made to bluetooth states such as:
     * 1) Discoverability mode on/off or expire.
     */
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }

            }
        }
    };




    /**
     * Broadcast Receiver for listing devices that are not yet paired
     * -Executed by btnDiscover() method.
     */
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive: in broadcast reciever 3");

            if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                Log.d(TAG, "onReceive: DISCOVERY STARTED");
            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                Log.d(TAG, "onReceive: DISCOVERY ENDED");
            }
            else if (BluetoothDevice.ACTION_FOUND.equals(action)){
                Log.d(TAG, "onReceive: ACTION FOUND.");
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                btDevices.add(device);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                deviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, btDevices);
                newDevices.setAdapter(deviceListAdapter);
            }

        }
    };

    /**
     * Broadcast Receiver that detects bond state changes (Pairing status changes)
     */
    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                    //inside BroadcastReceiver4
                    listOfBonds.add(mDevice);
                    listOfBondAdapter.notifyDataSetChanged();
                    btDevice = mDevice;
                }
                //case2: creating a bone
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mBroadcastReceiver2);
        unregisterReceiver(mBroadcastReceiver3);
        unregisterReceiver(mBroadcastReceiver4);
        btAdapter.cancelDiscovery();
    }
}
