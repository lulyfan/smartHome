package com.ut.data.dataSource.remote.udp;

import com.ut.data.dataSource.remote.udp.cmd.Info;
import com.ut.data.util.CrcCheck;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Msg {

    private static final byte[] HEAD = {(byte) 0xEB, (byte) 0x90, (byte) 0xEB, (byte) 0x90};
    private byte linkCmd;                              //链路层命令
    private short dataLength;                          //扩展数据+正文数据长度
    private byte appCmd;                               //应用层命令

    //扩展数据
    private static final int extendDataLength = 24;
    private byte encrypt = 0;
    private byte localDeviceType = Info.LocalDeviceType.ANDROID_PHONE;
    private byte[] clientIdentify = {0, 0, 0, 0, 0, 0};
    private byte[] communicationPW = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};         //通信口令
    private byte[] srcMac = new byte[6];
    private byte[] destMac = new byte[6];

    private byte[] content;

    private short checkCode;

    private static final int MIN_MSG_LENGTH = HEAD.length + extendDataLength + 6;

    public byte[] encode() {
        countLength();
        ByteBuffer byteBuffer = ByteBuffer.allocate(HEAD.length + dataLength + 6);
        byteBuffer.put(HEAD);
        byteBuffer.put(linkCmd);
        byteBuffer.putShort(dataLength);
        byteBuffer.put(appCmd);
        byteBuffer.put(encrypt);
        byteBuffer.put(localDeviceType);
        byteBuffer.put(clientIdentify);
        byteBuffer.put(communicationPW);
        byteBuffer.put(srcMac);
        byteBuffer.put(destMac);

        if (content != null) {
            byteBuffer.put(content);
        }

        byte[] data = new byte[dataLength + 4];
        byteBuffer.position(HEAD.length);
        byteBuffer.get(data);

        CrcCheck check = new CrcCheck(16, 0x1021, false, 0, 0x00, 0);
        checkCode = (short) check.CountCheckAllCode(data);
        byteBuffer.putShort(checkCode);

        return byteBuffer.array();
    }

    public static Msg decode(byte[] data) {

        if (!check(data)) {
            System.out.println("数据校验失败");
            return null;
        }

        Msg msg = new Msg();

        try {
            ByteBuffer byteBuf = ByteBuffer.wrap(data);
            byteBuf.position(HEAD.length);                  //跳过消息头
            msg.linkCmd = byteBuf.get();
            msg.dataLength = byteBuf.getShort();
            msg.appCmd = byteBuf.get();
            msg.encrypt = byteBuf.get();
            msg.localDeviceType = byteBuf.get();
            byteBuf.get(msg.clientIdentify);
            byteBuf.get(msg.communicationPW);
            byteBuf.get(msg.srcMac);
            byteBuf.get(msg.destMac);
            msg.content = new byte[msg.dataLength - extendDataLength];
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

    private static boolean crc(byte[] data, int crcCode) {

        CrcCheck check = new CrcCheck(16, 0x1021, false, 0, 0x00, 0);
        return crcCode == check.CountCheckAllCode(data);
    }

    public void printf() {

        System.out.println("head:" + Arrays.toString(HEAD));
        System.out.println("linkCmd:" + linkCmd);
        System.out.println("length:" + dataLength);
        System.out.println("appCmd:" + appCmd);
        System.out.println("encrypt:" + encrypt);
        System.out.println("localDeviceType:" + localDeviceType);
        System.out.println("clientIdentify:" + Arrays.toString(clientIdentify));
        System.out.println("communicationPW:" + Arrays.toString(communicationPW));
        System.out.println("srcMac:" + Arrays.toString(srcMac));
        System.out.println("destMac:" + Arrays.toString(destMac));
        System.out.println("content:" + Arrays.toString(content));
        System.out.println("checkCode:" + checkCode);
    }

    public static Msg createMsg() {
        return createMsg((byte) 12);
    }

    public static Msg createMsg(byte appCmd) {
        Msg msg = new Msg();
        msg.linkCmd = 11;
        msg.appCmd = appCmd;
        msg.encrypt = 0;
        msg.localDeviceType = 14;

        byte[] clientIdentify = {15, 16, 17, 18, 19, 20};
        msg.clientIdentify = clientIdentify;
        msg.checkCode = 22;

        byte[] communicationPW = {23, 24, 25, 26};
        msg.communicationPW = communicationPW;

        byte[] srcMac = {1, 2, 3, 4, 5, 6};
        byte[] destMac = {6, 5, 4, 3, 2, 1};
        msg.srcMac = srcMac;
        msg.destMac = destMac;

        byte[] content = {30, 31, 32};
        msg.content = content;
        msg.dataLength = (short) (extendDataLength + msg.content.length);
        return msg;
    }

    public byte getLinkCmd() {
        return linkCmd;
    }

    public void setLinkCmd(byte linkCmd) {
        this.linkCmd = linkCmd;
    }

    public short getLength() {
        return dataLength;
    }

    private void countLength() {
        int contentLength = content == null ? 0 : content.length;
        this.dataLength = (short) (extendDataLength + contentLength);
    }

    public byte getAppCmd() {
        return appCmd;
    }

    public void setAppCmd(byte appCmd) {
        this.appCmd = appCmd;
    }

    public byte getEncryptAndversion() {
        return encrypt;
    }

    public void setEncryptAndversion(byte encryptAndversion) {
        this.encrypt = encryptAndversion;
    }

    public byte getLocalDeviceType() {
        return localDeviceType;
    }

    public void setLocalDeviceType(byte localDeviceType) {
        this.localDeviceType = localDeviceType;
    }

    public byte[] getCloudDeviceIdentify() {
        return clientIdentify;
    }

    public void setCloudDeviceIdentify(byte[] cloudDeviceIdentify) {
        this.clientIdentify = cloudDeviceIdentify;
    }

    public byte[] getSrcMac() {
        return srcMac;
    }

    public void setSrcMac(byte[] srcMac) {
        this.srcMac = srcMac;
    }

    public byte[] getDestMac() {
        return destMac;
    }

    public void setDestMac(byte[] destMac) {
        this.destMac = destMac;
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

    public static int getExtenddatalength() {
        return extendDataLength;
    }
}
