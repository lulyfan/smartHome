package com.ut.data.dataSource.remote.udp.cmd;

import com.ut.data.dataSource.remote.udp.Msg;
import com.ut.data.dataSource.remote.udp.NetHelper;

import java.util.List;

/**
 * Created by huangkaifan on 2018/6/8.
 */

public abstract class CmdBase<T> {

    protected NetHelper netHelper;
    private int timeoutCount;
    private int broadcastTimeoutCount;

    public static final byte[] BROADCAST_DEST_MAC = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};

    public CmdBase() {
        this.netHelper = NetHelper.getInstance();
    }

    abstract public Msg build();
    abstract T parse(Msg msg);
    abstract List<T> parse(List<Msg> msgs);

    public void sendMsg(final UDPCallBack UDPCallBack) {

        if (netHelper == null) {
            return;
        }

        timeoutCount = 0;

        Msg msg = build();

        netHelper.asyncSend(msg, new NetHelper.ResponseListener() {

            @Override
            public void timeout(Msg sendMsg) {
                // TODO Auto-generated method stub
                System.out.println("timeout");
                timeoutCount ++;

                if (timeoutCount >= 3) {
                    if (UDPCallBack != null) {
                        UDPCallBack.timeout();
                    }
                } else {
                    netHelper.asyncSend(sendMsg, this);
                }
            }

            @Override
            public void onACk(Msg sendMsg, Msg responseMsg) {
                responseMsg.printf();

                if (UDPCallBack != null) {
                    UDPCallBack.success(parse(responseMsg));
                }
            }

            @Override
            public void onNAk(Msg sendMsg, int nakCode) {

                if (UDPCallBack != null) {
                    UDPCallBack.fail(nakCode);
                }
            }
        });
    }

    public void broadcastSendMsg(final BroadcastCallBack<T> callBack) {

        if (netHelper == null) {
            return;
        }

        broadcastTimeoutCount = 0;

        Msg msg = build();

        netHelper.asyncBroadcastSend(msg, new NetHelper.BroadcastResponseListener() {

            @Override
            public void onACk(Msg sendMsg, List<Msg> responseMsgs) {

                if (callBack != null) {
                    callBack.success(parse(responseMsgs));
                }
            }

            @Override
            public void onNAk(Msg sendMsg, List<Integer> nakCodes) {

                if (callBack != null) {
                    callBack.fail(nakCodes);
                }
            }

            @Override
            public void timeout(Msg sendMsg) {
                // TODO Auto-generated method stub
                System.out.println("broadcast timeout");
                broadcastTimeoutCount ++;

                if (broadcastTimeoutCount >= 3) {
                    if (callBack != null) {
                        callBack.timeout();
                    }
                } else {
                    netHelper.asyncBroadcastSend(sendMsg, this);
                }
            }


        });
    }
}
