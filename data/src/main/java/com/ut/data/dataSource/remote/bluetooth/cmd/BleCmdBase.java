package com.ut.data.dataSource.remote.bluetooth.cmd;

import com.ut.data.dataSource.remote.bluetooth.BleHelper;
import com.ut.data.dataSource.remote.bluetooth.BleMsg;
import com.ut.data.dataSource.remote.udp.Msg;
import com.ut.data.dataSource.remote.udp.NetHelper;
import com.ut.data.dataSource.remote.udp.cmd.BroadcastCallBack;

import java.util.List;

/**
 * Created by huangkaifan on 2018/6/8.
 */

public abstract class BleCmdBase<T> {

    protected BleHelper bleHelper;
    private int timeoutCount;

    public BleCmdBase() {
        this.bleHelper = BleHelper.getInstance();
    }

    abstract public BleMsg build();
    abstract T parse(BleMsg msg);

    public void sendMsg(final BleCallBack<T> callBack) {

        if (bleHelper == null) {
            return;
        }

        timeoutCount = 0;

        BleMsg msg = build();

        bleHelper.asyncSend(msg, new BleHelper.ResponseListener() {

            @Override
            public void timeout(BleMsg sendMsg) {
                // TODO Auto-generated method stub
                System.out.println("timeout");
                timeoutCount ++;

                if (timeoutCount >= 3) {
                    if (callBack != null) {
                        callBack.timeout();
                    }
                } else {
                    bleHelper.asyncSend(sendMsg, this);
                }
            }

            @Override
            public void onACk(BleMsg sendMsg, BleMsg responseMsg) {
                if (callBack != null) {
                    callBack.success(parse(responseMsg));
                }
            }

            @Override
            public void onNAk(BleMsg sendMsg) {
                if (callBack != null) {
                    callBack.fail();
                }
            }
        });
    }
}
