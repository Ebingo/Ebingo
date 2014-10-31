package com.promote.ebingoo.bean;

/**
 * Created by ACER on 2014/9/2.
 * <p/>
 * 搜索企业。
 */
public class SearchInterpriseBean extends SearchTypeBean {

    /**
     * 地区*
     */
    private String region;
    /**
     * 图片 *
     */
    private String image;
    /**
     * 主营业务 *
     */
    private String business;
    /**
     * 企业等级 *
     */
    private int rank;

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

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
