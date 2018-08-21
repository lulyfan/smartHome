package com.ut.data.dataSource.remote.http.data.pushData.status;

public class LightStatus {
    private int status;
    public static final int OPEN = 1;
    public static final int CLOSE = 0;

    @Override
    public String toString() {
        return "LightStatus:" + (status == OPEN ? "open" : "close");
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
