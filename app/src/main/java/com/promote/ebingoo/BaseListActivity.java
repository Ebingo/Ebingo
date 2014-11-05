package com.promote.ebingoo;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jch.lib.util.HttpUtil;
import com.jch.lib.view.PullToRefreshView;
import com.promote.ebingoo.center.ItemDelteDialog;
import com.promote.ebingoo.util.ContextUtil;
import com.promote.ebingoo.util.LogCat;
import com.promote.ebingoo.view.RefreshListView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.jch.lib.view.PullToRefreshView.*;

/**
 * 用于展示列表的Activity，有一个默认的布局文件R.layout.activity_base_list，子类不需要设定布局文件。标题是在manifest中对应Activity的label值
 * zhuchao on 2014/9/18.
 */
public class BaseListActivity extends ListActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener, ItemDelteDialog.DeleteItemListener {
    public static final String ARG_REFRESH = "refresh";
    /**
     * 分页的lastId
     */
    protected int lastId = 0;
    protected int pageSize = 10;
    /**
     * 弹出编辑框的位置
     */
    private int edit_position = -1;
    private ItemDelteDialog delteDialog;
    private RefreshListView mPullToRefreshView;
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

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_list);
        setTitle(getTitle());
        delteDialog = new ItemDelteDialog(this, this);
        mPullToRefreshView = (RefreshListView) findViewById(android.R.id.list);
//        mPullToRefreshView.setFootViewVisibility(View.GONE);//先把FooterView隐藏，上来就显示很不美观

        lastRefreshTime = getLastRefreshTime(getLocalClassName());
        mPullToRefreshView.setShowLastUpdatedText(true);
        mPullToRefreshView.setOnRefreshListener(l);

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
        mPullToRefreshView.setHasMore(refreshable);
        if (!refreshable)mPullToRefreshView.hideFooter();
    }

    /**
     * @param refreshable true可以上拉加载更多，false反之
     */
    public void setDownRefreshable(boolean refreshable) {
        if (!refreshable)mPullToRefreshView.hideHeader();
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
        refreshFooterView(false);
        onRefreshFinish();
    }

    /**
     * @param isDelete true 由于删除导致需要刷新页面
     *                 false 非删除导致需要刷新页面
     */
    protected void refreshFooterView(boolean isDelete) {

        if (isDelete) {
            lastId = 0;//如果用户删除数据，则无论用户接下来是上拉还是下拉
        } else {
            int itemCount = getAdapter().getCount();
            LogCat.i("item:"," itemCount="+itemCount+" oldSize="+lastId);
            if (itemCount -lastId < pageSize) {
                mPullToRefreshView.setHasMore(false);
                if (itemCount!=0)ContextUtil.toast("数据已经加载完毕！");
            }
            lastId = itemCount;
        }
        if (isDataFullScreen()) {
//            mPullToRefreshView.setFoo;
        }
    }

    /**
     * 判断数据有没有满屏，逻辑是 可见的item数与总数相等，说明没有满屏
     *
     * @return
     */
    private boolean isDataFullScreen() {
        int itemCount = getAdapter().getCount();
        ListView lv = getListView();
        int oneScreenItemCount = lv.getLastVisiblePosition() - lv.getFirstVisiblePosition() + 1;
        return oneScreenItemCount == itemCount;
    }

    public void onRefreshFinish() {
        if (!isHeadRefreshing) return;
        lastRefreshTime = new Date();
        mPullToRefreshView.onRefreshComplete();
        isHeadRefreshing = false;
    }

    private RefreshListView.OnRefreshListener l = new RefreshListView.OnRefreshListener() {
        @Override
        public void onRefresh() {
            lastId = 0;

            mPullToRefreshView.setHasMore(true);
            isHeadRefreshing = true;
            if (HttpUtil.isNetworkConnected(getApplicationContext())) {
                startRefresh();
            } else {
                onLoadFinish();
            }
        }

        @Override
        public void onLoadMore() {
            if (lastId != 0) {
                startLoadMore();
            } else {
                startRefresh();
            }
        }
    };


    final public void startRefresh() {
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onRefresh();
            }
        }, 100);
    }

    final public void startLoadMore() {
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onLoadMore(lastId);
            }
        }, 100);
    }

    /**
     * 加载更多
     */
    protected void onLoadMore(int lastId) {

    }

    /**
     * 刷新
     */
    protected void onRefresh() {

    }

    /**
     * 设置一个View，当列表数据为空的时候显示
     *
     * @param v
     */
    public void setEmptyView(View v) {
        v.setId(android.R.id.empty);
        FrameLayout content= (FrameLayout) findViewById(R.id.content);
        content.removeView(content.findViewById(android.R.id.empty));
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity=Gravity.CENTER;
        content.addView(v, lp);
        mPullToRefreshView.setEmptyView(v);
        onContentChanged();
    }
}
