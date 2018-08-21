package com.ut.data.dataSource.remote.http.data;

public class LockOperateLog {

//                "id": 6,
//                "userId": 49,
//                "createTime": "2018-03-16 14:12:13",
//                "hostId": 31,
//                "address": "",
//                "statusCode": "3",
//                "value": "",
//                "description": "蓝牙开锁",
//                "time": "2018-03-16 15:27:57.0"

    private int id;
    private int userId;
    private String createTime;        //创建时间；
    private int hostId;             //主机Id；
    private String address;         //设备地址，
    private String statusCode;
    private String value;
    private String description;     //状态描述
    private String time;

    @Override
    public String toString() {
        return "LockOperateLog address:"+ address + " description:" + description;
    }

    public int getHostId() {
        return hostId;
    }

    public void setHostId(int hostId) {
        this.hostId = hostId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
