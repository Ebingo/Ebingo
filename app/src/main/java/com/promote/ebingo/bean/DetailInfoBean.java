package com.promote.ebingo.bean;

/**
 * Created by ACER on 2014/9/12.
 */
public class DetailInfoBean {


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
    /**
     * 发布公司 id *
     */
    private int company_id;
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
     * 供应还是求购 *
     */
    private int type;
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

    /**
     * 求购数量. *
     */
    private int buy_num;
    /** 公司名称. **/
    private String company_name;

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

    public int getCompany_id() {
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
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

    public int getBuy_num() {
        return buy_num;
    }

    public void setBuy_num(int buy_num) {
        this.buy_num = buy_num;
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
