package com.ut.data.dataSource.remote.udp.cmd;

import java.util.List;

/**
 * Created by huangkaifan on 2018/6/12.
 */

public interface BroadcastCallBack<T> {
    void success(List<T> result);
    void fail(List<Integer> errorCodes);
    void timeout();
}
