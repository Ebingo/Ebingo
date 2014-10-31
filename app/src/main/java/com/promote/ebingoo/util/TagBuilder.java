package com.promote.ebingoo.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.promote.ebingoo.R;

/**
 * 用来创建标签
 * Created by acer on 2014/9/17.
 */
public class TagBuilder {
    private Context context;
    private View tagView;

    public View create() {
        View v= LayoutInflater.from(context).inflate(R.layout.call_record_item,null);
        return tagView;
    }

    public TagBuilder setNumber() {
        return this;
    }

    public TagBuilder setText() {
        return this;
    }

    public TagBuilder set() {
        return this;
    }
}
