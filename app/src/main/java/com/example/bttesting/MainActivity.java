package com.example.bttesting;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "MainActivity";
    // Get the default adapter
    BluetoothAdapter mbluetoothAdapter;
    Button btnEnableScan;
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public DevicesListAdapter mDevicesListAdapter;
    ListView lvNewdevice;


    //create broadcast receiver
    private final BroadcastReceiver m1BroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(mbluetoothAdapter.ACTION_STATE_CHANGED)){
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,BluetoothAdapter.ERROR);

                switch (state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "mBroadcastReceiver : State OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver : State TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver : State TURNING ON");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver : State ON");
                        break;
                }
            }
        }
    };

    //create broadcast receiver
    private final BroadcastReceiver m2BroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(mbluetoothAdapter.ACTION_SCAN_MODE_CHANGED)){
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE,BluetoothAdapter.ERROR);

                switch (state){
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "m2BroadcastReceiver: Discoverable Enable");
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "m2BroadcastReceiver : Discoverable Disable. Able to receive conection.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "m2BroadcastReceiver : Discoverable Disable. Not able to receive conection.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "m2BroadcastReceiver : Conecting...");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "m2BroadcastReceiver : Connected...");
                        break;
                }
            }
        }
    };

    /*Broadcast for listing devices that are not yet paired
    * execute by btnScan() method
    * */
    private BroadcastReceiver m3BroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Log.d(TAG, "onReceive: " + device.getName() + ":" + device.getAddress());
                mDevicesListAdapter = new DevicesListAdapter(context, R.layout.devices_adpter_view, mBTDevices);
                lvNewdevice.setAdapter(mDevicesListAdapter);
            }
        }
    };
    private BroadcastReceiver m4BroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED");
                }
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING");
                }
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE){
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE");
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        //Create destroy methods to unregiste receiver
        Log.d(TAG, "onDestroy: Called");
        super.onDestroy();
        unregisterReceiver(m1BroadcastReceiver);
        unregisterReceiver(m2BroadcastReceiver);
        unregisterReceiver(m3BroadcastReceiver);
        unregisterReceiver(m4BroadcastReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up enable / disable bt service button
        Button btnOnOff = (Button) findViewById(R.id.btnEnableBT);
        //set up enable / disable bt scanner
        btnEnableScan = (Button) findViewById(R.id.btnScanDevices);

        //Listing device
        lvNewdevice = (ListView) findViewById(R.id.lvNewdevice);
        mBTDevices = new ArrayList<>();

        // update list view device
        lvNewdevice.setOnItemClickListener(MainActivity.this);
        //pairing device
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(m4BroadcastReceiver, filter);
        //create bt adapter
        mbluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //set enable\disable bt service button
        btnOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: enabling/disabling Bluetooth");
                enableDisableBT();
            }
        });
    }

    private void enableDisableBT() {
        //enable / disable bt service
        if (mbluetoothAdapter == null){
            //checking mobile which do have bt service
            Log.d(TAG, "enbleDisableBT: Does mot BT servivec");
        }
        if (!mbluetoothAdapter.isEnabled()){
            //checking mobile that disable bt service
            Log.d(TAG, "enbleBT: Enabling BT");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //turn on bt service
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            //storing bt state intent
            registerReceiver(m1BroadcastReceiver, BTIntent);
        }
        if (mbluetoothAdapter.isEnabled()){
            //checking mobile that enable bt service
            Log.d(TAG, "enableBT: Disabling BT");
            //turn off bt service
            mbluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            //storing bt state intent
            registerReceiver(m1BroadcastReceiver, BTIntent);
        }
    }

    public void btnEnableScan(View view) {
        Log.d(TAG, "btnEnableScan: Making device discoverable for 300 second");

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
        startActivity(discoverableIntent);

        IntentFilter intentFilter = new IntentFilter(mbluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(m2BroadcastReceiver,intentFilter);

    }

    public void btnScan(View view) {
        Log.d(TAG, "btnScan: Looking for unpaired devices.");

        if (mbluetoothAdapter.isDiscovering()){
            mbluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnScan: Cancel scan.");

            checkBTPermission();

            mbluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(m3BroadcastReceiver, discoverDevicesIntent);

        }
        if (!mbluetoothAdapter.isDiscovering()){
            //checking BT permission in manifest
            checkBTPermission();

            mbluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(m3BroadcastReceiver, discoverDevicesIntent);
        }
    }
    /** this method is required for all devices running API23+
     *  Android must programmatically check the permission for bluetooth. Putting the proper permission
     *  in manifest is not enough
     * NOTe: This will only execute on version > Lollipop because it is not needed otherwise.
     * **/
    private void checkBTPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0){
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            }else {
                Log.d(TAG, "checkBTPermission: No need to check permissions. SDK version < LOLIPOP");
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //cancel discovery
        mbluetoothAdapter.cancelDiscovery();

        Log.d(TAG, "onItemClick: you click on device.");
        String deviceName = mBTDevices.get(position).getName();
        String deviceAddress = mBTDevices.get(position).getAddress();

        Log.d(TAG, "onItemClick: device name:" + deviceName);
        Log.d(TAG, "onItemClick: device address:" + deviceAddress);

        //create bond
        //checking API version
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            Log.d(TAG, "Trying to pair " + deviceName);
            mBTDevices.get(position).createBond();
        }
    }
}
