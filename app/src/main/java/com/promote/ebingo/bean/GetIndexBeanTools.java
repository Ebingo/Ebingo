package com.promote.ebingo.bean;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ACER on 2014/8/28.
 */
public class GetIndexBeanTools {

    private GetIndexBean response;


    public static GetIndexBean getIndexBeanJson(String json) {

//        Type type = new TypeToken<GetIndexBeanTools>(){}.getType();
        return ((GetIndexBeanTools) (new Gson()).fromJson(json, GetIndexBeanTools.class)).getIndexBean();

    }

    public GetIndexBean getIndexBean() {
        return response;
    }


    public class GetIndexBean implements Serializable {

        /**
         * 公告条. *
         */

        private ArrayList<Adv> ads = null;

        private TodayNum today_num = null;

        private ArrayList<HotCategory> hot_category = null;

        private ArrayList<HotBean> hot_supply = null;

        private ArrayList<HotBean> hot_demand = null;

        private String version = null;

        private String news = null;


        public ArrayList<Adv> getAds() {
            return ads;
        }

        public void setAds(ArrayList<Adv> ads) {
            this.ads = ads;
        }

        public TodayNum getToday_num() {
            return today_num;
        }

        public void setToday_num(TodayNum today_num) {
            this.today_num = today_num;
        }

        public ArrayList<HotCategory> getHot_category() {
            return hot_category;
        }

        public void setHot_category(ArrayList<HotCategory> hot_category) {
            this.hot_category = hot_category;
        }

        public ArrayList<HotBean> getHot_supply() {
            return hot_supply;
        }

        public void setHot_supply(ArrayList<HotBean> hot_supply) {
            this.hot_supply = hot_supply;
        }

        public ArrayList<HotBean> getHot_demand() {
            return hot_demand;
        }

        public void setHot_demand(ArrayList<HotBean> hot_demand) {
            this.hot_demand = hot_demand;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getNews() {
            return news;
        }

        public void setNews(String news) {
            this.news = news;
        }
    }
}
