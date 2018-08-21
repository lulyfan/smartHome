package com.ut.data.dataSource.remote.http.data;

public class RegionInfo {
//    hostID:1  ordinal 111 name 一楼 parentRegionNo： 0
    private int hostID;
    private int ordinal;
    private String name;
    private int parentRegionNo;

    @Override
    public String toString() {
        return "region name:" + name + " ordinal:" + ordinal;
    }

    public int getHostID() {
        return hostID;
    }

    public void setHostID(int hostID) {
        this.hostID = hostID;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParentRegionNo() {
        return parentRegionNo;
    }

    public void setParentRegionNo(int parentRegionNo) {
        this.parentRegionNo = parentRegionNo;
    }
}
