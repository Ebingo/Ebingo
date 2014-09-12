package com.promote.ebingo.bean;

/**
 * Created by ACER on 2014/9/12.
 */
public class CollectBean {

    /**
     * 搜藏id. *
     */
    private int id;
    /**
     * 信息标题. *
     */
    private String title;
    /**
     * 信息id. *
     */
    private int info_id;
    /**
     * 信息类型. *
     */
    private int type;
    /**
     * 加入收藏时间. *
     */
    private String time;
    /**
     * 价格. *
     */
    private String price;
    /**
     * 搜藏次数. *
     */
    private int collectTimes;
    /**
     * 图片路径. *
     */
    private String img;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getInfo_id() {
        return info_id;
    }

    public void setInfo_id(int info_id) {
        this.info_id = info_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getCollectTimes() {
        return collectTimes;
    }

    public void setCollectTimes(int collectTimes) {
        this.collectTimes = collectTimes;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
