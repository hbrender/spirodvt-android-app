package com.example.incentive_spirometer_and_dvt_application.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.incentive_spirometer_and_dvt_application.R;
import com.example.incentive_spirometer_and_dvt_application.helpers.BluetoothThread;
import com.google.android.material.snackbar.Snackbar;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Set;

/**
 *
 * Activity where a user can pair a hardware device and attach it to a specific patient
 *
 *  v1.0: 04/20/20
 *
 */

public class ConnectDevice extends AppCompatActivity {
    static final String TAG = "ConnectDeviceTag";

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_CONNECTED_THREAD_SUCCESS = 5;
    static final int STATE_CONNECTED_THREAD_FAILED = 6;
    static final int STATE_MESSAGE_RECEIVED = 7;

    static final int REQUEST_ENABLE_BT = 6;
    static final int REQUEST_LOCATION = 7;

    private static boolean isSpiro;
    private AlphaAnimation inAnim;
    private AlphaAnimation outAnim;
    private FrameLayout progressBarHolder;

    LinearLayout baseLayout;
    Button listDevices, scan;
    ListView listView, nearbyDevices;
    TextView status, message;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice[] bondedDevices;
    ArrayList<BluetoothDevice> nearbyDevicesAL;
    ArrayList<String> nearbyDevicesStrings;
    ArrayAdapter<String> nearbyArrayAdapter;

    private static BluetoothThread bluetoothThread;

    private final BroadcastReceiver nearbyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                Log.d(TAG, "onReceive: device found!");

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                nearbyDevicesAL.add(device);
                nearbyDevicesStrings.add(device.getName() + ": " + device.getAddress());
                nearbyArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    private final BroadcastReceiver bondReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice tempDev = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if(tempDev.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BondBroadcast: already bonded");
                    updateBondedList();
                }
                if(tempDev.getBondState() == BluetoothDevice.BOND_BONDING){
                    Log.d(TAG, "BondBroadcast: currently bonding");
                }
                if(tempDev.getBondState() == BluetoothDevice.BOND_NONE){
                    Log.d(TAG, "BondBroadcast: no bond");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_device);

        // if the intent coming in is dealing with connecting to a spirometer or  DVT device
        Intent incomingIntent = getIntent();
        if(incomingIntent != null){
            isSpiro = incomingIntent.getBooleanExtra("isSpiro", true);
        }

        // getting global view variables
        progressBarHolder = findViewById(R.id.connectProgressBarHolder);
        listDevices = (Button) findViewById(R.id.listDevices);
        scan = (Button) findViewById(R.id.scanNearby);
        listView = (ListView) findViewById(R.id.listView);
        nearbyDevices = (ListView) findViewById(R.id.nearbyDevices);
        message = (TextView) findViewById(R.id.message);
        baseLayout = (LinearLayout) findViewById(R.id.baseLayout);

        // registering receivers so we can get callbacks from the bluetooth thread
        IntentFilter foundIntentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(nearbyReceiver, foundIntentFilter);
        IntentFilter bondIntentFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(bondReciever, bondIntentFilter);

        // getting bluetooth adapter and making sure we have basic bluetooth permissions
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.d(TAG, "onCreate: " +bluetoothAdapter.getName() + " " + bluetoothAdapter.getAddress());
        if(!bluetoothAdapter.isEnabled()){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        }

        // refresh the bonded devices list to show what devices are currently already paired to the phone
        updateBondedList();

