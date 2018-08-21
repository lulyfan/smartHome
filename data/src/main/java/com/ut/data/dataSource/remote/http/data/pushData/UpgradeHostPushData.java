package com.ut.data.dataSource.remote.http.data.pushData;

public class UpgradeHostPushData {

    /**
     * hostId : 1
     * success : true
     */

    private int hostId;
    private boolean success;

    public int getHostId() {
        return hostId;
    }

    public void setHostId(int hostId) {
        this.hostId = hostId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
