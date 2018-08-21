package com.ut.data.dataSource.remote.bluetooth.cmd;

import com.ut.data.dataSource.remote.bluetooth.BleMsg;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class GetOpenLockRecord extends BleCmdBase<GetOpenLockRecord.Data>{

    private int readIndex;  //表示读取序号

    @Override
    public BleMsg build() {
        BleMsg bleMsg = new BleMsg();
        bleMsg.setEncrypt(false);
        bleMsg.setCode((byte) 0x0D);

        byte[] content = new byte[1];
        content[0] = (byte) readIndex;

        return bleMsg;
    }

    @Override
    Data parse(BleMsg msg) {
        ByteBuffer buffer = ByteBuffer.wrap(msg.getContent());
        Data data = new Data();
        data.recordNum = buffer.get();
        data.records = new ArrayList<>();

        for (int i=0; i<data.recordNum; i++) {
            Data.Record record = new Data.Record();
            record.keyId = buffer.get();
            buffer.get(record.openLockTime);
            data.records.add(record);
        }

        return data;
    }

    public void setReadIndex(int readIndex) {
        this.readIndex = readIndex;
    }

    public static class Data {

        public byte recordNum;
        public List<Record> records;

        public static class Record {
            byte keyId;
            byte[] openLockTime = new byte[4];
        }
    }
}
