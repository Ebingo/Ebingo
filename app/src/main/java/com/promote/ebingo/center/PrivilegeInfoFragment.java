package com.promote.ebingo.center;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.publish.EbingoDialog;
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


    public static PrivilegeInfoFragment newInstance(VipType vipType) {
        PrivilegeInfoFragment fragment = new PrivilegeInfoFragment();
        fragment.setDisplayVipType(vipType);
        return fragment;
    }

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (displayVipType == null) {
            displayVipType = VipType.parse(savedInstanceState.getString("displayVipType"));
        }
        View v = inflater.inflate(R.layout.privilege_info, null);
        if (displayVipType.compareTo(VipType.getCompanyInstance()) <= 0) {
            v.findViewById(R.id.btn_apply_vip).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.btn_apply_vip).setOnClickListener(this);
        }
        ImageView iv = (ImageView) v.findViewById(R.id.iv_vip_info);
        switch (displayVipType) {
            case VISITOR:
                iv.setImageResource(R.drawable.visitor_info);
                break;
            case Experience_Vip:
                iv.setImageResource(R.drawable.experience_info);
                break;
            case Standard_VIP:
                iv.setImageResource(R.drawable.standard_info);
                break;
            case Silver_VIP:
                iv.setImageResource(R.drawable.silver_info);
                break;
            case Gold_VIP:
                iv.setImageResource(R.drawable.gold_info);
                break;
            case Platinum_VIP:
                iv.setImageResource(R.drawable.platinum_info);
                break;
        }

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("displayVipType", displayVipType.code);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_apply_vip:
                startApply();
                break;
        }
    }

    public void startApply() {
        VipType curVipType = VipType.parse(Company.getInstance().getVipType());
        VipType disPlayVip = getDisplayVipType();
        if (disPlayVip == VipType.VISITOR) {
            return;
        } else if (disPlayVip.compareTo(curVipType) <= 0) {
            ContextUtil.toast("您当前为" + curVipType.name + ",不需要再申请" + getDisplayVipType().name + "。");
            return;
        } else if (getDisplayVipType().compareTo(VipType.Experience_Vip) == 0) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            return;
        }
        final Dialog dialog = DialogUtil.waitingDialog(getActivity(), "正在提交申请...");
        EbingoRequestParmater parmater = new EbingoRequestParmater(getActivity());
        parmater.put("company_id", Company.getInstance().getCompanyId());
        parmater.put("apply_type", displayVipType.code);
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
        EbingoDialog dialog = new EbingoDialog(getActivity());
        dialog.setTitle(R.string.warn);
        dialog.setMessage("提交申请成功！请耐心等待工作人员处理。");
        dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
