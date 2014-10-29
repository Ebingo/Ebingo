package com.promote.ebingo.bean;

import java.io.Serializable;

/**
 * Created by jch on 2014/8/28.
 * <p/>
 * 首頁熱點供應，熱點需求 共通类。
 */
public class HotBean implements Serializable {

    private String image = null;
    /**  **/
    private int id;
    /**
     * 这是大标题. *
     */
    private String title = null;
    /**
     * 这是小标题. *
     */
    private String sub_title = null;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSub_title() {
        return sub_title;
    }

    public void setSub_title(String sub_title) {
        this.sub_title = sub_title;
    }
}
