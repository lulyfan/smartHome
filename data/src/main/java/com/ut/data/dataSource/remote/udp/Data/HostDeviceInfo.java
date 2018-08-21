package com.ut.data.dataSource.remote.udp.Data;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class HostDeviceInfo {

    private static final int BOOT = 0x00;
    private static final int APP = 0x01;
    private static final int TCP = 1;
    private static final int UDP = 2;

    public byte runEnvironment;             //主机运行环境
    public byte[] name = new byte[32];      //GB2312编码
    public int nameLength;                  //标识主机名字的实际占用长度
    public byte[] key = new byte[6];        //密钥 (加密用)
    public byte deviceType;                 //本机设备类型
    public byte protocol;                   //协议类型  1：TCP  2：UDP
    public byte[] iPAddress = new byte[4];
    public byte[] port = new byte[2];

    public byte[] mac = new byte[6];

    @Override
    public String toString() {

        String environment = "主机运行环境:" + (runEnvironment == BOOT ? "BOOT" : "APP");
        String hostName = null;
        try {
            hostName = "主机名:" + new String(name, 0, nameLength, "GB2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String skey = "密钥:" + Arrays.toString(key);
        String sDeviceType = "本机设备类型:" + deviceType;
        String sProtocol = "协议类型:" + (protocol == TCP ? "TCP" : "UDP");
        String sIP = "IP:" + Arrays.toString(iPAddress);
        String sPort = "Port:" + (((port[0] & 0xFF) * 256) + (port[1] & 0xFF));
        String sMac = "Mac:" + Arrays.toString(mac);
        return environment + "\n" + hostName + "\n" + skey + "\n" + sDeviceType + "\n" + sProtocol + "\n" + sIP + "\n" + sPort;
    }
}
