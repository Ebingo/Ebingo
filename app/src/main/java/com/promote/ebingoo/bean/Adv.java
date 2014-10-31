package com.promote.ebingoo.bean;

import java.io.Serializable;

/**
 * Created by jch on 2014/8/28.
 * <p/>
 * 首页广告图片.
 */
public class Adv implements Serializable {

    /**  **/
    private String src = null;

    /**
     * 1.产品详情页 2.企业详情页 3 企业详情页 4.单独 web 页. *
     */
    private int type;
    /**
     * 如果为纯数字则表示为产品 id、者企业 id、分类 id。如果为链接则为 web 外部链接 *
     */
    private String content;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


}
