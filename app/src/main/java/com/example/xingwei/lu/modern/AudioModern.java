package com.example.xingwei.lu.modern;

import android.support.annotation.NonNull;

/**
 * 创建时间: 2017/11/27
 * 创建人: Administrator
 * 功能描述:
 */

public class AudioModern implements Comparable<AudioModern> {
    private String fileName;
    private String duration = "";
    private String time;
    private String path;
    private long lastModifyTime;

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    @Override
    public int compareTo(@NonNull AudioModern audioModern) {
        if (this.lastModifyTime > audioModern.getLastModifyTime()) {
            return -1;
        } else if (this.lastModifyTime < audioModern.getLastModifyTime()) {
            return 1;
        } else
            return 0;
    }
}
