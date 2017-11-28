package com.example.xingwei.lu.modern;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * 创建时间: 2017/11/28
 * 创建人: Administrator
 * 功能描述:
 */

public class PdfPathMoudle extends DataSupport {
    @Column(unique = true, nullable = false)
    private long id;
    private String path;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
