package com.ut.smartHome.plugin.handler;

import android.app.Activity;
import android.app.FragmentManager;

import com.google.gson.Gson;
import com.ut.data.dataSource.local.AppDataBase;
import com.ut.data.dataSource.local.LocalDataSource;
import com.ut.data.dataSource.remote.http.HttpDataSource;
import com.ut.data.dataSource.remote.udp.UdpDataSource;
import com.ut.smartHome.plugin.BasePlugin;
import com.ut.smartHome.plugin.SmartHomeContext;
import com.ut.smartHome.ui.LoadDialogFragment;

import org.apache.cordova.CordovaPlugin;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public abstract class BasePageHandler implements PluginHandler{

    protected HttpDataSource httpDataSource;
    protected UdpDataSource udpDataSource;
    protected LocalDataSource localDataSource;
    protected SmartHomeContext smartHomeContext;
    protected BasePlugin plugin;
    private Gson gson = new Gson();
    private LoadDialogFragment loadDialogFragment = new LoadDialogFragment();

    public BasePageHandler(SmartHomeContext context, BasePlugin plugin) {
        httpDataSource = context.getHttpDataSource();
        udpDataSource = context.getUdpDataSource();
        localDataSource = context.getLocalDataSource();
        smartHomeContext = context;
        this.plugin = plugin;
    }

    protected String toJsonStr(Object object) {
        return gson.toJson(object);
    }

    public void runOnUIThread(Runnable runnable) {
        plugin.cordova.getActivity().runOnUiThread(runnable);
    }

    public void runOnThreadPool(Runnable runnable) {
        plugin.cordova.getThreadPool().execute(runnable);
    }

    protected void showLoadDialog(String message) {
        Activity activity = (Activity) plugin.getContext();
        FragmentManager fragmentManager = activity.getFragmentManager();
        loadDialogFragment.setMessage(message);
        loadDialogFragment.show(fragmentManager, "load");
    }

    protected void dismissLoadDialog() {
        loadDialogFragment.dismiss();
    }
}
