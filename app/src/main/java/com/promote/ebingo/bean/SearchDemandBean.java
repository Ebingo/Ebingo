package com.promote.ebingo.bean;

/**
 * Created by ACER on 2014/9/2.
 *
 * 求购实体类。
 *
 */
public class SearchDemandBean extends SearchTypeBean {
    /**发布日期**/
    private String date = null;
    /**求购简介**/
    private String Introduction = null;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIntroduction() {
        return Introduction;
    }

    public void setIntroduction(String introduction) {
        this.Introduction = introduction;
    }
}
