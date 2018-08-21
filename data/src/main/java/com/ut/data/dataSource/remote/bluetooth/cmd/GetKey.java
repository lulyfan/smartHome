package com.ut.data.dataSource.remote.bluetooth.cmd;

import com.ut.data.dataSource.remote.bluetooth.BleMsg;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class GetKey extends BleCmdBase<GetKey.Data>{

    byte[] key = new byte[6];
    byte isAutoOpenLock;

    @Override
    public BleMsg build() {
        BleMsg msg = new BleMsg();
        msg.setEncrypt(false);
        msg.setCode((byte) 0x01);

        byte[] content = new byte[7];
        System.arraycopy(key, 0, content, 0, key.length);
        content[6] = isAutoOpenLock;
        msg.setContent(content);

        return msg;
    }

    @Override
    Data parse(BleMsg msg) {
        ByteBuffer buffer = ByteBuffer.wrap(msg.getContent());
        Data data = new Data();
        data.verifyResult = buffer.get();

        if (buffer.hasRemaining()) {
            data.key = buffer.getShort();
        }

        return data;
    }

    public void setKey(String pwd) {
        try {
            key = pwd.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public byte[] getKey() {
        return key;
    }

    public void setIsAutoOpenLock(byte isAutoOpenLock) {
        this.isAutoOpenLock = isAutoOpenLock;
    }

    public static class Data {
        public byte verifyResult;
        public short key;
    }
}
