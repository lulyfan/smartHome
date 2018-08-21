package com.ut.data.dataSource.remote.udp.cmd;

/**
 * Created by huangkaifan on 2018/6/12.
 */

public interface UDPCallBack<T> {
    void success(T result);
    void fail(int errorCode);
    void timeout();
}