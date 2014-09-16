package com.promote.ebingo.center;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.CallRecord;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.JsonUtil;
import com.promote.ebingo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 通话记录
 */
public class CallRecordActivity extends ListActivity implements View.OnClickListener {
    private final static int ITEM_DELETE_ID = 2;
    private final static int ITEM_CANCEL_ID = 3;
    private ArrayList<CallRecord> records;
    private RecordAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_record);
        ((TextView) findViewById(R.id.common_title_tv)).setText(getTitle());
        findViewById(R.id.common_back_btn).setOnClickListener(this);
        getCallRecord();
        registerForContextMenu(getListView());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_back_btn:
                finish();
                break;
        }
    }

    private void getCallRecord() {
        EbingoRequestParmater parmater = new EbingoRequestParmater(getApplicationContext());
        parmater.put("company_id", Company.getInstance().getCompanyId());
        HttpUtil.post(HttpConstant.getCallRecord, parmater, new JsonHttpResponseHandler("utf-8") {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                LogCat.i("--->", response + "");
                try {
                    JSONArray recordsJson = response.getJSONArray("response");
                    records = new ArrayList<CallRecord>();
                    JsonUtil.getArray(recordsJson, CallRecord.class, records);
                    adapter=new RecordAdapter(CallRecordActivity.this, records);
                    setListAdapter(adapter);
                } catch (JSONException e) {
                    LogCat.e("NO Call Record!");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        ListView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        CallRecord record = records.get(info.position);
        menu.setHeaderTitle(record.getName());
        menu.add(0, ITEM_DELETE_ID, 1, "删除");
        menu.add(0, ITEM_CANCEL_ID, 2, "取消");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case ITEM_CANCEL_ID:

                break;
            case ITEM_DELETE_ID: {
                final ListView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                EbingoRequestParmater parmater = new EbingoRequestParmater(this);
                parmater.put("company_id", Company.getInstance().getCompanyId());
                parmater.put("infoid", records.get(info.position).getInfoId());
                final Dialog dialog = DialogUtil.waitingDialog(this, "操作中...");
                HttpUtil.post(HttpConstant.deleteInfo, parmater, new JsonHttpResponseHandler("utf-8") {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            response = response.getJSONObject("response");
                            if (HttpConstant.CODE_OK.equals(response.getString("code"))) {
                                ContextUtil.toast("操作成功!");
                                records.remove(info.position);
                                adapter.notifyDataSetChanged();
                            }else {
                                ContextUtil.toast("操作失败!");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        dialog.dismiss();
                    }
                });
                ContextUtil.toast("delete");
                break;
            }
        }
        return true;
    }

    class RecordAdapter extends BaseAdapter {

        private List<CallRecord> data;
        private Context mContext;

        RecordAdapter(Context mContext, List<CallRecord> data) {
            this.data = data;
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.call_record_item, null);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.contact = (TextView) convertView.findViewById(R.id.contact);
                holder.date = (TextView) convertView.findViewById(R.id.date);
                holder.dial = convertView.findViewById(R.id.dial);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            CallRecord callRecord = data.get(position);
            holder.title.setText(callRecord.getName());
            holder.contact.setText(callRecord.getContacts());
            holder.date.setText(callRecord.getCall_time());
            holder.dial.setTag(callRecord.getPhone_num());
            holder.dial.setOnClickListener(dialListener);
            convertView.setTag(holder);
            return convertView;
        }

        private View.OnClickListener dialListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContextUtil.dialNumber(CallRecordActivity.this, v.getTag().toString());
            }
        };


        class ViewHolder {
            TextView title;
            TextView contact;
            TextView date;
            View dial;
        }
    }
}
