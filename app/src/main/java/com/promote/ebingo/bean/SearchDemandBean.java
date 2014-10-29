package com.promote.ebingo.bean;

import java.io.Serializable;

/**
 * Created by ACER on 2014/9/2.
 *
 * 求购实体类。
 *
 */
public class SearchDemandBean extends SearchTypeBean implements Serializable{
    /**发布日期. **/
    private String date = null;
    /**求购简介. **/
    private String introduction = null;
    /** 瀏覽量. **/
    private String read_num = null;
    /** 求購數量. **/
    private String buy_num = null;
    private String verify_result;

    public String getVerify_result() {
        return verify_result;
    }

    public void setVerify_result(String verify_result) {
        this.verify_result = verify_result;
    }

    public String getBuy_num() {
        return buy_num;
    }

    public void setBuy_num(String buy_num) {
        this.buy_num = buy_num;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getRead_num() {
        return read_num;
    }

    public void setRead_num(String read_num) {
        this.read_num = read_num;
    }
}
