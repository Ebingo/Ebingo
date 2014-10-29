package com.promote.ebingo.bean;

import android.net.Uri;

import com.promote.ebingo.publish.VipType;

import java.io.Serializable;

/**
 * Created by acer on 2014/9/3.
 */
public class Company implements Serializable{
    private Integer company_id;//公司Id
    private String image;//公司图片url
    private String company_name;//公司名
    private String head;//负责人名称
    private String headPhone;//负责人电话
    private String company_tel;//公司电话
    private String address;//地址
    private String website; //公司网址
    private Integer province_id;//省份id
    private String province_name;//省份名
    private String city_name;//城市名
    private Integer city_id;//城市id
    private String viptype= VipType.VISITOR.code;//会员类型，默认是游客
    private String is_lock;
    private String email;
    private transient Uri imageUri;//公司图片在手机中的位置
    private CompanyVipInfo companyVipInfo;
    private String e_url;//e平台url
    private static Company mCompany = null;

    private Company() {

    }

    /**
     * 获取当前公司
     * @return
     */

    public static Company getInstance() {
        if (mCompany == null) {
            mCompany = new Company();
//            mCompany.setCompanyId(6);
        }
        return mCompany;
    }

    public String getE_url() {
        return e_url;
    }

    public void setE_url(String e_url) {
        this.e_url = e_url;
    }

    public CompanyVipInfo getVipInfo() {
        return companyVipInfo;
    }

    public void setVipInfo(CompanyVipInfo companyVipInfo) {
        this.companyVipInfo = companyVipInfo;
    }

    public String getCompany_tel() {
        return company_tel;
    }

    public void setCompany_tel(String company_tel) {
        this.company_tel = company_tel;
    }

    public String getProvince_name() {
        return province_name==null?"":province_name;
    }

    public void setProvince_name(String province_name) {
        this.province_name = province_name;
    }

    public String getCity_name() {
        return city_name==null?"":city_name;
    }

    /**
     * 获得完整地址 将 Province city address拼接起来
     * @return
     */
    public String getRegion(){
        return getProvince_name()+getCity_name()+getAddress();
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public Integer getProvince_id() {
        return province_id;
    }

    public void setProvince_id(Integer province_id) {
        this.province_id = province_id;
    }

    public Integer getCity_id() {
        return city_id;
    }

    public void setCity_id(Integer city_id) {
        this.city_id = city_id;
    }

    public static void loadInstance(Company company){
        mCompany=company;
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
        return viptype;
    }

    public void setVipType(String vipType) {
        this.viptype = vipType;
    }

    public String getIsLock() {
        return is_lock;
    }

    public void setIsLock(String isLock) {
        this.is_lock = isLock;
    }

    public Integer getCompanyId() {
        return company_id;
    }

    public void setCompanyId(Integer companyId) {
        this.company_id = companyId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return company_name;
    }

    public void setName(String name) {
        this.company_name = name;
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
        return company_tel;
    }

    public void setCompanyTel(String companyTel) {
        this.company_tel = companyTel;
    }

    public String getAddress() {
        return address==null?"":address;
    }

    public void setAddress(String address) {
        this.address = address;
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
    public static void clearInstance() {
        mCompany=null;
    }

    @Override
    public String toString() {
        return "Company{" +
                "company_id=" + company_id +
                ", image='" + image + '\'' +
                ", company_name='" + company_name + '\'' +
                ", head='" + head + '\'' +
                ", headPhone='" + headPhone + '\'' +
                ", company_tel='" + company_tel + '\'' +
                ", address='" + address + '\'' +
                ", website='" + website + '\'' +
                ", province_id=" + province_id +
                ", province_name='" + province_name + '\'' +
                ", city_name='" + city_name + '\'' +
                ", city_id=" + city_id +
                ", viptype='" + viptype + '\'' +
                ", is_lock='" + is_lock + '\'' +
                ", email='" + email + '\'' +
                ", imageUri=" + imageUri +
                ", companyVipInfo=" + companyVipInfo +
                '}';
    }
}
