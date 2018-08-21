package com.ut.data.dataSource.remote.bluetooth.cmd;

/**
 * Created by huangkaifan on 2018/6/12.
 */

public interface BleCallBack<T> {
    void success(T result);
    void fail();
    void timeout();
}