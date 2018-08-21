package com.ut.data.dataSource.remote.http.data.pushData;

public class SearchHostData {


    /**
     * code : 102
     * data : {"mac":"00-04-A3-E4-62-EA","name":"智能控制主机","status":{"status":0},"id":"","ip":"192.168.1.101","password":"","isCurrentHost":0,"port":"5001"}
     */

    private int code;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * mac : 00-04-A3-E4-62-EA
         * name : 智能控制主机
         * status : {"status":0}
         * id :
         * ip : 192.168.1.101
         * password :
         * isCurrentHost : 0
         * port : 5001
         */

        private String mac;
        private String name;
        private StatusBean status;
        private String id;
        private String ip;
        private String password;
        private int isCurrentHost;
        private String port;

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public StatusBean getStatus() {
            return status;
        }

        public void setStatus(StatusBean status) {
            this.status = status;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getIsCurrentHost() {
            return isCurrentHost;
        }

        public void setIsCurrentHost(int isCurrentHost) {
            this.isCurrentHost = isCurrentHost;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public static class StatusBean {
            /**
             * status : 0
             */

            private int status;

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }
        }
    }
}
