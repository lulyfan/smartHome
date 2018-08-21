package com.ut.data.dataSource.remote.udp;

import com.ut.data.dataSource.remote.udp.Data.HostDeviceInfo;
import com.ut.data.dataSource.remote.udp.cmd.BroadcastCallBack;
import com.ut.data.dataSource.remote.udp.cmd.SearchHostCmd;
import com.ut.data.dataSource.remote.udp.cmd.UDPCallBack;

/**
 * Created by huangkaifan on 2018/6/15.
 */

public class UdpDataSource {

    private static UdpDataSource INSTANCE;
    private static final int PORT = 5001;

    private UdpDataSource(byte[] srcMac) {
        ClientBase client = new UDPClient(PORT);
        NetHelper netHelper = NetHelper.getInstance(client);
        netHelper.setSrcMac(srcMac);
        netHelper.setReceiveListener(new NetHelper.ReceiveListener() {
            @Override
            public void onReceive(Msg msg) {
                System.out.println("receive msg");
            }
        });
    }

    public synchronized static UdpDataSource getInstance(byte[] srcMac) {
        if (INSTANCE == null) {
            INSTANCE = new UdpDataSource(srcMac);
        }
        return INSTANCE;
    }

    public void searchHost(BroadcastCallBack<HostDeviceInfo> UDPCallBack) {
        SearchHostCmd cmd = new SearchHostCmd();
        cmd.broadcastSendMsg(UDPCallBack);
    }
}
