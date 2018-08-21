package com.ut.data.dataSource.remote.bluetooth.Data;

public class Status {

    public byte devNo;
    public byte type;
    public byte[] value;

    public Status(byte devNo, byte type) {
        this.devNo = devNo;
        this.type = type;

        value = new byte[getLength(type)];
    }

    //根据type得到设备状态value的数组长度
    private static int getLength(int type) {

        int length = 0;

        switch (type) {
            case 0x00:
            case 0x01:
            case 0x02:
            case 0x03:
            case 0x04:
            case 0x05:
            case 0x06:
            case 0x07:
                length = 1;
                break;

            case 0x08:
                length = 2;
                break;

            case 0x09:
                length = 3;
                break;

            case 0x0A:
                length = 4;
                break;

            case 0x0B:
                length = 6;
                break;

            case 0x0C:
                length = 8;
                break;

            case 0x0D:
                length = 10;
                break;

            case 0x0E:
                length = 12;
                break;

            case 0x0F:
                length = 16;
                break;

                default:
        }

        return length;
    }
}
