package com.ut.data.dataSource.remote.http.data;

import java.util.List;

public class SceneOperate {
    /**
     * name : 场景2
     * imageUrl : www.cwerfwef23.com
     * weeks :
     * time :
     * deviceData : [{"hostId":31,"address":"14_00-04-A3-E4-5C-B1&0&30&0_14","deviceTypeCode":32513,"deviceName":"五键轻触开关14_左继电器","status":[{"code":"status","value":"1","description":"灯开"}]},{"hostId":31,"address":"15_00-04-A3-E4-5C-B1&0&30&0_15","deviceTypeCode":32513,"deviceName":"五键轻触开关14_中继电器","status":[{"code":"status","value":"1","description":"灯开"}]}]
     */

    private String name;
    private String imageUrl;
    private String weeks;
    private String time;
    private List<DeviceDataBean> deviceData;

    private int id;

    private SceneInfo.WeekItem[] week;

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getWeeks() {
        return weeks;
    }

    public void setWeeks(String weeks) {
        this.weeks = weeks;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<DeviceDataBean> getDeviceData() {
        return deviceData;
    }

    public void setDeviceData(List<DeviceDataBean> deviceData) {
        this.deviceData = deviceData;
    }

    public SceneInfo.WeekItem[] getWeek() {
        return week;
    }

    public void setWeek(String weeks) {
        this.week = SceneInfo.parseWeeksToWeek(weeks);
    }

    public static class DeviceDataBean {
        /**
         * hostId : 31
         * address : 14_00-04-A3-E4-5C-B1&0&30&0_14
         * deviceTypeCode : 32513
         * deviceName : 五键轻触开关14_左继电器
         * status : [{"code":"status","value":"1","description":"灯开"}]
         */

        private int hostId;
        private String address;
        private int deviceTypeCode;
        private String deviceName;
        private List<StatusBean> status;

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

        public int getDeviceTypeCode() {
            return deviceTypeCode;
        }

        public void setDeviceTypeCode(int deviceTypeCode) {
            this.deviceTypeCode = deviceTypeCode;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public List<StatusBean> getStatus() {
            return status;
        }

        public void setStatus(List<StatusBean> status) {
            this.status = status;
        }

        public static class StatusBean {
            /**
             * code : status
             * value : 1
             * description : 灯开
             */

            private String code;
            private String value;
            private String description;

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }
        }
    }
}
