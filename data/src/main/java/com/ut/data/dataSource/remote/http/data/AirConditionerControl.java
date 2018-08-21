package com.ut.data.dataSource.remote.http.data;

public class AirConditionerControl {
    private int tem = -1;                    //温度，控制范围16-32。不控制传-1
    private int status = -1;                 //空调开关机状态，0-关闭1-开启 。不控制传-1
    private int mode = -1;                   //空调运行模式，0-制冷1-制热2-除湿3-送风4-自动 。不控制传-1
    private int wind = -1;                   //空调风力，0-超低1-低2-中3-高4-超高5-自动 。不控制传-1
    private int swingLR = -1;                //左右扫风，0-关闭1-开启 。不控制传-1
    private int swingUD = -1;                //上下扫风，0-关闭1-开启 。不控制传-1
    private int sleep = -1;                  //睡眠模式,0-关闭1-开启 。不控制传-1

    public int getTem() {
        return tem;
    }

    public void setTem(int tem) {
        this.tem = tem;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getWind() {
        return wind;
    }

    public void setWind(int wind) {
        this.wind = wind;
    }

    public int getSwingLR() {
        return swingLR;
    }

    public void setSwingLR(int swingLR) {
        this.swingLR = swingLR;
    }

    public int getSwingUD() {
        return swingUD;
    }

    public void setSwingUD(int swingUD) {
        this.swingUD = swingUD;
    }

    public int getSleep() {
        return sleep;
    }

    public void setSleep(int sleep) {
        this.sleep = sleep;
    }
}
