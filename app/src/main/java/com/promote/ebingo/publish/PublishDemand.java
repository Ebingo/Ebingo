package com.promote.ebingo.publish;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.bean.DetailInfoBean;
import com.promote.ebingo.center.MyDemandActivity;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.publish.login.LoginManager;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import static com.promote.ebingo.publish.PublishFragment.PICK_CATEGORY;
import static com.promote.ebingo.publish.PublishFragment.PICK_DESCRIPTION;
import static com.promote.ebingo.publish.PublishFragment.PICK_FOR_DEMAND;
import static com.promote.ebingo.publish.PublishFragment.PICK_TAGS;
import static com.promote.ebingo.publish.PublishFragment.TYPE_DEMAND;
import static com.promote.ebingo.publish.PublishFragment.Error;

/**
 * 发布求购
 * Created by acer on 2014/9/2.
 */
public class PublishDemand extends Fragment implements View.OnClickListener, PublishEditActivity.EditInfo {
    private DetailInfoBean mDetailInfo;
    TextView tv_pick_category;
    TextView tv_pick_description;
    TextView tv_tags;

    EditText edit_title;
    EditText edit_contact;
    EditText edit_phone;
    EditText edit_demand_num;

    EditText edit_unit;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.publish_demand, container, false);
        init(view);
        if (mDetailInfo!=null)edit(mDetailInfo);
        return view;
    }

    private void init(View view) {
        tv_pick_category = (TextView) view.findViewById(R.id.tv_category);
        tv_tags = (TextView) view.findViewById(R.id.tv_tags);
        tv_pick_description = (TextView) view.findViewById(R.id.tv_description);

        edit_title = (EditText) view.findViewById(R.id.edit_title);
        edit_contact = (EditText) view.findViewById(R.id.edit_contact);
        edit_phone = (EditText) view.findViewById(R.id.edit_mobile);
        edit_demand_num = (EditText) view.findViewById(R.id.edit_demand_num);
        edit_unit = (EditText) view.findViewById(R.id.edit_unit);

        view.findViewById(R.id.pick_category).setOnClickListener(this);
        view.findViewById(R.id.pick_description).setOnClickListener(this);
        view.findViewById(R.id.pick_tags).setOnClickListener(this);
        view.findViewById(R.id.btn_publish).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pick_category: {
                Intent intent = new Intent(getActivity(), PickCategoryActivity.class);
                getActivity().startActivityForResult(intent, PICK_FOR_DEMAND | PICK_CATEGORY);
                break;
            }
            case R.id.pick_description: {
                Intent intent = new Intent(getActivity(), EditDescription.class);
                intent.putExtra(EditDescription.CONTENT, tv_pick_description.getText().toString().trim());
                getActivity().startActivityForResult(intent, PICK_FOR_DEMAND | PICK_DESCRIPTION);
                break;
            }
            case R.id.pick_tags: {
                Intent intent = new Intent(getActivity(), AddTagsActivity.class);
                getActivity().startActivityForResult(intent, PICK_FOR_DEMAND | PICK_TAGS);
                break;
            }

            case R.id.btn_publish: {
                Integer category_id = (Integer) tv_pick_category.getTag();
                String title = edit_title.getText().toString().trim();
                String tags = tv_tags.getText().toString().trim();
                String description = tv_pick_description.getText().toString().trim();
                String contacts = edit_contact.getText().toString().trim();
                String contact_phone = edit_phone.getText().toString().trim();
                String demand_num = edit_demand_num.getText().toString().trim();
                String unit = edit_unit.getText().toString().trim();
                Integer company_id = Company.getInstance().getCompanyId();

                if (company_id == null) ContextUtil.toast("请重新登录！");
                else if (category_id == null)
                    Error.showError(tv_pick_category, Error.CATEGORY_EMPTY);
                else if (TextUtils.isEmpty(title)) Error.showError(edit_title, Error.TITLE_EMPTY);
                else if (TextUtils.isEmpty(description))
                    Error.showError(tv_pick_description, Error.DESCRIPTION_EMPTY);
                else if (TextUtils.isEmpty(demand_num))
                    Error.showError(edit_demand_num, Error.BUY_NUM_EMPTY);
                else if (TextUtils.isEmpty(contacts))
                    Error.showError(edit_contact, Error.CONTACT_EMPTY);
                else if (getChineseNum(contacts) < 2 || getChineseNum(contacts) > 4)
                    Error.showError(edit_contact, Error.CONTACT_LENGTH_ERROR);
                else if (TextUtils.isEmpty(contact_phone))
                    Error.showError(edit_phone, Error.PHONE_EMPTY);
                else if (!LoginManager.isMobile(contact_phone))
                    Error.showError(edit_phone, Error.PHONE_FORMAT_ERROR);
                else if (TextUtils.isEmpty(unit)) Error.showError(edit_unit, Error.NULL_UNIT);
                else {
                    EbingoRequestParmater parmater = new EbingoRequestParmater(v.getContext());
                    parmater.put("type", TYPE_DEMAND);
                    parmater.put("company_id", company_id);
                    parmater.put("category_id", category_id);
                    parmater.put("title", title);
                    parmater.put("tags", tags);
                    parmater.put("description", description);
                    parmater.put("contacts", contacts);
                    parmater.put("contacts_phone", contact_phone);
                    parmater.put("buy_num", demand_num);
                    parmater.put("unit", unit);
                    startPublish(parmater);
                }

                break;
            }
        }
    }

    private int getChineseNum(String input) {
        LogCat.i("--->", "input:" + input.length());
        return input.length();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String result = null;
        if (data != null) {
            result = data.getStringExtra("result");
        }
        requestCode = requestCode & (PublishFragment.REQUEST_MASK);
        if (result != null) {
            switch (requestCode) {
                case PICK_CATEGORY:
                    tv_pick_category.setText(result);
                    tv_pick_category.setTag(data.getIntExtra("categoryId", 0));
                    LogCat.i("--->", "categoryId:" + tv_pick_category.getTag());
                    break;
                case PICK_DESCRIPTION:
                    tv_pick_description.setText(result);
                    break;
                case PICK_TAGS:
                    tv_tags.setText(result);
                    break;

            }
        }
    }

    public void startPublish(EbingoRequestParmater parmater) {

        final ProgressDialog dialog = DialogUtil.waitingDialog(getActivity());
        HttpUtil.post(HttpConstant.saveInfo, parmater, new JsonHttpResponseHandler("utf-8") {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                ContextUtil.toast(response);
                try {
                    JSONObject result = response.getJSONObject("response");
                    if (HttpConstant.CODE_OK.equals(result.getString("code"))) {
                        Intent intent = new Intent(getActivity(), MyDemandActivity.class);
                        intent.putExtra("refresh",true);
                        startActivity(intent);
                        clearText();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                ContextUtil.toast(responseString);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dialog.dismiss();
            }
        });
    }

    /**
     * 清空文字
     */
    private void clearText() {

        edit_demand_num.setText(null);
        tv_pick_category.setText(null);
        edit_title.setText(null);
        tv_pick_description.setText(null);
        edit_contact.setText(null);
        edit_phone.setText(null);
        tv_tags.setText(null);
        edit_unit.setText(null );
    }

    @Override
    public void edit(DetailInfoBean infoBean) {
        mDetailInfo=infoBean;
        if (tv_pick_category!=null) {
            tv_pick_category.setTag(infoBean.getCategory_id());
            tv_pick_category.setText(infoBean.getCategory_name());
            edit_title.setText(infoBean.getTitle());
            tv_pick_description.setText(infoBean.getDescription());

            edit_demand_num.setText(infoBean.getMin_sell_num());
            edit_unit.setText(infoBean.getUnit());
            edit_contact.setText(infoBean.getContacts());
            edit_phone.setText(infoBean.getPhone_num());
        }
    }
}
