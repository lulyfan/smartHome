package com.ut.data.dataSource.remote.http.data;

public class LockConfig {
//    "id": 1, "hostId": 28, "type": "4", "auth": null, "name": "测试", "value": "1", "keyInNum": "0","canChangeAuth":true
    private int hostId;         //锁对应的主机的 Id；
    private String name;        //钥匙名称；
    private int value;          // value：钥匙的编码(0-39中的编码id)；
    private int auth;           // auth：锁的授权状态，0未授权1授权；
    private String type;        // type：钥匙类型;
    private long start;         // start:钥匙的开始授权时间;
    private long end;           // end:钥匙的结束授权时间。
    private int id;             //唯一标识

    private String keyInNum;
    private boolean canChangeAuth;

    @Override
    public String toString() {
        return "LockConfig hostId:" + hostId + " name:" + name;
    }

    public int getHostId() {
        return hostId;
    }

    public void setHostId(int hostId) {
        this.hostId = hostId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getAuth() {
        return auth;
    }

    public void setAuth(int auth) {
        this.auth = auth;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKeyInNum() {
        return keyInNum;
    }

    public void setKeyInNum(String keyInNum) {
        this.keyInNum = keyInNum;
    }

    public boolean isCanChangeAuth() {
        return canChangeAuth;
    }

    public void setCanChangeAuth(boolean canChangeAuth) {
        this.canChangeAuth = canChangeAuth;
    }
}
