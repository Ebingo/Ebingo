package com.promote.ebingoo.bean;

import java.util.ArrayList;

/**
 * Created by ACER on 2014/11/12.
 */
public class HotKey {
    /**
     * 求购。
     */
    private ArrayList<String> demand;
    /**
     * 公司。
     */
    private ArrayList<String> company;
    /**
     * 供应。
     */
    private ArrayList<String> supply;

    public ArrayList<String> getDemand() {
        return demand;
    }

    public void setDemand(ArrayList<String> demand) {
        this.demand = demand;
    }

    public ArrayList<String> getCpmpany() {
        return company;
    }

    public void setCpmpany(ArrayList<String> cpmpany) {
        this.company = cpmpany;
    }

    public ArrayList<String> getSupply() {
        return supply;
    }

    public void setSupply(ArrayList<String> supply) {
        this.supply = supply;
    }
}
