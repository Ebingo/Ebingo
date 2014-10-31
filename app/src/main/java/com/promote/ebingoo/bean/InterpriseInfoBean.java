package com.promote.ebingoo.bean;

import java.util.ArrayList;

/**
 * Created by ACER on 2014/9/18.
 */
public class InterpriseInfoBean {

    private String name = null;
    private String image = null;
    private String addr = null;
    private int vip_type = -1;
    private String website = null;
    private String tel = null;
    private String introduction = null;
    private String e_url = null;
    //    /**
//     * 联系人. *
//     */
//    private String contacts = null;

    public String getE_url() {
        return e_url;
    }

    public void setE_url(String e_url) {
        this.e_url = e_url;
    }

    private String mainRun = null;
    private ArrayList<CurrentSupplyBean> infoarray = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public int getViptype() {
        return vip_type;
    }

    public void setViptype(int viptype) {
        this.vip_type = viptype;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public ArrayList<CurrentSupplyBean> getInfoarray() {
        return infoarray;
    }

    public void setInfoarray(ArrayList<CurrentSupplyBean> infoarray) {
        this.infoarray = infoarray;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getMainRun() {
        return mainRun;
    }

    public void setMainRun(String mainRun) {
        this.mainRun = mainRun;
    }
}
