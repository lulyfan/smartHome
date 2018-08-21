package com.ut.data.dataSource.remote.bluetooth;

import com.ut.data.util.CrcCheck;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class BleMsg {

    private static final byte[] HEAD = {(byte) 0xA5, 0x5A};
    private boolean isEncrypt;
    private short dataLength;    //报文长度为功能码和数据正文字节长度
    private boolean isResponseError;
    private byte code;           //功能码
    private byte[] content;
    private short checkCode;

    private static final int MIN_MSG_LENGTH = HEAD.length + 5;

    public byte[] encode() {
        countLength();
        ByteBuffer byteBuffer = ByteBuffer.allocate(HEAD.length + dataLength + 4);
        byteBuffer.put(HEAD);

        dataLength = isEncrypt ? (short) (dataLength | 0x1000) : dataLength;
        byteBuffer.putShort(dataLength);
        byteBuffer.put(code);
        if (content != null) {
            byteBuffer.put(content);
        }

        byte[] data = new byte[dataLength + 2];
        byteBuffer.position(HEAD.length);
        byteBuffer.get(data);

        CrcCheck check = new CrcCheck(16, 0x1021, false, 0, 0x00, 0);
        checkCode = (short) check.CountCheckAllCode(data);
        byteBuffer.putShort(checkCode);

        return byteBuffer.array();
    }

    public static BleMsg decode(byte[] data) {
        if (!check(data)) {
            System.out.println("数据校验失败");
            return null;
        }

        BleMsg msg = new BleMsg();

        try {
            ByteBuffer byteBuf = ByteBuffer.wrap(data);
            byteBuf.position(HEAD.length);                  //跳过消息头

            short tmp = byteBuf.getShort();
            msg.dataLength = (short) (tmp & 0x7FFF);
            msg.isEncrypt = (tmp & 0x8000) == 1;

            byte tmp2 = byteBuf.get();
            msg.code = (byte) (tmp2 & 0x7F);
            msg.isResponseError = (tmp2 & 0x80) == 1;

            msg.content = new byte[msg.dataLength - 1];
            byteBuf.get(msg.content);
            msg.checkCode = byteBuf.getShort();

            return msg;

        } catch (Exception e) {
            return null;
        }
    }

    private static boolean check(byte[] data) {
        if (data.length < MIN_MSG_LENGTH) {
            System.out.println("数据长度小于数据包最小长度");
            return false;
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        byte[] head = new byte[HEAD.length];
        byteBuffer.get(head);
        if (!Arrays.equals(head, HEAD)) {
            System.out.println("消息头错误");
            return false;
        }

        int bodyLength = data.length - HEAD.length - 2;
        byte[] body = new byte[bodyLength];
        byteBuffer.get(body);

        int crcCode = byteBuffer.getShort() & 0xFFFF;
        if (!crc(body, crcCode)) {
            System.out.println("crc检验失败");
            return false;
        }

        return true;
    }

    private void countLength() {
        int contentLength = content == null ? 0 : content.length;
        this.dataLength = (short) (contentLength + 1);
    }

    private static boolean crc(byte[] data, int crcCode) {

        CrcCheck check = new CrcCheck(16, 0x1021, false, 0, 0x00, 0);
        return crcCode == check.CountCheckAllCode(data);
    }

    public static byte[] getHEAD() {
        return HEAD;
    }

    public boolean isEncrypt() {
        return isEncrypt;
    }

    public void setEncrypt(boolean encrypt) {
        isEncrypt = encrypt;
    }

    public short getDataLength() {
        return dataLength;
    }

    public void setDataLength(short dataLength) {
        this.dataLength = dataLength;
    }

    public boolean isResponseError() {
        return isResponseError;
    }

    public void setResponseError(boolean responseError) {
        isResponseError = responseError;
    }

    public byte getCode() {
        return code;
    }

    public void setCode(byte code) {
        this.code = code;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public short getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(short checkCode) {
        this.checkCode = checkCode;
    }
}
