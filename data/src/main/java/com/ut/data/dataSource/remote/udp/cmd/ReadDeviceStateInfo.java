package com.ut.data.dataSource.remote.udp.cmd;

import com.ut.data.dataSource.remote.udp.Msg;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by huangkaifan on 2018/6/12.
 */

public class ReadDeviceStateInfo extends CmdBase<ReadDeviceStateInfo.Data> {

    @Override
    public Msg build() {
        Msg msg = new Msg();
        msg.setLinkCmd((byte) Info.LinkCMD.MULTI_INFO_FRAME);
        msg.setAppCmd((byte) Info.AppCMD.READ_DEVICE_STATE);
        msg.setSrcMac(netHelper.getSrcMac());
        msg.setDestMac(BROADCAST_DEST_MAC);
        return msg;
    }

    @Override
    Data parse(Msg msg) {
        ByteBuffer buffer = ByteBuffer.wrap(msg.getContent());
        Data data = new Data();
        buffer.get(data.frameNum);
        buffer.get(data.totalFrame);
        buffer.get(data.deviceCount);
        return data;
    }

    @Override
    List<Data> parse(List<Msg> msgs) {
        return null;
    }

    public static class Data {
        byte[] frameNum = new byte[2];
        byte[] totalFrame = new byte[2];
        byte[] deviceCount = new byte[2];
    }
}
