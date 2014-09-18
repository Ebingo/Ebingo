package com.promote.ebingo.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by ACER on 2014/9/18.
 */
public class ComanyNewsListBeanTools {

    public ArrayList<CompanyNewListBean> getResponse() {
        return response;
    }

    public void setResponse(ArrayList<CompanyNewListBean> response) {
        this.response = response;
    }

    public ArrayList<CompanyNewListBean> response = null;

    public static ArrayList<CompanyNewListBean> getCompanyNewsListBeans(String json) {

        Type type = new TypeToken<InterpriseInfoBeanTools>() {
        }.getType();

        return ((ComanyNewsListBeanTools) new Gson().fromJson(json, type)).getResponse();
    }


    public class CompanyNewListBean {

        private int id;
        /**
         * 标题*
         */
        private String title;
        /**
         * 发布时间*
         */
        private String create_time;
        /**
         * 描述*
         */
        private String description;


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
