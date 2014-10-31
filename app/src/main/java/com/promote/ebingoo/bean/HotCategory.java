package com.promote.ebingoo.bean;

import java.io.Serializable;

/**
 * Created by jch on 2014/8/28.
 */
public class HotCategory implements Serializable {

    private int id;

    private String image = null;

    private String name = null;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
