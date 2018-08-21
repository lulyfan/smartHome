package com.ut.smartHome.plugin;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.WebView;

import com.ut.data.dataSource.local.AppDataBase;
import com.ut.data.dataSource.local.LocalDataSource;
import com.ut.data.dataSource.remote.http.HttpDataSource;
import com.ut.data.dataSource.remote.http.WebSocketHelper;
import com.ut.data.dataSource.remote.udp.UdpDataSource;
import com.ut.data.util.MacUtil;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;

public class SmartHomeContext {

    private HttpDataSource httpDataSource;
    private UdpDataSource udpDataSource;
    private LocalDataSource localDataSource;
    private String currentHostMac;
    private int currentHostId;
    private int userId;
    private boolean isHasCurrentHost;
    private byte[] mac;               //本地设备的mac
    private String blueLockMac;

    public static final String PREFERENCE_NAME = "smartHomeData";
    public static final String KEY_ACCOUNT = "account";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_BLE_PWD = "blePwd";
    public static final String KEY_IS_AUTO_OPEN_LOCK = "isAutoOpenLock";

    private static SmartHomeContext INSTANCE;

    private SmartHomeContext(Context context) {
        mac = MacUtil.getMacByte(context);

        AppDataBase dataBase = AppDataBase.getInstance(context);
        httpDataSource = HttpDataSource.getInstance();
        udpDataSource = UdpDataSource.getInstance(mac);
        localDataSource = LocalDataSource.getInstance(dataBase.dataDao());
    }

    public synchronized static SmartHomeContext getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SmartHomeContext(context);
        }
        return INSTANCE;
    }

    public String getBlueLockMac() {
        return blueLockMac;
    }

    public void setBlueLockMac(String blueLockMac) {
        this.blueLockMac = blueLockMac;
    }

    public byte[] getMac() {
        return mac;
    }

    public void setMac(byte[] mac) {
        this.mac = mac;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCurrentHostMac() {
        return currentHostMac;
    }

    public void setCurrentHostMac(String currentHostMac) {
        this.currentHostMac = currentHostMac;
    }

    public int getCurrentHostId() {
        return currentHostId;
    }

    public void setCurrentHostId(int currentHostId) {
        this.currentHostId = currentHostId;
    }

    public boolean isHasCurrentHost() {
        return isHasCurrentHost;
    }

    public void setHasCurrentHost(boolean hasCurrentHost) {
        isHasCurrentHost = hasCurrentHost;
    }

    public HttpDataSource getHttpDataSource() {
        return httpDataSource;
    }

    public void setHttpDataSource(HttpDataSource httpDataSource) {
        this.httpDataSource = httpDataSource;
    }

    public LocalDataSource getLocalDataSource() {
        return localDataSource;
    }

    public void setLocalDataSource(LocalDataSource localDataSource) {
        this.localDataSource = localDataSource;
    }

    public UdpDataSource getUdpDataSource() {
        return udpDataSource;
    }

    public void setUdpDataSource(UdpDataSource udpDataSource) {
        this.udpDataSource = udpDataSource;
    }

    public static void saveLoginInfo(Context context, String account, String password) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SmartHomeContext.PREFERENCE_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCOUNT, account);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    public static boolean isHasLoginInfo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SmartHomeContext.PREFERENCE_NAME, Context.MODE_PRIVATE);

        String account = sharedPreferences.getString(KEY_ACCOUNT, "");
        String password = sharedPreferences.getString(KEY_PASSWORD, "");

        return !"".equals(account) && !"".equals(password);
    }

    public static String getAccount(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SmartHomeContext.PREFERENCE_NAME, Context.MODE_PRIVATE);

        return sharedPreferences.getString(KEY_ACCOUNT, "");
    }

    public static String getPassword(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SmartHomeContext.PREFERENCE_NAME, Context.MODE_PRIVATE);

        return sharedPreferences.getString(KEY_PASSWORD, "");
    }

    public static void clearLoginInfo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SmartHomeContext.PREFERENCE_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_ACCOUNT);
        editor.remove(KEY_PASSWORD);
        editor.apply();
    }

    public static void clearBlePws(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SmartHomeContext.PREFERENCE_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_BLE_PWD);
        editor.apply();
    }

    //保存蓝牙锁校验密码
    public static void saveBlePwd(Context context, String pwd) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SmartHomeContext.PREFERENCE_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_BLE_PWD, pwd);
        editor.apply();
    }

    public static String getBlePwd(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SmartHomeContext.PREFERENCE_NAME, Context.MODE_PRIVATE);

        return sharedPreferences.getString(KEY_BLE_PWD, "");
    }

    public static void setIsAutoOpenLock(Context context, boolean isAutoOpenLock) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SmartHomeContext.PREFERENCE_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_AUTO_OPEN_LOCK, isAutoOpenLock);
        editor.apply();
    }

    public static boolean isAutoOpenLock(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SmartHomeContext.PREFERENCE_NAME, Context.MODE_PRIVATE);

        return sharedPreferences.getBoolean(KEY_IS_AUTO_OPEN_LOCK, false);
    }
}
