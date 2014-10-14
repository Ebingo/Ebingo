package com.promote.ebingo.bean;

import java.io.Serializable;

/**
 * Created by ACER on 2014/9/12.
 */
public class DetailInfoBean implements Serializable {
    /**
     * 从哪个平台过来
     * 值：android,ios,pc
     */
    private String from;
    private String category_name;
    /**
     * 信息 id *
     */
    private int info_id;

    /**
     * 标题 *
     */
    private String title;
    private String description;
    /**
     * 创建时间 *
     */
    private String create_time;
    private String unit;
    private Integer collect_id;
    /**
     * 发布公司 id *
     */
    private Integer company_id;
    /**
     * 查看次数 *
     */
    private int read_num;
    /**
     * 分类 id *
     */
    private int category_id;
    /**
     * 联系人姓名 *
     */
    private String contacts;
    /**
     * 联系人手机号 *
     */
    private String phone_num;
    /**
     * 所在地 *
     */
    private String region;
    /**
     * 供应还是求购 1 求购 2 供应
     */
    private String type;
    /**
     * 图片地址 *
     */
    private String image;
    /**
     * 3d 加载地址 *
     */
    private String url_3d;
    /**
     * 价格 *
     */
    private String price;
    /**
     * 起售标准 *
     */
    private String min_sell_num;
    /**
     * 是否加入收藏 *
     */
    private int inwishlist;

    private int wishlist_id;
    /**
     * 求购数量. *
     */
    private String buy_num;
    /**
     * 公司名称. *
     */
    private String company_name;
    /**
     * 审核时间
     */
    private String verify_time;
    /**
     * 审核结果
     */
    private String verify_result;
    /**
     * 审核原因，失败原因？
     */
    private String verify_reason;
    /**
     * vip等级类型。
     */
    private int vip_type = -3;


    public int getWishlist_id() {
        return wishlist_id;
    }

    public void setWishlist_id(int wishlist_id) {
        this.wishlist_id = wishlist_id;
    }

    public String getVerify_time() {
        return verify_time;
    }

    public void setVerify_time(String verify_time) {
        this.verify_time = verify_time;
    }

    public String getVerify_result() {
        return verify_result;
    }

    public void setVerify_result(String verify_result) {
        this.verify_result = verify_result;
    }

    public String getVerify_reason() {
        return verify_reason;
    }

    public void setVerify_reason(String verify_reason) {
        this.verify_reason = verify_reason;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public void setCompany_id(Integer company_id) {
        this.company_id = company_id;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getCollect_id() {
        return collect_id;
    }

    public void setCollect_id(Integer collect_id) {
        this.collect_id = collect_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }


    public int getInfo_id() {
        return info_id;
    }

    public void setInfo_id(int info_id) {
        this.info_id = info_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public Integer getCompany_id() {
        return company_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public int getRead_num() {
        return read_num;
    }

    public void setRead_num(int read_num) {
        this.read_num = read_num;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl_3d() {
        return url_3d;
    }

    public void setUrl_3d(String url_3d) {
        this.url_3d = url_3d;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMin_sell_num() {
        return min_sell_num;
    }

    public void setMin_sell_num(String min_sell_num) {
        this.min_sell_num = min_sell_num;
    }

    public int getInwishlist() {
        return inwishlist;
    }

    public void setInwishlist(int inwishlist) {
        this.inwishlist = inwishlist;
    }

    public String getBuy_num() {
        return buy_num;
    }

    public void setBuy_num(String buy_num) {
        this.buy_num = buy_num;
    }

    /**
     * 设置当前vipType.
     *
     * @param vip_type
     */
    public void setVip_type(int vip_type) {
        this.vip_type = vip_type;
    }

    /**
     * 获得vipType。
     *
     * @return
     */
    public int getVip_type() {
        return this.vip_type;
    }

    @Override
    public String toString() {
        return "DetailInfoBean{" +
                "info_id=" + info_id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", create_time='" + create_time + '\'' +
                ", company_id=" + company_id +
                ", read_num=" + read_num +
                ", category_id=" + category_id +
                ", contacts='" + contacts + '\'' +
                ", phone_num='" + phone_num + '\'' +
                ", region='" + region + '\'' +
                ", type=" + type +
                ", image='" + image + '\'' +
                ", url_3d='" + url_3d + '\'' +
                ", price='" + price + '\'' +
                ", min_sell_num='" + min_sell_num + '\'' +
                ", inwishlist=" + inwishlist +
                ", buy_num=" + buy_num +
                ", company_name='" + company_name + '\'' +
                '}';
    }

}
