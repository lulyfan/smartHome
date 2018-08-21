package com.ut.data.dataSource.remote.udp.cmd;

import com.ut.data.dataSource.remote.udp.Msg;
import com.ut.data.dataSource.remote.udp.NetHelper;

import java.nio.ByteBuffer;

/**
 * Created by huangkaifan on 2018/6/5.
 * 写入或读取语言配置数据
 */

public class WRLanguageConfigData {

    short currentFrameNum;
    short totalFrameNum;
    int version;
    byte encrypt;
    int dataLength;                                           //语言配置数据长度
    int dataCheckSum;
    byte[] data;
    int writePos;                                             //已经写入的字节数
    int readPos;                                              //已经读取的字节数
    private static final int DATA_MAX_LENGTH_ONE_FRAME = 512;
    private static final int INFO_FRAME_DATA_LENGTH = 17;

    private NetHelper netHelper;

    public WRLanguageConfigData(NetHelper netHelper) {
        this.netHelper = netHelper;
    }

    public void write() {

        if (data == null) {
            return;
        }

        currentFrameNum = 0;
        totalFrameNum = (short) (dataLength / DATA_MAX_LENGTH_ONE_FRAME + 2);

        Msg msg = new Msg();
        msg.setLinkCmd((byte) Info.LinkCMD.MULTI_INFO_FRAME);
        msg.setAppCmd((byte) Info.AppCMD.WRITE_LANGUAGE_CONFIG);

        ByteBuffer dataBuffer = ByteBuffer.allocate(INFO_FRAME_DATA_LENGTH);
        dataBuffer.putShort(currentFrameNum);
        currentFrameNum++;
        dataBuffer.putShort(totalFrameNum);
        dataBuffer.putInt(version);
        dataBuffer.put(encrypt);
        dataBuffer.putInt(dataLength);

        for (byte b : data) {
            dataCheckSum += b;
        }
        dataBuffer.putInt(dataCheckSum);

        msg.setContent(dataBuffer.array());


        netHelper.asyncSend(msg, new NetHelper.ResponseListener() {
            @Override
            public void onACk(Msg sendMsg, Msg responseMsg) {
                writeDataFrame(sendMsg);
            }

            @Override
            public void onNAk(Msg sendMsg, int nakCode) {

            }

            @Override
            public void timeout(Msg sendMsg) {

            }
        });


    }

    private void writeDataFrame(Msg msg) {

        int remianWriteDataLength = dataLength - writePos;
        int writeSize = 0;                                         //本次要写入的字节数量
        if (remianWriteDataLength <= 0) {
            return;
        }

        ByteBuffer dataBuffer = null;
        if (remianWriteDataLength >= DATA_MAX_LENGTH_ONE_FRAME) {
            dataBuffer = ByteBuffer.allocate(DATA_MAX_LENGTH_ONE_FRAME);
            writeSize = DATA_MAX_LENGTH_ONE_FRAME;
        } else {
            dataBuffer = ByteBuffer.allocate(remianWriteDataLength);
            writeSize = remianWriteDataLength;
        }

        dataBuffer.putShort(currentFrameNum);
        dataBuffer.put(data, writePos, writeSize);
        msg.setContent(dataBuffer.array());

        final int finalWriteSize = writeSize;

        netHelper.asyncSend(msg, new NetHelper.ResponseListener() {
            @Override
            public void onACk(Msg sendMsg, Msg responseMsg) {
                writePos += finalWriteSize;
                currentFrameNum ++;

                writeDataFrame(sendMsg);
            }

            @Override
            public void onNAk(Msg sendMsg, int nakCode) {

            }

            @Override
            public void timeout(Msg sendMsg) {

            }
        });
    }

    public void read() {

        currentFrameNum = 0;

        Msg msg = new Msg();
        msg.setLinkCmd((byte) Info.LinkCMD.MULTI_INFO_FRAME);
        msg.setAppCmd((byte) Info.AppCMD.READ_LANGUAGE_CONFIG);

        ByteBuffer byteBuffer = ByteBuffer.allocate(2);
        byteBuffer.putShort(currentFrameNum);
        msg.setContent(byteBuffer.array());

        netHelper.asyncSend(msg, new NetHelper.ResponseListener() {
            @Override
            public void onACk(Msg sendMsg, Msg responseMsg) {
                currentFrameNum ++;

                ByteBuffer buffer = ByteBuffer.wrap(responseMsg.getContent());
                buffer.getShort();
                totalFrameNum = buffer.getShort();
                version = buffer.getInt();
                encrypt = buffer.get();
                dataLength = buffer.getInt();
                dataCheckSum = buffer.getInt();
                data = new byte[dataLength];
            }

            @Override
            public void onNAk(Msg sendMsg, int nakCode) {

            }

            @Override
            public void timeout(Msg sendMsg) {

            }
        });
    }

    private void readDataFrame(Msg msg) {

        if (currentFrameNum >= totalFrameNum) {
            int sum = 0;
            for (byte b : data) {
                sum += b;
            }

            if (sum != dataCheckSum) {
                System.out.println("和校验失败");
            }
            return;
        }

        final ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getContent());
        byteBuffer.putShort(currentFrameNum);

        netHelper.asyncSend(msg, new NetHelper.ResponseListener() {
            @Override
            public void onACk(Msg sendMsg, Msg responseMsg) {
                currentFrameNum ++;

                ByteBuffer buffer = ByteBuffer.wrap(responseMsg.getContent());
                buffer.getShort();
                buffer.get(data, readPos, buffer.capacity() - 2);
                readPos += buffer.capacity() - 2;

                readDataFrame(sendMsg);
            }

            @Override
            public void onNAk(Msg sendMsg, int nakCode) {

            }

            @Override
            public void timeout(Msg sendMsg) {

            }
        });
    }
}
