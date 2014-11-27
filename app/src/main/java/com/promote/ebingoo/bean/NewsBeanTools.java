package com.promote.ebingoo.bean;

import com.google.gson.Gson;

/**
 * Created by ACER on 2014/9/20.
 */
public class NewsBeanTools {

    private NewsBean response;

    public static NewsBean getNews(String json) {
        return new Gson().fromJson(json, NewsBeanTools.class).getResponse();

    }

    public NewsBean getResponse() {
        return response;
    }

    public class NewsBean {

        private String url = null;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
