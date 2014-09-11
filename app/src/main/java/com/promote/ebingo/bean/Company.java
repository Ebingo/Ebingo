package com.promote.ebingo.bean;

import android.net.Uri;

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
    private String vipType;//会员类型
    private String isLock;
    private String email;
    private Uri imageUri;//公司图片在手机中的位置



    private static Company mCompany;

    private Company() {

    }

    public static Company getInstance() {
        if (mCompany == null) {
            mCompany = new Company();
//            mCompany.setCompanyId(10);
        }
        return mCompany;
    }

    public String getEmail() {
        return email;
    }
    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVipType() {
        return vipType;
    }

    public void setVipType(String vipType) {
        this.vipType = vipType;
    }

    public String getIsLock() {
        return isLock;
    }

    public void setIsLock(String isLock) {
        this.isLock = isLock;
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

    /**
     * 清空公司。
     */
    public void clearCompany() {

        companyId = -1;//公司Id
        image = null;  //公司图片url
        name = null;   //公司名
        head = null;   //负责人名称
        headPhone = null;  //负责人电话
        companyTel = null;  //公司电话
        region = null;    //公司地区
        website = null;   //公司网址
        vipType = null;    //会员类型
        isLock = null;
    }

}
