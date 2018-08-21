package com.ut.data.dataSource.remote.bluetooth.cmd;

import com.ut.data.dataSource.remote.bluetooth.BleMsg;
import com.ut.data.dataSource.remote.bluetooth.Data.BleLockState;
import com.ut.data.dataSource.remote.bluetooth.Data.Status;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class GetBleInfo extends BleCmdBase<GetBleInfo.Data>{

    private byte[] phone = new byte[6];
    private byte[] mac;

    @Override
    public BleMsg build() {
        BleMsg bleMsg = new BleMsg();
        bleMsg.setEncrypt(false);
        bleMsg.setCode((byte) 0x02);

        byte[] content = new byte[12];
        System.arraycopy(phone, 0, content, 0, 6);
        System.arraycopy(mac, 0, content, 0, 6);
        bleMsg.setContent(content);

        return bleMsg;
    }

    @Override
    Data parse(BleMsg msg) {
        ByteBuffer buffer = ByteBuffer.wrap(msg.getContent());
        Data data = new Data();
        buffer.get(data.deviceType);
        buffer.get(data.mac);
        buffer.get(data.hostMac);
        data.hostId = buffer.get();
        data.channelId = buffer.get();
        data.nodeId = buffer.get();
        data.nodeDeviceId = buffer.get();
        buffer.get(data.time);

        byte statusNum = buffer.get();
        data.statusList = new ArrayList<>();

        for (int i=0; i<statusNum; i++) {
            byte devNo = buffer.get();
            byte type = buffer.get();

            Status status = new Status(devNo, type);
            buffer.get(status.value);
            data.statusList.add(status);
        }

        return data;
    }

    public void setPhone(String phoneNum) {
        phoneNum = "0" + phoneNum;
        for (int i=0; i<6; i++) {
            String tmp = phoneNum.substring(i, i+2);

            int num1 = Integer.parseInt(tmp.substring(0, 1));
            int num2 = Integer.parseInt(tmp.substring(1));
            phone[i] = (byte) ((num1 << 4) | num2);
        }
    }

    public void setMac(byte[] mac) {
        this.mac = mac;
    }

    public static class Data {
        byte[] deviceType = new byte[2];
        byte[] mac = new byte[6];
        byte[] hostMac = new byte[6];
        byte hostId;
        byte channelId;
        byte nodeId;
        byte nodeDeviceId;
        byte[] time = new byte[7];
        List<Status> statusList;

        public int getElectricity() {
            return getDevStatus(0);
        }

        public int getLockState() {
          return getDevStatus(6);
        }

        public int getAlarm() {
           return getDevStatus(7);
        }

        public int getDevStatus(int devNo) {
            if (statusList == null) {
                return -1;
            }

            for (Status status : statusList) {
                if (status.devNo == devNo) {
                    return status.value[0];
                }
            }

            return -1;
        }

        public BleLockState getBleLockState() {
            String elec = getElectricity() + "";
            String lockState = getLockState() + "";
            int alarm = getAlarm();

            return new BleLockState(lockState, elec, alarm);
        }
    }
}
