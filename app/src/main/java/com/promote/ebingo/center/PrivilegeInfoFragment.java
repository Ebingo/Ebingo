package com.promote.ebingo.center;


import android.app.Dialog;
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

    private VipType vipType;

    public VipType getVipType() {
        return vipType;
    }

    public void setVipType(VipType vipType) {
        this.vipType = vipType;
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

        if (vipType == null) {
            try {
                vipType = VipType.values()[savedInstanceState.getInt("vipType")];
            } catch (NullPointerException e) {
                throw new RuntimeException(PrivilegeInfoFragment.class.getName() + ":vipType is null!");
            }
        }
        String[] info = getResources().getStringArray(resMap.get(vipType.code));
        View v = inflater.inflate(R.layout.privilege_info, null);
        addItem((LinearLayout) v.findViewById(R.id.info_content), info, inflater);
        if (vipType.compareTo(VipType.VISITOR)==0){
            v.findViewById(R.id.btn_apply_vip).setVisibility(View.GONE);
        }else{
            v.findViewById(R.id.btn_apply_vip).setOnClickListener(this);
        }

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("vipType", vipType.ordinal());
    }

    private void addItem(LinearLayout content, String[] items, LayoutInflater inflater) {
        if (items == null) return;
        for (String str : items) {
            CheckBox item = (CheckBox) inflater.inflate(R.layout.tag, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (int) getResources().getDimension(R.dimen.widget_height));
            lp.setMargins(dp(3), dp(5), dp(3), dp(5));
            item.setLayoutParams(lp);
            item.setEnabled(false);
            item.setTextSize(13);
            item.setSingleLine(false);
            item.setText(str);
            item.setGravity(Gravity.CENTER_VERTICAL);
            content.addView(item);
        }
    }

    private int dp(int value) {
        return (int) Dimension.dp(value);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_apply_vip:

               VipType curVipType=VipType.parse(Company.getInstance().getVipType());
               if (getVipType().compareTo(curVipType)<=0){
                    ContextUtil.toast("您当前为"+curVipType.name+",不需要再申请"+getVipType().name+"。");
               }else{
                   startApply();
               }
                break;
        }
    }
    private void startApply(){
        final Dialog dialog= DialogUtil.waitingDialog(getActivity(),"正在提交申请...");
        EbingoRequestParmater parmater=new EbingoRequestParmater(getActivity());
        parmater.put("company_id", Company.getInstance().getCompanyId());
        parmater.put("apply_type","");
        HttpUtil.post(HttpConstant.applyVip,parmater,new JsonHttpResponseHandler("utf-8"){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                LogCat.i("--->",response+"");
                try {
                    response=response.getJSONObject("response");
                    if (HttpConstant.CODE_OK.equals(response.getString("code"))){
                        ContextUtil.toast("提交申请成功！");
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
}
