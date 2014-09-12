package com.promote.ebingo.publish;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.promote.ebingo.center.MyDemandActivity;
import com.promote.ebingo.center.MySupplyActivity;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import static com.promote.ebingo.publish.PublishFragment.*;

/**
 * 发布求购
 * Created by acer on 2014/9/2.
 */
public class PublishDemand extends Fragment implements View.OnClickListener {

    TextView tv_category;
    TextView tv_description;
    TextView tv_tags;

    EditText edit_title;
    EditText edit_contact;
    EditText edit_mobile;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.publish_demand, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        tv_category = (TextView) view.findViewById(R.id.tv_category);
        tv_tags = (TextView) view.findViewById(R.id.tv_tags);
        tv_description = (TextView) view.findViewById(R.id.tv_description);

        edit_title = (EditText) view.findViewById(R.id.edit_title);
        edit_contact = (EditText) view.findViewById(R.id.edit_contact);
        edit_mobile = (EditText) view.findViewById(R.id.edit_mobile);

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
                startActivityForResult(intent, PICK_FOR_DEMAND | PICK_CATEGORY);
                break;
            }
            case R.id.pick_description: {
                Intent intent = new Intent(getActivity(), EditDescription.class);
                intent.putExtra("description", tv_description.getText().toString().trim());
                startActivityForResult(intent, PICK_FOR_DEMAND | PICK_DESCRIPTION);
                break;
            }
            case R.id.pick_tags: {
                Intent intent = new Intent(getActivity(), AddTagsActivity.class);
                startActivityForResult(intent, PICK_FOR_DEMAND | PICK_TAGS);
                break;
            }

            case R.id.btn_publish: {

                Fragment parent = getParentFragment();
                EbingoRequestParmater parmater = new EbingoRequestParmater(v.getContext());
                parmater.put("type", TYPE_DEMAND);
                parmater.put("company_id", Company.getInstance().getCompanyId());
                parmater.put("category_id", tv_category.getTag());
                parmater.put("region_name", Company.getInstance().getRegion());
                parmater.put("title", edit_title.getText().toString().trim());
                parmater.put("tags", tv_tags.getText().toString().trim());
                parmater.put("description", tv_description.getText().toString().trim());
                parmater.put("contacts", edit_contact.getText().toString().trim());
                parmater.put("contacts_phone", edit_mobile.getText().toString().trim());
                startPublish(parmater);

                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String result = null;
        if (data != null) {
            result = data.getStringExtra("result");
        }
        requestCode = requestCode & (0xfff);
        if (result != null) {
            switch (requestCode) {
                case PICK_CATEGORY:
                    tv_category.setText(result);
                    tv_category.setTag(data.getIntExtra("categoryId", 0));
                    LogCat.i("--->", "categoryId:" + tv_category.getTag());
                    break;
                case PICK_DESCRIPTION:
                    tv_description.setText(result);
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
        tv_category.setText(null);
        edit_title.setText(null);
        tv_description.setText(null);
        edit_contact.setText(null);
        edit_mobile.setText(null);
        tv_tags.setText(null);

    }
}
