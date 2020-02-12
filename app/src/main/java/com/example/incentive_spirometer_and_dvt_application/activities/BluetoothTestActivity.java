package com.example.incentive_spirometer_and_dvt_application.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import java.util.Set;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.incentive_spirometer_and_dvt_application.R;

public class BluetoothTestActivity extends AppCompatActivity {
    private static final String TAG = "BluetoothTest";
    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;
    private BluetoothAdapter spiroAdapter;

    TextView mStatusBlueTv, mPairedTv;
    ImageView mBlueIv;
    Button mOnBtn, mOffBtn, mDiscoverBtn, mPairedBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_test);

        mStatusBlueTv = (TextView) findViewById(R.id.statusBluetoothTv);
        mPairedTv     = (TextView) findViewById(R.id.pairedTv);
        mBlueIv       = (ImageView) findViewById(R.id.bluetoothIv);
        mOnBtn        = (Button) findViewById(R.id.onBtn);
        mOffBtn       = (Button) findViewById(R.id.offBtn);
        mDiscoverBtn  = (Button) findViewById(R.id.discoverableBtn);
        mPairedBtn    = (Button) findViewById(R.id.pairedBtn);

        spiroAdapter = BluetoothAdapter.getDefaultAdapter();

        if(spiroAdapter == null){
            mStatusBlueTv.setText("Bt is not available");
        }
        else {
            mStatusBlueTv.setText("bt is available");
            if(spiroAdapter.isEnabled()){
                mBlueIv.setImageResource(R.drawable.ic_action_on);
            }
            else{
                mBlueIv.setImageResource(R.drawable.ic_action_off);
            }
        }
    }
    
    public void onClick(View v){
        switch(v.getId()){
            case R.id.onBtn:
                if(!spiroAdapter.isEnabled()){
                    showToast("Turning On Bluetooth...");
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                }
                else{
                    showToast("Bluetooth is already on");
                }
                break;
            case R.id.discoverableBtn:
                if (!spiroAdapter.isDiscovering()){
                    showToast("Making Your Device Discoverable");
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(intent, REQUEST_DISCOVER_BT);
                }
                break;
            case R.id.offBtn:
                if (spiroAdapter.isEnabled()){
                    spiroAdapter.disable();
                    showToast("Turning Bluetooth Off");
                    mBlueIv.setImageResource(R.drawable.ic_action_off);
                }
                else {
                    showToast("Bluetooth is already off");
                }
                break;
            case R.id.pairedBtn:
                if (spiroAdapter.isEnabled()){
                    mPairedTv.setText("Paired Devices");
                    Set<BluetoothDevice> devices = spiroAdapter.getBondedDevices();
                    for (BluetoothDevice device: devices){
                        mPairedTv.append("\nDevice: " + device.getName()+ ", " + device);
                    }
                }
                else {
                    //bluetooth is off so can't get paired devices
                    showToast("Turn on bluetooth to get paired devices");
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK){
                    //bluetooth is on
                    mBlueIv.setImageResource(R.drawable.ic_action_on);
                    showToast("Bluetooth is on");
                }
                else {
                    //user denied to turn bluetooth on
                    showToast("could't on bluetooth");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //toast message function
    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
