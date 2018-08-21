package com.ut.data.dataSource.remote.http.data;

public class DeviceOperateLog {

    private int hostId;          //主机id；
    private String address;      //设备地址；
    private String statusCode;   //锁的情况时锁，statusCode操作类型(pwd:密码开锁,card:刷卡开锁,fingerprint:指纹开锁)；非锁的情况时，statusCode为设备状态码；
    private int value;           //状态值；
    private String description;  //中文描述，用于显示；
    private long createTime;     //日志创建时间；


    @Override
    public String toString() {
        return "DeviceOperateLog hostId:" + hostId + " DeviceAddress:" + address;
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

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
