package com.promote.ebingo.center;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.publish.VipType;
import com.promote.ebingo.publish.login.LoginActivity;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.Dimension;
import com.promote.ebingo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 显示每个等级会员拥有的特权
 * Created by acer on 2014/9/11.
 */
public class PrivilegeInfoFragment extends Fragment implements View.OnClickListener {

    private Map<String, Integer> resMap = new HashMap<String, Integer>();

    private VipType displayVipType;

    public VipType getDisplayVipType() {
        return displayVipType;
    }

    public void setDisplayVipType(VipType vipType) {
        this.displayVipType = vipType;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resMap.put(VipType.VISITOR.code, R.array.privilege_info_visitor);
        resMap.put(VipType.NORMAL_VIP.code, R.array.privilege_info_normal_vip);
        resMap.put(VipType.VIP.code, R.array.privilege_info_vip);
        resMap.put(VipType.VVIP.code, R.array.privilege_info_vvip);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (displayVipType == null) {
            try {
                displayVipType = VipType.values()[savedInstanceState.getInt("displayVipType")];
            } catch (NullPointerException e) {
                throw new RuntimeException(PrivilegeInfoFragment.class.getName() + ":displayVipType is null!");
            }
        }
        String[] info = getResources().getStringArray(resMap.get(displayVipType.code));
        View v = inflater.inflate(R.layout.privilege_info, null);
        addItem((LinearLayout) v.findViewById(R.id.info_content), info, inflater);
        if (displayVipType.compareTo(VipType.parse(Company.getInstance().getVipType())) <= 0) {
            v.findViewById(R.id.btn_apply_vip).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.btn_apply_vip).setOnClickListener(this);
        }

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("displayVipType", displayVipType.ordinal());
    }

    private void addItem(LinearLayout content, String[] items, LayoutInflater inflater) {
        if (items == null) return;
        for (String str : items) {
            TagView tagView = (TagView) inflater.inflate(R.layout.sample_tag_view, null);
            tagView.setNumber(0);
            tagView.setText(str);
            tagView.setTextSize(13);
            content.addView(tagView);
        }
    }

    private int dp(int value) {
        return (int) Dimension.dp(value);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_apply_vip:
                VipType curVipType = VipType.parse(Company.getInstance().getVipType());
                if (getDisplayVipType().compareTo(curVipType) <= 0) {
                    ContextUtil.toast("您当前为" + curVipType.name + ",不需要再申请" + getDisplayVipType().name + "。");
                } else if (getDisplayVipType().compareTo(VipType.NORMAL_VIP) == 0) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    startApply();
                }
                break;
        }
    }

    private void startApply() {
        final Dialog dialog = DialogUtil.waitingDialog(getActivity(), "正在提交申请...");
        EbingoRequestParmater parmater = new EbingoRequestParmater(getActivity());
        parmater.put("company_id", Company.getInstance().getCompanyId());
        parmater.put("apply_type", "");
        HttpUtil.post(HttpConstant.applyVip, parmater, new JsonHttpResponseHandler("utf-8") {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                LogCat.i("--->", response + "");
                try {
                    response = response.getJSONObject("response");
                    if (HttpConstant.CODE_OK.equals(response.getString("code"))) {
                        showDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dialog.dismiss();
            }
        });
    }

    private void showDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("温馨提示")
                .setMessage("提交申请成功！请耐心等待工作人员处理。")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
