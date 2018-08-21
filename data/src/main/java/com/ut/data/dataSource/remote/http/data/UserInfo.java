package com.ut.data.dataSource.remote.http.data;

public class UserInfo {
    private String account;
    private int host;         //绑定主机数
    private int region;       //区域数（跨主机）

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getHost() {
        return host;
    }

    public void setHost(int host) {
        this.host = host;
    }

    public int getRegion() {
        return region;
    }

    public void setRegion(int region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "account:" + account
                + " hostNum:" + host
                + " regionNum:" + region;
    }
}
