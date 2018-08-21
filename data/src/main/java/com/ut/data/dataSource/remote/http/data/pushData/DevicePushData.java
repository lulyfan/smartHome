package com.ut.data.dataSource.remote.http.data.pushData;

public class DevicePushData {

    private int hostId;
    private String address;
    private int deviceTypeCode;
    private String status;

    @Override
    public String toString() {
        return "hostId:" + hostId + " deviceTypeCode:" + deviceTypeCode + " " + status;
    }

    public int getHostId() {
        return hostId;
    }

    public void setHostId(int hostId) {
        this.hostId = hostId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getDeviceTypeCode() {
        return deviceTypeCode;
    }

    public void setDeviceTypeCode(int deviceTypeCode) {
        this.deviceTypeCode = deviceTypeCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
