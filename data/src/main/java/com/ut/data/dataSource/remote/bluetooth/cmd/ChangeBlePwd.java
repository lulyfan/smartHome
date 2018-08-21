package com.ut.data.dataSource.remote.bluetooth.cmd;

import com.ut.data.dataSource.remote.bluetooth.BleMsg;

public class ChangeBlePwd extends BleCmdBase<ChangeBlePwd.Data>{

    private byte[] oldPwd;
    private byte[] newPwd;

    @Override
    public BleMsg build() {
        BleMsg bleMsg = new BleMsg();
        bleMsg.setEncrypt(false);
        bleMsg.setCode((byte) 0x05);

        byte[] content = new byte[12];
        System.arraycopy(oldPwd, 0, content, 0, 6);
        System.arraycopy(newPwd, 0, content, 6, 6);
        bleMsg.setContent(content);
        return bleMsg;
    }

    @Override
    Data parse(BleMsg msg) {
        Data data = new Data();
        data.result = msg.getContent()[0];
        return data;
    }

    public void setOldPwd(byte[] oldPwd) {
        this.oldPwd = oldPwd;
    }

    public void setNewPwd(byte[] newPwd) {
        this.newPwd = newPwd;
    }

    public static class Data {
        public byte result;

        public boolean isSuccess() {
            return result == 1;
        }
    }
}
