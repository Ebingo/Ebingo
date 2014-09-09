package com.promote.ebingo.bean;

/**
 * Created by acer on 2014/9/5.
 */
public class HotTag {
    private String name;
    private Integer id;
    transient boolean isSelect=false;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

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

    @Override
    public String toString() {
        return "HotTag{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
