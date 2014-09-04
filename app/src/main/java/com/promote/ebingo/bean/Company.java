package com.promote.ebingo.bean;

/**
 * Created by acer on 2014/9/3.
 */
public class Company {
    private Integer companyId;//公司Id
    private String image;//公司图片url
    private String name;//公司名
    private String head;//负责人名称
    private String headPhone;//负责人电话
    private String companyTel;//公司电话
    private String region;//公司地区
    private String website; //公司网址

    private static Company mCompany;

    private Company(){

    }

    public static Company getInstance(){
        if (mCompany==null)mCompany=new Company();
        return mCompany;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
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

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getHeadPhone() {
        return headPhone;
    }

    public void setHeadPhone(String headPhone) {
        this.headPhone = headPhone;
    }

    public String getCompanyTel() {
        return companyTel;
    }

    public void setCompanyTel(String companyTel) {
        this.companyTel = companyTel;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
