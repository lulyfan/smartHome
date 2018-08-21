package com.ut.data.dataSource.remote.http.data.pushData;

public class HostStatePushData {


    /**
     * hostId : 1
     * status : 0
     * name : 主机名
     * forceLogin : true
     */

    private int hostId;
    private String status;
    private String name;
    private boolean forceLogin;

    public int getHostId() {
        return hostId;
    }

    public void setHostId(int hostId) {
        this.hostId = hostId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isForceLogin() {
        return forceLogin;
    }

    public void setForceLogin(boolean forceLogin) {
        this.forceLogin = forceLogin;
    }
}
