package com.promote.ebingo.center;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.jch.lib.util.VaildUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.BaseListActivity;
import com.promote.ebingo.InformationActivity.CallDialog;
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
public class CallRecordActivity extends BaseListActivity implements View.OnClickListener {
    private final static int ITEM_DELETE_ID = 2;
    private final static int ITEM_CANCEL_ID = 3;
    private ArrayList<CallRecord> records;
    private RecordAdapter adapter;
    private CallRecordManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = new CallRecordManager(getApplicationContext());
        getCallRecord();
        registerForContextMenu(getListView());
        enableDelete(true);
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
                    adapter = new RecordAdapter(CallRecordActivity.this, records);
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
    protected CharSequence onPrepareDelete(int position) {
        return records.get(position).getName();
    }

    @Override
    protected void onDelete(final int position) {
        EbingoRequestParmater parmater = new EbingoRequestParmater(this);
        parmater.put("company_id", Company.getInstance().getCompanyId());
        parmater.put("infoid", records.get(position).getInfoId());
        final Dialog dialog = DialogUtil.waitingDialog(this, "操作中...");
        HttpUtil.post(HttpConstant.deleteInfo, parmater, new JsonHttpResponseHandler("utf-8") {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    response = response.getJSONObject("response");
                    if (HttpConstant.CODE_OK.equals(response.getString("code"))) {
                        ContextUtil.toast("操作成功!");
                        records.remove(position);
                        adapter.notifyDataSetChanged();
                    } else {
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
            holder.dial.setTag(callRecord);
            holder.dial.setOnClickListener(new DialListener());
            convertView.setTag(holder);
            return convertView;
        }

        class DialListener implements View.OnClickListener {

            @Override
            public void onClick(View v) {
                try {
                    CallRecord record = (CallRecord) v.getTag();
                    LogCat.i("--->", record + "");
                    LogCat.i("--->", record.getPhone_num() + "");
                    LogCat.i("--->", record.getTo_id() + "");
                    CallRecordManager.dialNumber(CallRecordActivity.this, record);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private View.OnClickListener dialListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallRecord record = (CallRecord) v.getTag();
                CallRecordManager.dialNumber(CallRecordActivity.this, record);
            }
        };


        class ViewHolder {
            TextView title;
            TextView contact;
            TextView date;
            View dial;
        }
    }

    public static class CallRecordManager {
        private Context context;

        public CallRecordManager(Context context) {
            this.context = context;
        }

        public void addCallRecord(CallRecord record, JsonHttpResponseHandler handler) {
            EbingoRequestParmater parmater = new EbingoRequestParmater(context);
            parmater.put("call_id", record.getCall_id());
            parmater.put("to_id", record.getTo_id());
            parmater.put("info_id", record.getInfoId());
            HttpUtil.post(HttpConstant.addCallRecord, parmater, handler);
        }

        public void deleteCallRecord(int id, JsonHttpResponseHandler handler) {
            EbingoRequestParmater parmater = new EbingoRequestParmater(context);
            HttpUtil.post(HttpConstant.deleteInfo, parmater, handler);
        }

        /**
         * 拨打电话，并添加通话记录
         *
         * @param context
         * @param record  record中必须包含 phone_number,call_id,to_id,info_id
         */
        public static void dialNumber(final Activity context, final CallRecord record) {
            final String number = record.getPhone_num();
            if (TextUtils.isEmpty(number) || number.equals(VaildUtil.validPhone(number))) return;
            CallDialog.PhoneCallBack phoneCallBack = new CallDialog.PhoneCallBack() {
                @Override
                public void call(CallDialog dialog, String phoneNum) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                    context.startActivity(intent);
                    new CallRecordManager(context).addCallRecord(record, new JsonHttpResponseHandler());
                    dialog.dismiss();
                }
            };
            CallDialog callDialog = new CallDialog(context, phoneCallBack);
            callDialog.setCallphone(context.getString(R.string.dial_number_notice,record.getPhone_num()));
            callDialog.show();
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            builder.setTitle("拨打电话")
//                    .setMessage("是否拨打" + number + "?")
//                    .setPositiveButton("拨打", l)
//                    .setNegativeButton("取消", l)
//                    .show();


        }

    }


}
