package com.ut.data.dataSource.remote.http.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Shortcut {

//            "id": 134,
//            "userId": 94,
//            "hostId": 42,
//            "name": "",
//            "mainId": 30023,
//            "type": "device",
//            "createTime": 1532401006000,
//            "device": null,
//            "scene": null
    @PrimaryKey
    private int id;
    private int userId;
    private int hostId;
    private String name;
    private int mainId;
    private String type;
    private long createTime;

    @Ignore
    private DeviceInfo device;
    @Ignore
    private SceneInfo scene;

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

    public int getMainId() {
        return mainId;
    }

    public void setMainId(int mainId) {
        this.mainId = mainId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public DeviceInfo getDevice() {
        return device;
    }

    public void setDevice(DeviceInfo device) {
        this.device = device;
    }

    public SceneInfo getScene() {
        return scene;
    }

    public void setScene(SceneInfo scene) {
        this.scene = scene;
    }
}
