package com.example.bttesting;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class DevicesListAdapter extends ArrayAdapter<BluetoothDevice> {

    private LayoutInflater mLayoutInterflater;
    private ArrayList<BluetoothDevice> mDevices;
    private int mViewResourceId;

    public DevicesListAdapter(Context context, int tvResourceId, ArrayList<BluetoothDevice> devices) {
        super(context, tvResourceId, devices);
        this.mDevices = devices;
        mLayoutInterflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = tvResourceId;
    }

    public View getView(int position, View converterView, ViewGroup parent){
        converterView = mLayoutInterflater.inflate(mViewResourceId, null);

        BluetoothDevice device = mDevices.get(position);

        if (device != null){
            TextView deviceName = (TextView) converterView.findViewById(R.id.tvDeviceName);
            TextView deviceAddress = (TextView) converterView.findViewById(R.id.tvDeviceAddress);

            if (deviceName != null){
                deviceName.setText(device.getName());
            }
            if (device != null){
                deviceAddress.setText(device.getAddress());
            }
        }

        return converterView;
    }
}
