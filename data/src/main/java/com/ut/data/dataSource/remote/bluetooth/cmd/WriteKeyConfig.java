package com.ut.data.dataSource.remote.bluetooth.cmd;

import com.ut.data.dataSource.remote.bluetooth.BleMsg;
import com.ut.data.dataSource.remote.bluetooth.Data.KeyInfo;

import java.nio.ByteBuffer;
import java.util.List;

public class WriteKeyConfig extends BleCmdBase<WriteKeyConfig.Data>{

    private List<KeyInfo> keyInfos;

    public void setKeyInfos(List<KeyInfo> keyInfos) {
        this.keyInfos = keyInfos;
    }

    @Override
    public BleMsg build() {
        BleMsg bleMsg = new BleMsg();
        bleMsg.setEncrypt(false);
        bleMsg.setCode((byte) 0x0C);

        ByteBuffer buffer = ByteBuffer.allocate(1 + 4 * keyInfos.size());
        buffer.put((byte) keyInfos.size());

        for (KeyInfo keyInfo : keyInfos) {
            buffer.put(keyInfo.keyNo);
            buffer.put(keyInfo.type);
            buffer.put(keyInfo.auth);
            buffer.put(keyInfo.keyInNum);
        }

        bleMsg.setContent(buffer.array());
        return bleMsg;
    }

    @Override
    Data parse(BleMsg msg) {

        Data data = new Data();
        data.result = msg.getContent()[0];
        return data;
    }

    public static class Data {
        byte result;

        public boolean isSuccess() {
            return result == 1;
        }
    }
}
