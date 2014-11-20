package com.promote.ebingoo.bean;

import java.io.Serializable;

/**
 * Created by ACER on 2014/11/14.
 * <p/>
 * 热门专题.
 */
public class HotActivitys implements Serializable {

    private HotActivity left;
    private HotActivity right;
    private HotActivity bottom;

    public HotActivity getLeft() {
        return left;
    }

    public HotActivity getRight() {
        return right;
    }

    public HotActivity getBottom() {
        return bottom;
    }
}
