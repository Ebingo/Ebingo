package com.promote.ebingo.bean;

import java.io.Serializable;

/**
 * Created by acer on 2014/9/2.
 * 分类实例。
 */


public class CategoryBeen implements Serializable {
    Integer id;
    String name;
    String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return "CategoryBeen{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                '}';
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

