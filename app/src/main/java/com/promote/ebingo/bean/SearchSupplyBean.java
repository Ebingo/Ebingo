package com.promote.ebingo.bean;

/**
 * Created by ACER on 2014/9/2.
 */
public class SearchSupplyBean extends SearchTypeBean {
    /**公司名称**/
    private String company;
    /**地区**/
    private String region;
    /**图片**/
    private String image;
    /** 主营业务**/
    private String date;
    /**起购标准**/
    private int min_supply_num;
    /**查看次数**/
    private int read_num;
    /**企业等级**/
    private String company_rank;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getMin_supply_num() {
        return min_supply_num;
    }

    public void setMin_supply_num(int min_supply_num) {
        this.min_supply_num = min_supply_num;
    }

    public int getRead_num() {
        return read_num;
    }

    public void setRead_num(int read_num) {
        this.read_num = read_num;
    }

    public String getCompany_rank() {
        return company_rank;
    }

    public void setCompany_rank(String company_rank) {
        this.company_rank = company_rank;
    }
}
