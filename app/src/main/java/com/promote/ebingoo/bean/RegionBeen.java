package com.promote.ebingoo.bean;

import java.io.Serializable;

/**
 * Created by acer on 2014/9/9.
 */
public class RegionBeen implements Serializable {
    String name;
    Integer id;
    Integer father_id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFather_id() {
        return father_id;
    }

    public void setFather_id(Integer father_id) {
        this.father_id = father_id;
    }
}
