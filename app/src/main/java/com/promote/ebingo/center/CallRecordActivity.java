package com.promote.ebingo.center;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.jch.lib.util.VaildUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.BaseListActivity;
import com.promote.ebingo.InformationActivity.BuyInfoActivity;
import com.promote.ebingo.InformationActivity.CallDialog;
import com.promote.ebingo.InformationActivity.ProductInfoActivity;
import com.promote.ebingo.R;
import com.promote.ebingo.application.Constant;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.CallRecord;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.publish.EbingoDialog;
import com.promote.ebingo.publish.VipType;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.FileUtil;
import com.promote.ebingo.util.JsonUtil;
import com.promote.ebingo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 通话记录
 */
public class CallRecordActivity extends BaseListActivity implements View.OnClickListener {
    private ArrayList<CallRecord> records;
    private RecordAdapter adapter;
    private static boolean REFRESH = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        records = new ArrayList<CallRecord>();
        enableDelete(true);
        enableCache(FileUtil.FILE_CALL_RECORD, records);
        if (records.size() == 0) {
            getCallRecord();
        }
    }

    public static void setRefresh() {
        REFRESH = true;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        CallRecord record = records.get(position);
        Intent intent = new Intent();
        LogCat.i("--->", "type=" + record.getType() + " info_id=" + record.getInfoId());
        if (Constant.PUBLISH_SUPPLY.equals(record.getType())) {
            intent.setClass(this, ProductInfoActivity.class);
            intent.putExtra(ProductInfoActivity.ARG_ID, record.getInfoId());
        } else {
            intent.setClass(this, BuyInfoActivity.class);
            intent.putExtra(BuyInfoActivity.DEMAND_ID, record.getInfoId());
        }
        startActivity(intent);
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
                    records.clear();
                    JsonUtil.getArray(recordsJson, CallRecord.class, records);
                    adapter = new RecordAdapter(CallRecordActivity.this, records);
                    setListAdapter(adapter);
                    REFRESH = false;
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
                    LogCat.i("--->", response + "");
                    response = response.getJSONObject("response");
                    if (HttpConstant.CODE_OK.equals(response.getString("code"))) {
                        ContextUtil.toast("操作成功!");
                        records.remove(position);
                        adapter.notifyDataSetChanged();
                        refreshFooterView(true);
                        onRefresh();
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
            holder.dial.setTag(callRecord);
            holder.dial.setOnClickListener(new DialListener());
            holder.date.setText(callRecord.getCall_time());
            convertView.setTag(holder);
            return convertView;
        }

        private boolean isSameDay(Date day1, Date day2) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String ds1 = sdf.format(day1);
            String ds2 = sdf.format(day2);
            if (ds1.equals(ds2)) {
                return true;
            } else {
                return false;
            }
        }

        class DialListener implements View.OnClickListener {

            @Override
            public void onClick(View v) {
                try {
                    CallRecord record = (CallRecord) v.getTag();

                    boolean canDial = true;
//                    VipType vipType = VipType.parse(Company.getInstance().getVipType());
//                    VipType.VipInfo info = vipType.getVipInfo();
//                    String type = record.getType();
//                    canDial=info.canDial(type);
                    if (canDial) {//这里加判断，为了防止会员过期后，此处还有通话记录
                        CallRecordManager.dialNumber(CallRecordActivity.this, record);
                    } else {
                        EbingoDialog.newInstance(CallRecordActivity.this, EbingoDialog.DialogStyle.STYLE_TO_PRIVILEGE).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

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

        public static void addCallRecord(Context context,CallRecord record, JsonHttpResponseHandler handler) {
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

            EbingoDialog dialog=EbingoDialog.newInstance(context, EbingoDialog.DialogStyle.STYLE_CALL_PHONE);
            dialog.setTitle(record.getContacts());
            dialog.setMessage(context.getString(R.string.dial_number_notice, record.getPhone_num()));
            dialog.setPositiveButton(R.string.make_call,new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LogCat.i("--->", "dial:" + number);
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                    context.startActivity(intent);
                    CallRecordManager.addCallRecord(context, record, new JsonHttpResponseHandler());
                    dialog.dismiss();
                }
            });
            dialog.show();
        }

    }


}
