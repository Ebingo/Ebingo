package com.promote.ebingo.bean;

import java.io.Serializable;

/**
 * Created by acer on 2014/10/13.
 */
public class CompanyVipInfo implements Serializable {
    /**
     * 是否可以拨打求购电话
     */
    private boolean callDemand = true;
    private boolean callSupply = true;
    /**
     * 是否可以查看求购公司信息
     */
    private boolean canLookDemandCompany = true;
    /**
     * 发布求购信息
     */
    private boolean publish_demand_info;
    private String vip_type;//会员等级
    private int try_date;//体验剩余天数，只有为“体验会员”的时候才有此项
    private String vip_name; //对应的会员名称
    private int vip_date;//会员剩余天数
    private int supply_num;//已经发布的供应信息条数（控制体验会员发布信息条数）
    private int keywords_num;//关键词剩余个数
    private int tag_num;//标签剩余个数
    private int display_3d_num;//3d剩余个数
    private String wap_url;//E平台链接，如果有则显示，没有不显示

    public boolean callSupply() {
        return callSupply;
    }

    public void setCallSupply(boolean callSupply) {
        this.callSupply = callSupply;
    }

    public boolean callDemand() {
        return callDemand;
    }

    public void setCallDemand(boolean callDemand) {
        this.callDemand = callDemand;
    }

    public boolean canLookDemandCompany() {
        return canLookDemandCompany;
    }

    public void setCanLookDemandCompany(boolean canLookDemandCompany) {
        this.canLookDemandCompany = canLookDemandCompany;
    }

    public boolean publishDemandInfo() {
        return publish_demand_info;
    }

    public void setPublishDemandInfo(boolean publish_demand_info) {
        this.publish_demand_info = publish_demand_info;
    }

    public String getVip_type() {
        return vip_type;
    }

    public void setVip_type(String vip_type) {
        this.vip_type = vip_type;
    }

    public int getTry_date() {
        return try_date;
    }

    public void setTry_date(int try_date) {
        this.try_date = try_date;
    }

    public String getVip_name() {
        return vip_name;
    }

    public void setVip_name(String vip_name) {
        this.vip_name = vip_name;
    }

    public int getVip_date() {
        return vip_date;
    }

    public void setVip_date(int vip_date) {
        this.vip_date = vip_date;
    }

    public int getSupply_num() {
        return supply_num;
    }

    public void setSupply_num(int supply_num) {
        this.supply_num = supply_num;
    }

    public int getKeywords_num() {
        return keywords_num;
    }

    public void setKeywords_num(int keywords_num) {
        this.keywords_num = keywords_num;
    }

    public int getTag_num() {
        return tag_num;
    }

    public void setTag_num(int tag_num) {
        this.tag_num = tag_num;
    }

    public int getDisplay_3d_num() {
        return display_3d_num;
    }

    public void setDisplay_3d_num(int display_3d_num) {
        this.display_3d_num = display_3d_num;
    }

    public String getWap_url() {
        return wap_url;
    }

    public void setWap_url(String wap_url) {
        this.wap_url = wap_url;
    }

    @Override
    public String toString() {
        return "CompanyVipInfo{" +
                "callDemand=" + callDemand +
                ", callSupply=" + callSupply +
                ", canLookDemandCompany=" + canLookDemandCompany +
                ", publish_demand_info=" + publish_demand_info +
                ", vip_type='" + vip_type + '\'' +
                ", try_date=" + try_date +
                ", vip_name='" + vip_name + '\'' +
                ", vip_date=" + vip_date +
                ", supply_num=" + supply_num +
                ", keywords_num=" + keywords_num +
                ", tag_num=" + tag_num +
                ", display_3d_num=" + display_3d_num +
                ", wap_url='" + wap_url + '\'' +
                '}';
    }
}
