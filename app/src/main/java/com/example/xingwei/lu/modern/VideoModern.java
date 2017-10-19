package com.example.xingwei.lu.modern;

/**
 * 创建时间: 2017/10/18
 * 创建人: Administrator
 * 功能描述:
 */

public class VideoModern {
    private String fileName;
    private String duration;
    private String time;
    private String path;

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "VideoModern{" +
                "fileName='" + fileName + '\'' +
                ", duration='" + duration + '\'' +
                ", time='" + time + '\'' +
                ", path='" + path + '\'' +
                '}';
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
