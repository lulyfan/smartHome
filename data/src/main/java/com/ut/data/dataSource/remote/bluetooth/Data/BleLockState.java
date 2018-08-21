package com.ut.data.dataSource.remote.bluetooth.Data;

import java.util.Date;

public class BleLockState {

    private String time;
    private String status;
    private String elect;
    private int alarm = -1;

    public BleLockState(String status, String elect, int alarm) {
        this.status = status;
        this.elect = elect;
        this.alarm = alarm;
        time = new Date().toString();
    }

    public BleLockState() {
        time = new Date().toString();
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getElect() {
        return elect;
    }

    public void setElect(String elect) {
        this.elect = elect;
    }

    public int getAlarm() {
        return alarm;
    }

    public void setAlarm(int alarm) {
        this.alarm = alarm;
    }

    @Override
    public String toString() {
        return "elect:" + elect + " status:" + status + " alarm:" + alarm;
    }
}
