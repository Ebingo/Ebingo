package com.promote.ebingoo.bean;

/**
 * Created by ACER on 2014/9/10.
 */
public class ResponseBaseBean<T> {

    private int code;

    private T data;

    /**
     * 错误信息. *
     */
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {

        return code + data.toString();
    }
}
