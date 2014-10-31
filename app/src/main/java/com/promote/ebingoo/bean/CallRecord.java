package com.promote.ebingoo.bean;

/**
 * Created by acer on 2014/9/15.
 */
public class CallRecord {

    private Integer info_id;
    private String name;
    private String type;//关联信息类型1求2供
    private String call_time;//上次拨打时间
    private String phone_num;//被叫电话号码
    private String contacts;
    private Integer call_id;//主叫
    private Integer to_id;//被叫

    public Integer getTo_id() {
        return to_id;
    }

    public void setTo_id(Integer to_id) {
        this.to_id = to_id;
    }

    public Integer getCall_id() {
        return call_id;
    }

    public void setCall_id(Integer call_id) {
        this.call_id = call_id;
    }


    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public Integer getInfoId() {
        return info_id;
    }

    public void setInfoId(Integer infoId) {
        this.info_id = infoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCall_time() {
        return call_time;
    }

    public void setCall_time(String call_time) {
        this.call_time = call_time;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }
}
