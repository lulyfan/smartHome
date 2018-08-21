package com.ut.smartHome.plugin;

import android.os.Bundle;

import com.ut.data.dataSource.remote.bluetooth.BleMsg;
import com.ut.smartHome.plugin.handler.LockPageHandler;

public class LockPlugins extends BasePlugin {

    private LockPageHandler lockPageHandler;

    @Override
    protected void pluginInitialize() {

        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                SmartHomeContext smartHomeContext = SmartHomeContext.getInstance(cordova.getContext());

                lockPageHandler = new LockPageHandler(smartHomeContext, LockPlugins.this);
                addPluginHandler(lockPageHandler);
            }
        });
    }

    @Override
    public Object onMessage(String id, Object data) {
        switch (id) {
            case LockPageHandler.BLE_CONNECTED:

                lockPageHandler.handleConnect();
                System.out.println("handleConnect");

                break;
            case LockPageHandler.BLE_DISCONNECTED:

                lockPageHandler.handleDisconnect();
                System.out.println("handleDisconnect");

                break;
            case LockPageHandler.BLE_STATUS:

                lockPageHandler.handleBleStatus((BleMsg) data);
                break;
        }

        return null;
    }

    @Override
    public Bundle onSaveInstanceState() {
        return super.onSaveInstanceState();
    }
}
