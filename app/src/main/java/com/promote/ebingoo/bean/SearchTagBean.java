package com.promote.ebingoo.bean;

/**
 * Created by acer on 2014/9/18.
 */
public class SearchTagBean extends SearchTypeBean {
    private String create_time;
    private int is_read;
    private String title;

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

    public int getIs_read() {
        return is_read;
    }

    public void setIs_read(int is_read) {
        this.is_read = is_read;
    }

    @Override
    public String toString() {
        return "SearchTagBean{" +
                "title='" + title + '\'' +
                "create_time='" + create_time + '\'' +
                ", is_read=" + is_read +
                '}';
    }
}
