package com.promote.ebingo;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.promote.ebingo.center.ItemDelteDialog;

/**
 * 用于展示列表的Activity，有一个默认的布局文件R.layout.activity_base_list，子类不需要设定布局文件。标题是在manifest中对应Activity的label值
 * zhuchao on 2014/9/18.
 */
public class BaseListActivity extends ListActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener, ItemDelteDialog.DeleteItemListener {

    private int delete_position = -1;
    private ItemDelteDialog delteDialog;
    protected TextView tv_no_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_list);
        setTitle(getTitle());
        delteDialog = new ItemDelteDialog(this, this);
        tv_no_data = (TextView) findViewById(android.R.id.empty);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_back_btn:
                finish();
                break;
        }
    }

    public void showNoData() {
        tv_no_data.setText(R.string.no_data);
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
        ((TextView) findViewById(R.id.common_title_tv)).setText(titleId);
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
        delete_position = position;
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
        onDelete(delete_position);
    }
}
