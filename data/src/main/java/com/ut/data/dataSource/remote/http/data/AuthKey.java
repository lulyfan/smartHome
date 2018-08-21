package com.ut.data.dataSource.remote.http.data;

public class AuthKey {

//     "id": "1",
//     "keyId": "1",
//     "password": "0",
//     "type": "1",
//     "name": '指纹一',
//     'start': 1520411067823,
//     'end': 1520411267823,
//     "times": 255,
//     "authCode": "1",
//     "open": false,

    private String id;
    private String keyId;
    private String password;
    private String type;
    private String name;
    private long start;
    private long end;
    private int times;
    private String authCode;
    private boolean open;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
