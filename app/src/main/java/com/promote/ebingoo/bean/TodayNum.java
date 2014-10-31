package com.promote.ebingoo.bean;

import java.io.Serializable;

/**
 * Created by ACER on 2014/8/28.
 */
public class TodayNum implements Serializable {

    private int supply_num;
    private int demand_num;
    private int call_num;

    public int getSupply_num() {
        return supply_num;
    }

    public void setSupply_num(int supply_num) {
        this.supply_num = supply_num;
    }

    public int getDemand_num() {
        return demand_num;
    }

    public void setDemand_num(int demand_num) {
        this.demand_num = demand_num;
    }

    public int getCall_num() {
        return call_num;
    }

    public void setCall_num(int call_num) {
        this.call_num = call_num;
    }
}
