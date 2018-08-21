package com.ut.data.dataSource.remote.bluetooth.cmd;

import com.ut.data.dataSource.remote.bluetooth.BleMsg;
import com.ut.data.dataSource.remote.bluetooth.Data.KeyInfo;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class GetKeyConfig extends BleCmdBase<GetKeyConfig.Data>{

    @Override
    public BleMsg build() {
        BleMsg bleMsg = new BleMsg();
        bleMsg.setEncrypt(false);
        bleMsg.setCode((byte) 0x0B);
        return bleMsg;
    }

    @Override
    Data parse(BleMsg msg) {
        ByteBuffer buffer = ByteBuffer.wrap(msg.getContent());
        Data data = new Data();
        data.keyNum = buffer.get();
        data.keyInfos = new ArrayList<>();

        for (int i=0; i<data.keyNum; i++) {
            KeyInfo keyInfo = new KeyInfo();
            keyInfo.keyNo = buffer.get();
            keyInfo.type = buffer.get();
            keyInfo.auth = buffer.get();
            keyInfo.keyInNum = buffer.get();
            data.keyInfos.add(keyInfo);
        }

        return data;
    }

    public static class Data {

        public byte keyNum;
        public List<KeyInfo> keyInfos;
    }
}
