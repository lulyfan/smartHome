/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package com.ut.smartHome;

import android.Manifest;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ut.data.dataSource.remote.bluetooth.BleClient;
import com.ut.data.dataSource.remote.bluetooth.BleHelper;
import com.ut.data.dataSource.remote.bluetooth.BleMsg;
import com.ut.data.dataSource.remote.bluetooth.jobluetooth.BlueToothParams;
import com.ut.data.dataSource.remote.bluetooth.jobluetooth.BluetoothLeService;
import com.ut.smartHome.plugin.SmartHomeContext;
import com.ut.smartHome.plugin.handler.LockPageHandler;

import org.apache.cordova.*;

import java.util.UUID;

public class MainActivity extends CordovaActivity
{
    private View startView;
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothLeService bluetoothLeService;
    private Handler mHandler = new Handler();

    private static final String BLE_TAG = "bluetoothLock";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 2;
    private static final UUID[] DEST_UUIDS = {UUID.fromString("0000b350-0000-1000-8000-00805f9b34fb")};
    private boolean mScanning;
    private BluetoothAdapter mBluetoothAdapter;
    private BleClient bleClient;
    private boolean isInLockPage;
    private boolean isForbidConnect;
    private BleHelper bleHelper;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // enable Cordova apps to be started in the background
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getBoolean("cdvStartInBackground", false)) {
            moveTaskToBack(true);
        }

        loadUrl(launchUrl);

        openStartImage();

        MutableLiveData<Boolean> loginState = AppData.getInstance().loginState;
        loginState.observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                Log.i("login", "receive login result");
                startView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        closeStartImage();
                    }
                }, 2000);
            }
        });

        Intent intent = new Intent(this, BluetoothLeService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    private void openStartImage() {

        startView = getLayoutInflater().inflate(R.layout.start, null);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addContentView(startView, layoutParams);
    }

    private void closeStartImage() {
        startView.setVisibility(View.GONE);
    }

    @Override
    public Object onMessage(String id, Object data) {
        super.onMessage(id, data);
        
        if (LockPageHandler.OPEN_BLUETOOTH.equals(id)) {

            isInLockPage = true;
            connectBleLock();

        } else if (LockPageHandler.CLOSE_BLUETOOTH.equals(id)) {

            isInLockPage = false;
            closeBleLock();
        }
        return null;
    }

    //isForbidConnect表示是否禁止之后的重连
    private void closeBleLock() {
        Log.i(BLE_TAG, "close bluetooth");

        if (mScanning) {
            scanLeDevice(false);
        }

        bluetoothLeService.disconnect(-1);
        appView.postMessage(LockPageHandler.BLE_DISCONNECTED, null);
    }

    private void reconnectBleLock(int delay) {
        closeBleLock();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                connectBleLock();
            }
        }, delay);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isForbidConnect = false;
        if (isInLockPage) {
            connectBleLock();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        isForbidConnect = true;
        closeBleLock();
        if (mBluetoothAdapter !=null) {
            mBluetoothAdapter.disable();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unbindService(serviceConnection);
        unregisterReceiver(mGattUpdateReceiver);
    }

    private void connectBleLock() {

        if (isForbidConnect) {
            return;
        }

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, com.ut.data.R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            return;
        }

        if (mBluetoothAdapter == null) {
            BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }

        scanDevice();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            scanDevice();
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bluetoothLeService = ((BluetoothLeService.LocalBinder)service).getService();
            bluetoothLeService.initialize();

            bleClient = new BleClient(bluetoothLeService);
            bleHelper = BleHelper.getInstance(bleClient);
            bleHelper.setReceiveListener(new BleHelper.ReceiveListener() {
                @Override
                public void onReceive(BleMsg msg) {

                    Log.i("data", "parse msg");
                    int code = msg.getCode();

                    switch (code) {

                        case 0x08:   //收到蓝牙锁状态
                            Log.i(BLE_TAG, "receive bluetoothLock status");
                            appView.postMessage(LockPageHandler.BLE_STATUS, msg);
                            BleMsg response = new BleMsg();
                            response.setCode((byte) 0x08);
                            bleHelper.asyncSend(response);
                            Log.i(BLE_TAG, "send response");
                            break;

                        case 0x09:   //收到蓝牙关闭消息
                            Log.i(BLE_TAG, "receive bluetoothLock close msg");
                            reconnectBleLock(5000);
                            break;

                            default:
                    }
                }
            });

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            BleHelper.getInstance().close();
        }
    };

    private void scanDevice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                return;
        }
        scanLeDevice(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_COARSE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            scanLeDevice(true);
        }
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {

            mScanning = true;
            mBluetoothAdapter.startLeScan(DEST_UUIDS, mLeScanCallback);
            Log.i(BLE_TAG, "start scan");
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            Log.i(BLE_TAG, "stop scan");
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

            Log.i(BLE_TAG, "find bluetooth device name:" + device.getName() + " mac:" + device.getAddress());
            scanLeDevice(false);

            String address = device.getAddress();
            String blueMac = address.replace(':', '-');
            SmartHomeContext smartHomeContext = SmartHomeContext.getInstance(MainActivity.this);
            smartHomeContext.setBlueLockMac(blueMac);

            bluetoothLeService.connect(address, 6);
            Log.i(BLE_TAG, "start connect bluetooth...");
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BlueToothParams.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BlueToothParams.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BlueToothParams.ACTION_GATT_DATARECEIVED);
        intentFilter.addAction(BlueToothParams.ACTION_GATT_READCHARACTERISTICSUCCESS);
        intentFilter.addAction(BlueToothParams.ACTION_GATT_READCHARACTERISTICERROR);
        intentFilter.addAction(BlueToothParams.ACTION_GATT_HANDLERDATAERROR);
        intentFilter.addAction(BlueToothParams.ACTION_GATT_SENDDATALENGTHEXCEED);
        intentFilter.addAction(BlueToothParams.ACTION_GATT_SENDDATAERROR);
        intentFilter.addAction(BlueToothParams.ACTION_GATT_SENDDATAEND);
        intentFilter.addAction(BlueToothParams.ACTION_GATT_SENDDATAB355);
        intentFilter.addAction(BlueToothParams.ACTION_GATT_SENDDATAB358);
        intentFilter.addAction(BlueToothParams.ACTION_GATT_SENDDATASUCCESS);
        intentFilter.addAction(BlueToothParams.ACTION_GATT_CONNECTTIMEOUT);
        return intentFilter;
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.i("testBle", "====action11 = " + action);
            //连接成功 并读取characteristic成功
            switch (action) {
                case BlueToothParams.ACTION_GATT_READCHARACTERISTICSUCCESS:
                    Log.i(BLE_TAG, "ACTION_GATT_READCHARACTERISTICSUCCESS");
                    appView.postMessage(LockPageHandler.BLE_CONNECTED, null);
                    break;

                case BlueToothParams.ACTION_GATT_DISCONNECTED:
                    Log.i(BLE_TAG, "bluetooth disconnected");
                    appView.postMessage(LockPageHandler.BLE_DISCONNECTED, null);
                    reconnectBleLock(1000);
                    break;

                case BlueToothParams.ACTION_GATT_CONNECTTIMEOUT:
                    Log.i(BLE_TAG, "bluetooth connect timeout,  blueblooth ");
                    reconnectBleLock(1000);
                    break;

                case BlueToothParams.ACTION_GATT_CONNECTED:
                    Log.i(BLE_TAG, "bluetooth connected");
                    bleClient.reset();
                    break;

                case BlueToothParams.ACTION_GATT_DATARECEIVED:
                    byte[] data = intent.getByteArrayExtra(BluetoothLeService.DATA_NAME);
                    bleClient.receiveData(data);
                    break;

                case BlueToothParams.ACTION_GATT_READCHARACTERISTICERROR:
                    Log.i(BLE_TAG, "ACTION_GATT_READCHARACTERISTICERROR");
                    break;

                case BlueToothParams.ACTION_GATT_HANDLERDATAERROR:
                    Log.i(BLE_TAG, "ACTION_GATT_HANDLERDATAERROR");
                    break;

                case BlueToothParams.ACTION_GATT_SENDDATALENGTHEXCEED:
                    Log.i(BLE_TAG, "ACTION_GATT_SENDDATALENGTHEXCEED");
                    break;

                case BlueToothParams.ACTION_GATT_SENDDATAERROR:
                    Log.i(BLE_TAG, "ACTION_GATT_SENDDATAERROR");
                    break;

                case BlueToothParams.ACTION_GATT_SENDDATAEND:
                    Log.i(BLE_TAG, "ACTION_GATT_SENDDATAEND");
                    break;

                case BlueToothParams.ACTION_GATT_SENDDATAB355:
                    Log.i(BLE_TAG, "ACTION_GATT_SENDDATAB355");
                    break;

                case BlueToothParams.ACTION_GATT_SENDDATAB358:
                    Log.i(BLE_TAG, "ACTION_GATT_SENDDATAB358");
                    break;

                case BlueToothParams.ACTION_GATT_SENDDATASUCCESS:
                    Log.i(BLE_TAG, "ACTION_GATT_SENDDATASUCCESS");
                    break;

                default:

                    break;
            }
        }


    };
}
