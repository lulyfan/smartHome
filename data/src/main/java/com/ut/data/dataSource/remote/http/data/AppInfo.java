package com.ut.data.dataSource.remote.http.data;

public class AppInfo {
//    version：版本号；
//    fileSize：文件大小；
//    fileUrl：获取路径；
//    description：版本描述；
//    extProps：扩展属性；
//    time：发布时间。
    private String version;
    private int fileSize;
    private String fileUrl;
    private String description;
    private String extProps;
    private long time;

    @Override
    public String toString() {
        return "version:" + version + " fileUrl:" + fileUrl;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExtProps() {
        return extProps;
    }

    public void setExtProps(String extProps) {
        this.extProps = extProps;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
