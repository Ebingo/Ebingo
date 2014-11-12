package com.promote.ebingoo.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by jch on 2014/11/10.
 * <p/>
 * 子分类。
 */
public class SubCategoryBean implements Serializable {
    /**
     * 父分类的id *
     */
    private int parent_id;

    Integer id;
    String name;
    String image;

    public ArrayList<SubCategoryBean> getSubCategorys() {
        return subCategorys;
    }

    public void setSubCategorys(ArrayList<SubCategoryBean> subCategorys) {
        this.subCategorys = subCategorys;
    }

    private ArrayList<SubCategoryBean> subCategorys = new ArrayList<SubCategoryBean>();


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

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

}
