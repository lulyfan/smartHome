package com.ut.smartHome;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

public class AppData {
    public MutableLiveData<Boolean> loginState = new MutableLiveData<>();
    public MutableLiveData<Boolean> isInBleLockPage = new MutableLiveData<>();   //是否APP处于蓝牙锁界面

    private static AppData INSTANCE = new AppData();

    public static AppData getInstance() {
        return INSTANCE;
    }
}
