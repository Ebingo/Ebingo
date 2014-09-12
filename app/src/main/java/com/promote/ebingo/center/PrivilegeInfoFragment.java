package com.promote.ebingo.center;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.promote.ebingo.R;
import com.promote.ebingo.util.Dimension;

import java.util.HashMap;
import java.util.Map;
import static com.promote.ebingo.center.MyPrivilegeActivity.VipType;
/**
 * 显示每个等级会员拥有的特权
 * Created by acer on 2014/9/11.
 */
public class PrivilegeInfoFragment extends Fragment {

    private Map<String, Integer> resMap = new HashMap<String, Integer>();

    private MyPrivilegeActivity.VipType vipType;

    public MyPrivilegeActivity.VipType getVipType() {
        return vipType;
    }

    public void setVipType(MyPrivilegeActivity.VipType vipType) {
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

        if (vipType == null){
            try {
                vipType=VipType.values()[savedInstanceState.getInt("vipType")];
            } catch (NullPointerException e) {
                throw new RuntimeException(PrivilegeInfoFragment.class.getName()+":vipType is null!");
            }
        }
        String[] info = getResources().getStringArray(resMap.get(vipType.code));
        View v = inflater.inflate(R.layout.privilege_info, null);
        LinearLayout content = (LinearLayout) v.findViewById(R.id.info_content);
        addItem(content, info, inflater);

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("vipCode", vipType.ordinal());
    }

    private void addItem(LinearLayout content, String[] items, LayoutInflater inflater) {
        if (items == null) return;
        for (String str : items) {
            CheckBox item = (CheckBox) inflater.inflate(R.layout.tag, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (int)getResources().getDimension(R.dimen.widget_height));
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

}
