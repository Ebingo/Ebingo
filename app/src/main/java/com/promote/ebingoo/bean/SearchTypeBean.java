package com.promote.ebingoo.bean;

import java.io.Serializable;

/**
 * Created by ACER on 2014/9/2.
 *
 * 搜索內容基類。
 */
public class SearchTypeBean implements Serializable{

    protected int id;

    protected String name;

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
}