        // enabling the scan for nearby devices
        // we need certain bluetooth and location permissions. once those are ready we can start discovering nearby devices
        scan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(ConnectDevice.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(ConnectDevice.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                    ActivityCompat.requestPermissions(ConnectDevice.this, new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                }
                else{ // we have permissions
                    nearbyDevicesAL = new ArrayList<>();
                    nearbyDevicesStrings = new ArrayList<>();
                    nearbyArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, nearbyDevicesStrings);
                    nearbyDevices.setAdapter(nearbyArrayAdapter);
                    startDiscovery();
                    Snackbar.make(findViewById(R.id.baseLayout), "Click on a nearby device to initiate the pairing process.",
                            Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });

        // when you click the refresh bonded devices button it updates the array adapter for the bonded devices list view
        listDevices.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                updateBondedList();
            }
        });

        // when clicking on an item in the bonded devices list view it initiates a connection request to the hardware
        listView.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // gets the associated device
                BluetoothDevice temp = bondedDevices[position];
                ParcelUuid[] uuids = temp.getUuids();

                // only initiates connection if there is a valid UUID for the device
                if(uuids.length > 0){
                    bluetoothThread = new BluetoothThread(handler);
                    bluetoothThread.startConnectThread(temp, null);
                    startProgressBar();
                }
            }
        });

        // when clicking on an item in the nearby devices list view, it initiates a bluetooth pairing process with that device
        nearbyDevices.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bluetoothAdapter.cancelDiscovery();

                String devName = nearbyDevicesAL.get(position).getName();

                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
                    Log.d(TAG, "onItemClick: Trying to create bond with " + devName);
                    nearbyDevicesAL.get(position).createBond();
                }
            }
        });
    }


    /**
     * Updates the bonded device list
     */
    public void updateBondedList(){
        Set<BluetoothDevice> bt = bluetoothAdapter.getBondedDevices();
        bondedDevices = new BluetoothDevice[bt.size()];
        String[] strings = new String[bt.size()];
        int index = 0;
        if(bt.size() > 0){
            for(BluetoothDevice device : bt){
                bondedDevices[index] = device;
                strings[index] = device.getName();
                index++;
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, strings);
            listView.setAdapter(arrayAdapter);
            Snackbar.make(findViewById(R.id.baseLayout), "Click on a bonded device to connect to it.",
                    Snackbar.LENGTH_SHORT)
                    .show();
        }
        else{
            Snackbar.make(findViewById(R.id.baseLayout), "No devices bonded",
                    Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * handles the call back if we did not have the bluetooth and location permissions
     * and initiates the discovery of nearby devices
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_LOCATION){
            if(grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)){
                Log.d(TAG, "onRequestPermissionsResult: permissions were granted!");
                nearbyDevicesAL = new ArrayList<>();
                nearbyDevicesStrings = new ArrayList<>();
                nearbyArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, nearbyDevicesStrings);
                nearbyDevices.setAdapter(nearbyArrayAdapter);
                startDiscovery();
                Snackbar.make(findViewById(R.id.baseLayout), "Click on a nearby device to initiate the pairing process.",
                        Snackbar.LENGTH_SHORT)
                        .show();
                startDiscovery();
            }
        }
    }

    /**
     * starts discovery for nearby devices using the built in bluetooth adapter function startDiscovery()
     */
    private void startDiscovery(){
        if(bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();
    }

    // creating the handler for dealing with callbacks from the bluetooth threads used
    Handler handler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch(msg.what){
                case STATE_LISTENING:
                    Log.d(TAG, "handleMessage: listening");
                    break;
                case STATE_CONNECTING:
                    Log.d(TAG, "handleMessage: connecting");
                    break;
                case STATE_CONNECTED:
                    Log.d(TAG, "handleMessage: connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    Log.d(TAG, "handleMessage: connection failed :(");
                    endProgressBar();
                    Snackbar.make(findViewById(R.id.baseLayout), "Error: connection could not be made",
                            Snackbar.LENGTH_SHORT)
                            .show();
                    break;
                case STATE_CONNECTED_THREAD_SUCCESS:
                    // connection was created and we send a prompt with what we want back
                    // either a spriometer ID or DVT ID
                    byte[] spiroBytes = "spiroid\r\n".getBytes(Charset.defaultCharset());
                    byte[] dvtBytes = "dvtid\r\n".getBytes(Charset.defaultCharset());
                    if(isSpiro){
                        bluetoothThread.sendMessage(spiroBytes);
                    }
                    else{
                        bluetoothThread.sendMessage(dvtBytes);
                    }
                    Log.d(TAG, "handleMessage: success in sending message");
                    break;
                case STATE_CONNECTED_THREAD_FAILED:
                    Log.d(TAG, "handleMessage: connected thread failed...closing it promptly");
                    bluetoothThread.endConnectThread();
                    bluetoothThread.endConnectedThread();
                    unregisterReceiver(nearbyReceiver);
                    unregisterReceiver(bondReciever);

                    endProgressBar();
                    Intent failedReturnIntent = new Intent();
                    setResult(RESULT_CANCELED, failedReturnIntent);
                    finish();
                    break;
                case STATE_MESSAGE_RECEIVED:
                    // this handles the response from the hardware when we asked to give us a device ID
                    byte[] readBuff = (byte[]) msg.obj;
                    String[] deviceId = new String(readBuff, 0, msg.arg1).split("\\n");

                    String realDevId = deviceId[0].trim();
                    bluetoothThread.endConnectThread();
                    bluetoothThread.endConnectedThread();

                    unregisterReceiver(nearbyReceiver);
                    unregisterReceiver(bondReciever);

                    endProgressBar();
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("isSpiro", isSpiro);
                    returnIntent.putExtra("deviceUuid", realDevId);
                    setResult(RESULT_OK, returnIntent);
                    finish();

                    break;
            }
            return true;
        }
    });

    /**
     * starts the progress bar
     */
    private void startProgressBar(){
        inAnim = new AlphaAnimation(0f, 1f);
        inAnim.setDuration(200);
        progressBarHolder.setAnimation(inAnim);
        progressBarHolder.setVisibility(View.VISIBLE);
    }

    /**
     * ends the progress bar
     */
    private void endProgressBar(){
        outAnim = new AlphaAnimation(1f, 0f);
        outAnim.setDuration(200);
        progressBarHolder.setAnimation(outAnim);
        progressBarHolder.setVisibility(View.GONE);
    }

    /**
     * starts the connected thread with the device we want...this will enable the hardware to send us back data
     * @param socket a bluetooth socket with the connetion to the device we want
     */
    public static void manageConnectedSocket(BluetoothSocket socket){
        bluetoothThread.startConnectedThread(socket);
        Log.d(TAG, "manageConnectedSocket: managing open connect socket");
    }


    @Override
    public void onBackPressed() {
        try{
            unregisterReceiver(nearbyReceiver);
            unregisterReceiver(bondReciever);
        }
        catch(Exception e){
            Log.d(TAG, "onBackPressed:receivers already unregistered");
        }

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        try{
            unregisterReceiver(nearbyReceiver);
            unregisterReceiver(bondReciever);
        }
        catch(Exception e){
            Log.d(TAG, "onDestroy: receivers already unregistered");
        }

        super.onDestroy();
    }
}
