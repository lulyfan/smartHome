package com.ut.data.dataSource.remote.http.data;

public class DeviceTypeStatus {
//            "type": 32513,
//            "value": "1",
//            "count": 0,
//            "name": "灯"

    private String type;
    private String value;
    private int count;
    private String name;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
