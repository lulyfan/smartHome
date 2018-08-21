package com.ut.data.dataSource.remote.http.data.pushData.status;

public class LockStatus {

    //0 关锁
    //1 开锁
    //2 反锁
    //elect:电池电量,0～4，表示0-4格电量

    private static final int CLOSE = 0;
    private static final int OPEN = 1;
    private static final int REVERSE = 2;

    private int status;
    private int elect;

    @Override
    public String toString() {
        return "LockStatus status:" + status + " elect:" + elect;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getElect() {
        return elect;
    }

    public void setElect(int elect) {
        this.elect = elect;
    }
}
