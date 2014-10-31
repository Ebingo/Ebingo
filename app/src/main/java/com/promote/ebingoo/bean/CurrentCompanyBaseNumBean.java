package com.promote.ebingoo.bean;

/**
 * Created by ACER on 2014/9/10.
 */
public class CurrentCompanyBaseNumBean {
    /**
     * 求购条数 *
     */
    private int demand;
    /**
     * 供应条数*
     */
    private int supply;
    /**
     * 收藏条数*
     */
    private int wishlist;
    /**
     * 新消息提醒条数*
     */
    private int news;

    public int getNews() {
        return news;
    }

    public void setNews(int news) {
        this.news = news;
    }

    public int getDemand() {
        return demand;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }

    public int getSupply() {
        return supply;
    }

    public void setSupply(int supply) {
        this.supply = supply;
    }

    public int getWishlist() {
        return wishlist;
    }

    public void setWishlist(int wishlist) {
        this.wishlist = wishlist;
    }


}
