package com.example.xingwei.lu.modern;

import org.litepal.crud.DataSupport;

/**
 * 创建时间: 2017/11/8
 * 创建人: Administrator
 * 功能描述:
 */

public class PdfModule extends DataSupport {
    private String path;
    private String time;

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
}
