package com.ut.data.dataSource.remote.bluetooth.cmd;

import com.ut.data.dataSource.remote.bluetooth.BleMsg;

public class OpenBleLock extends BleCmdBase<OpenBleLock.Data>{


    @Override
    public BleMsg build() {
        BleMsg bleMsg = new BleMsg();
        bleMsg.setEncrypt(false);
        bleMsg.setCode((byte) 0x04);

        byte[] content = new byte[3];
        content[0] = 2;
        content[1] = 7;
        content[2] = 1;
        bleMsg.setContent(content);
        return bleMsg;
    }

    @Override
    Data parse(BleMsg msg) {
        Data data = new Data();
        data.result = msg.getContent()[0];
        return data;
    }

    public static class Data {
        public byte result;
    }
}
