package com.ut.data.dataSource.remote.udp.cmd;


import com.ut.data.dataSource.remote.udp.Data.HostDeviceInfo;
import com.ut.data.dataSource.remote.udp.Msg;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangkaifan on 2018/6/8.
 */

public class SearchHostCmd extends CmdBase<HostDeviceInfo>{

    @Override
    public Msg build() {
        Msg msg = new Msg();
        msg.setLinkCmd((byte) Info.LinkCMD.SINGLE_DATA_FRAME);
        msg.setAppCmd((byte) Info.AppCMD.BROCAST_SEARCH_HOST);
        msg.setSrcMac(netHelper.getSrcMac());
        msg.setDestMac(BROADCAST_DEST_MAC);
        return msg;
    }

    @Override
    HostDeviceInfo parse(Msg msg) {
        return null;
    }

    @Override
    List<HostDeviceInfo> parse(List<Msg> msgs) {

        List<HostDeviceInfo> hostDeviceInfos = new ArrayList<>();
        for (Msg msg : msgs) {
            HostDeviceInfo hostDeviceInfo = new HostDeviceInfo();
            hostDeviceInfo.mac = msg.getSrcMac();

            ByteBuffer buffer = ByteBuffer.wrap(msg.getContent());
            hostDeviceInfo.runEnvironment = buffer.get();
            buffer.get(hostDeviceInfo.name);

            int i=0;
            for (; i<hostDeviceInfo.name.length; i++) {
                if (hostDeviceInfo.name[i] == 0) {
                    break;
                }
            }
            hostDeviceInfo.nameLength = i;

            buffer.get(hostDeviceInfo.key);
            hostDeviceInfo.deviceType = buffer.get();
            hostDeviceInfo.protocol = buffer.get();
            buffer.get(hostDeviceInfo.iPAddress);
            buffer.get(hostDeviceInfo.port);

            hostDeviceInfos.add(hostDeviceInfo);
        }
        return hostDeviceInfos;
    }
}
