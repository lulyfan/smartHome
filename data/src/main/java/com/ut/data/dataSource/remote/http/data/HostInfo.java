package com.ut.data.dataSource.remote.http.data;

public class HostInfo {

//            "id": 43,
//            "name": "zsy智能3U主机",
//            "mac": "00-04-A3-E4-63-1E",
//            "type": "host",
//            "bootVersion": "",
//            "configVersion": "1073936",
//            "dataVersion": "",
//            "serverVersion": "",
//            "qrcode": "00-04-A3-E4-63-1E",
//            "createTime": 1527737986000,
//            "status": "0",
//            "isCurrentHost": false

    private int id;
    private String name;
    private String mac;
    private String type;
    private String bootVersion;
    private String configVersion;
    private String dataVersion;
    private String serverVersion;
    private String qrcode;
    private long createTime;
    private String status;
    private boolean isCurrentHost;

    @Override
    public String toString() {
        return "name:" + name + " mac:" + mac;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBootVersion() {
        return bootVersion;
    }

    public void setBootVersion(String bootVersion) {
        this.bootVersion = bootVersion;
    }

    public String getConfigVersion() {
        return configVersion;
    }

    public void setConfigVersion(String configVersion) {
        this.configVersion = configVersion;
    }

    public String getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(String dataVersion) {
        this.dataVersion = dataVersion;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isCurrentHost() {
        return isCurrentHost;
    }

    public void setCurrentHost(boolean currentHost) {
        isCurrentHost = currentHost;
    }
}
