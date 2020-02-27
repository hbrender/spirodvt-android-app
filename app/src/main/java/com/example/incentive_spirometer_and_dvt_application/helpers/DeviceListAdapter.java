package com.example.incentive_spirometer_and_dvt_application.helpers;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.example.incentive_spirometer_and_dvt_application.R;

public class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {
    private LayoutInflater layoutInflater;
    private ArrayList<BluetoothDevice> devices;
    private int viewId;

    public DeviceListAdapter(Context context, int resourceId, ArrayList<BluetoothDevice> newDevices){
        super(context, resourceId, newDevices);
        this.devices = newDevices;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewId = resourceId;
    }

    public View getView(int position, View view, ViewGroup parent){
        view = layoutInflater.inflate(viewId, null);
        BluetoothDevice device = devices.get(position);

        if(device != null){
            TextView deviceName = (TextView) view.findViewById(R.id.deviceName);
            TextView deviceAddress = (TextView) view.findViewById(R.id.deviceAddress);

            if (deviceName != null){
                deviceName.setText(device.getName());
            }
            if(deviceAddress != null){
                deviceAddress.setText(device.getAddress());
            }
        }
        return view;
    }

}
