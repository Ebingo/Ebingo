package com.promote.ebingo;

import android.app.ListActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jch.lib.view.PullToRefreshView;
import com.promote.ebingo.center.ItemDelteDialog;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.LogCat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.jch.lib.view.PullToRefreshView.*;

/**
 * 用于展示列表的Activity，有一个默认的布局文件R.layout.activity_base_list，子类不需要设定布局文件。标题是在manifest中对应Activity的label值
 * zhuchao on 2014/9/18.
 */
public class BaseListActivity extends ListActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener, ItemDelteDialog.DeleteItemListener {
    public static final String ARG_REFRESH="refresh";
    /**
     * 分页的lastId
     */
    protected int lastId = 0;
    protected int pageSize = 20;
    /**
     * 弹出编辑框的位置
     */
    private int edit_position = -1;
    private ItemDelteDialog delteDialog;
    private PullToRefreshView mPullToRefreshView;
    /**
     * 缓存模块的名称
     */
    protected String mCacheName;
    /**
     * 缓存的数据
     */
    protected List mCache;
    /**
     * 上一次更新的时间
     */
    private Date lastRefreshTime = null;
    /**
     * 保存上次更新时间的文件名
     */
    private final String CACHE_DATE = "cache_date";

    private boolean isHeadRefreshing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_list);
        setTitle(getTitle());
        delteDialog = new ItemDelteDialog(this, this);
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.list_content);
        mPullToRefreshView.setFootViewVisibility(View.GONE);//先把FooterView隐藏，上来就显示很不美观
        setUpRefreshable(false);
        setDownRefreshable(false);

        lastRefreshTime = getLastRefreshTime(getLocalClassName());
        mPullToRefreshView.setLastUpdated(getString(R.string.last_update) + getDateString(lastRefreshTime));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_back_btn:
                finish();
                break;
        }
    }

    /**
     * 是否使用缓存，如果cacheName！=null 就使用缓存.
     * 使用缓存后，lastId等于列表的size,在Activity 进入onStop()时自动保存列表里的数据。
     *
     * @param cacheName 缓存文件的名称
     * @param data      要用来作为缓存数据的对象
     */
    public void enableCache(String cacheName, List data) {
        mCacheName = cacheName;
        mCache = data;
        if (mCache != null) {//读取缓存
            mCache.clear();
            List temp = (List) ContextUtil.read(mCacheName);
            if (temp != null) {
                mCache.addAll(temp);
                BaseAdapter adapter = getAdapter();
                if (adapter != null) adapter.notifyDataSetChanged();
                lastId = mCache.size();
            }

        }
    }

    protected BaseAdapter getAdapter() {
        return (BaseAdapter) getListAdapter();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //写入缓存
        LogCat.i("--->", "-->" + getLocalClassName() + " lastRefreshTime" + lastRefreshTime);
        if (mCacheName != null && mCache != null) {
            ContextUtil.saveCache(mCacheName, mCache);
            if (lastRefreshTime != null) {
                getSharedPreferences(CACHE_DATE, MODE_PRIVATE).edit().putString(getLocalClassName(), getDateString(lastRefreshTime)).commit();
                LogCat.i("---->", "name=" + getLocalClassName() + " date=" + getDateString(lastRefreshTime));
            }
        }
    }

    private String getDateString(Date date) {
        return DateFormat.format("MM-dd hh:mm:ss", date).toString();
    }

    /**
     * 从配置文件中读取 上次更新的时间
     *
     * @return
     */
    private Date getLastRefreshTime(String name) {
        try {
            String date = getSharedPreferences(CACHE_DATE, MODE_PRIVATE).getString(name, null);
            LogCat.i("---->", "name=" + name + " date=" + date);
            SimpleDateFormat format = new SimpleDateFormat("MM-dd hh:mm:ss");
            return format.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Date();
    }

    /**
     * @param refreshable true可以下拉刷新，false反之
     */
    public void setUpRefreshable(boolean refreshable) {
        if (refreshable) {
            mPullToRefreshView.setOnFooterRefreshListener(footerRefreshListener);
        } else {
            mPullToRefreshView.setOnFooterRefreshListener(null);
        }
        mPullToRefreshView.setUpRefreshable(refreshable);

    }

    /**
     * @param refreshable true可以上拉加载更多，false反之
     */
    public void setDownRefreshable(boolean refreshable) {
        if (refreshable) {
            mPullToRefreshView.setOnHeaderRefreshListener(headerRefreshListener);

        } else {
            mPullToRefreshView.setOnHeaderRefreshListener(null);
        }
        mPullToRefreshView.setDownRefreshable(refreshable);
    }

    /**
     * @param enable true if the listView can delete item
     */
    public void enableDelete(boolean enable) {
        if (enable) getListView().setOnItemLongClickListener(this);
        else getListView().setOnItemLongClickListener(null);
    }

    @Override
    public void setTitle(CharSequence title) {
        ((TextView) findViewById(R.id.common_title_tv)).setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(getString(titleId));
    }

    /**
     * 拼接筛选条件参数。
     * 筛选条件的数据格式为json格式。例
     * {"keywords":"夏天","sort":"time"}    //搜索关键词为“夏天”,按时间排序
     * {"keywords":"夏天","sort":"hot"}    //搜索关键词为“夏天”,按热度排序
     * {"company_id":26}               //获取公司id为26的求购信息列表
     * {"category_id":2}                 //获取分类id为2的求购信息列表
     * null                           //无条件，默认排序方式
     * Sort的值就两种情况，一种是按发布时间排序(time)，另一个是按热度排序(hot)
     *
     * @return
     */
    protected String makeCondition(String key, Object value) {
        return new StringBuffer()
                .append("\"" + key + "\"")
                .append(":")
                .append("\"" + value + "\"")
                .toString();
    }

    /**
     * called when a delete Dialog is prepared to show,
     *
     * @return the title used for the delete dialog
     */
    protected CharSequence onPrepareDelete(int position) {
        return null;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        edit_position = position;
        delteDialog.setItemText(onPrepareDelete(position), position);
        delteDialog.show();
        return true;
    }

    /**
     * called when user select "删除"
     *
     * @param position the position of the delete item in the listView
     */
    protected void onDelete(int position) {
    }

    @Override
    public void onItemDelete(View view, int itemId) {
        onDelete(edit_position);
    }

    /**
     * 如果listView已经加载了所有的数据，就应该隐藏上拉加载更多功能
     */
    protected void onLoadFinish() {
        onLoadMoreFinish(getAdapter().getCount() % pageSize == 0);
        onRefreshFinish();
    }

    /**
     * @param hasMoreData true 如果还有更多数据 false 数据已经加载完毕
     */
    protected void onLoadMoreFinish(boolean hasMoreData) {
        mPullToRefreshView.onFooterRefreshComplete();
        if (!hasMoreData) {
            mPullToRefreshView.setUpRefreshable(false);
            lastId = getListView().getChildCount();//
            ContextUtil.toast("数据已经加载完毕！");
        } else {
            mPullToRefreshView.setUpRefreshable(true);
        }
    }

    public void onRefreshFinish() {
        if (!isHeadRefreshing) return;
        lastRefreshTime = new Date();
        mPullToRefreshView.setLastUpdated(getString(R.string.last_update) + getDateString(lastRefreshTime));
        mPullToRefreshView.onHeaderRefreshComplete();
        isHeadRefreshing = false;
    }

    /**
     * 下拉刷新监听
     */
    private OnHeaderRefreshListener headerRefreshListener = new OnHeaderRefreshListener() {
        @Override
        public void onHeaderRefresh(PullToRefreshView view) {
            lastId = 0;
            mPullToRefreshView.setUpRefreshable(true);
            isHeadRefreshing = true;
            onRefresh();
        }
    };
    /**
     * 上拉加载更多监听
     */
    private OnFooterRefreshListener footerRefreshListener = new OnFooterRefreshListener() {
        @Override
        public void onFooterRefresh(PullToRefreshView view) {
            view.setFootViewVisibility(View.VISIBLE);
            onLoadMore(lastId += pageSize);
        }
    };

    /**
     * 加载更多
     */
    protected void onLoadMore(int lastId) {

    }

    /**
     *
     */
    protected void onRefresh() {

    }
}
