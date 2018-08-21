package com.ut.data.dataSource.remote.bluetooth;

import android.util.Log;

import com.ut.data.dataSource.remote.bluetooth.jobluetooth.BluetoothLeService;
import com.ut.data.dataSource.remote.udp.ClientBase;
import com.ut.data.util.HexUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import retrofit2.http.HEAD;

public class BleClient extends ClientBase{

    private BluetoothLeService bluetoothLeService;
    private boolean isConnect;

    private ByteBuffer buffer = ByteBuffer.allocate(1024);

    private static final int STATE_HEAD = 0;    //正在解析头
    private static final int STATE_BODY = 1;    //正在解析报文内容（除了头外的其余部分）

    private int state;
    private int msgLength;
    private long sendTime;

    public BleClient(BluetoothLeService bluetoothLeService) {
        this.bluetoothLeService = bluetoothLeService;
    }

    @Override
    public void send(byte[] msg) {

        String str = "";
        for (byte aData : msg) {
            int temp = aData & 0xFF;
            str += Integer.toHexString(temp) + " ";
        }

        Log.i("bluetoothLock", "write: " + str);
        long gapTime = System.currentTimeMillis() - sendTime;
        Log.i("gapTime", gapTime + "");
        sendTime = System.currentTimeMillis();

        bluetoothLeService.wirte(msg);
    }

    @Override
    public void broadcastSend(byte[] msg) {

    }

    @Override
    public void open() {

    }

    public void reset() {
        state = STATE_HEAD;
        buffer.clear();
    }

    @Override
    public boolean isConnect() {
        return isConnect;
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }

    public void receiveData(final byte[] data) {
        if (handleExecutor.isShutdown()) {
            return;
        }

        Log.i("data", HexUtils.getFormatHex(data));

        buffer.put(data);

        Log.i("data", "position:" + buffer.position() + " state:" + state + " msgLength:" + msgLength);

        switch (state) {
            case STATE_HEAD:
                if (buffer.position() < 4) {
                    return;
                }

                int dataLength = buffer.getShort(2) & 0x7FFF;   //获取报文长度
                msgLength = 2 + 2 + dataLength + 2;
                state = STATE_BODY;
                Log.i("data", "receive a head");
                break;

            case STATE_BODY:
                if (buffer.position() < msgLength) {
                    return;
                }

                buffer.flip();
                final byte[] msg = new byte[msgLength];
                buffer.get(msg);
                Log.i("data", "receive a msg");

                String str = "";
                for (byte aData : msg) {
                    int temp = aData & 0xFF;
                    str += Integer.toHexString(temp) + " ";
                }
                Log.i("bluetoothLock", "ACTION_GATT_DATARECEIVED: " + str);

                handleExecutor.execute(new Runnable() {

                    @Override
                    public void run() {
                        if (receiveListener != null) {
                            receiveListener.onReceive(msg);
                        }
                    }
                });
                buffer.compact();
                state = STATE_HEAD;

                break;

                default:
        }
    }
}
