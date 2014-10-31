package com.promote.ebingoo.InformationActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jch.lib.view.PullToRefreshView;
import com.promote.ebingoo.R;
import com.promote.ebingoo.util.ContextUtil;

/**
 * Created by jhc on 2014/9/26.
 * <p/>
 * 只有一个listView的fragment.到无数据时会有没有数据提示。
 */
public class CommonListFragment extends Fragment implements PullToRefreshView.OnFooterRefreshListener, PullToRefreshView.OnHeaderRefreshListener {


    private TextView commonnodatai;
    private ListView commondynamiclv;
    private PullToRefreshView commmonfreshview;

    public int getAddMaxeNum() {
        return mAddMaxeNum;
    }

    /**
     * 每一次加载更多的数据条数. *
     */
    private int mAddMaxeNum = 20;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        container = (ViewGroup) LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.common_list_layout, null);
        initialize(container);
        return container;
    }

    @Override
    public void onFooterRefresh(PullToRefreshView view) {

    }

    private void initialize(View view) {
        commonnodatai = (TextView) view.findViewById(R.id.common_nodata_i);
        commondynamiclv = (ListView) view.findViewById(R.id.common_dynamic_lv);
        commmonfreshview = (PullToRefreshView) view.findViewById(R.id.commmon_freshview);
        commmonfreshview.setOnFooterRefreshListener(this);
        commmonfreshview.setOnHeaderRefreshListener(this);
    }

    protected void setUpRefreshAble(boolean refreshAble) {
        commmonfreshview.setUpRefreshable(refreshAble);
    }

    protected void setDownRefreshAble(boolean refreshAble) {
        commmonfreshview.setDownRefreshable(refreshAble);
    }

    protected void setAdapter(BaseAdapter adapter) {
        commondynamiclv.setAdapter(adapter);
    }

    protected void setOnItemClickListener(AdapterView.OnItemClickListener itemClickListener) {
        commondynamiclv.setOnItemClickListener(itemClickListener);
    }

    protected void setOnItemSelectedListener(AdapterView.OnItemSelectedListener itemSelectedListener) {

        commondynamiclv.setOnItemSelectedListener(itemSelectedListener);
    }

    protected void setOnItemLongClickListener(AdapterView.OnItemLongClickListener itemLongClickListener) {
        commondynamiclv.setOnItemLongClickListener(itemLongClickListener);
    }

    /**
     * set description about no data.
     *
     * @param noDataStr
     */
    protected void setNoDataText(String noDataStr) {
        commonnodatai.setText(noDataStr);
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {

    }

    /**
     * load more data.
     *
     * @param addMaxNum
     */
    public void setAddMaxNum(int addMaxNum) {

        this.mAddMaxeNum = addMaxNum;
    }

    /**
     * Could will be load more date.
     *
     * @param loadMore
     * @param msgAble
     */
    public void loadMore(boolean loadMore, boolean msgAble) {
        if (!loadMore) {
            commmonfreshview.setUpRefreshable(false);       //不可再下拉加载。
            if (msgAble) {
                ContextUtil.toast(getString(R.string.load_data_complete));
            }
        }


        commmonfreshview.onFooterRefreshComplete();
    }


    /**
     * 有数据
     */
    public void haseData() {
        commmonfreshview.setVisibility(View.VISIBLE);
        commonnodatai.setVisibility(View.GONE);
    }

    /**
     * 乜有加载到数据。
     */
    public void noData() {
        commmonfreshview.setVisibility(View.GONE);
        commonnodatai.setVisibility(View.VISIBLE);
    }

}
