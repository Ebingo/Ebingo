package com.promote.ebingo.publish;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.promote.ebingo.R;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.util.LogCat;

import static com.promote.ebingo.publish.PublishFragment.*;

/**
 * Created by acer on 2014/9/2.
 */
public class PublishSupply extends Fragment implements View.OnClickListener {
    private EditText edit_title;
    private EditText edit_contact;
    private EditText edit_phone;
    private EditText edit_price;
    private EditText edit_book_standard;

    private TextView tv_pick_description;
    private TextView tv_pick_category;
    private TextView tv_pick_region;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_publish_demand, null);
        edit_title= (EditText) v.findViewById(R.id.edit_title);
        edit_contact= (EditText) v.findViewById(R.id.edit_contact);
        edit_phone = (EditText) v.findViewById(R.id.edit_phone);
        edit_price= (EditText) v.findViewById(R.id.edit_price);
        edit_book_standard= (EditText) v.findViewById(R.id.edit_book_standard);

        tv_pick_category= (TextView) v.findViewById(R.id.tv_pick_category);
        tv_pick_description= (TextView) v.findViewById(R.id.tv_pick_description);
        tv_pick_region= (TextView) v.findViewById(R.id.tv_pick_region);

        tv_pick_category.setOnClickListener(this);
        tv_pick_description.setOnClickListener(this);
        tv_pick_region.setOnClickListener(this);
        v.findViewById(R.id.tv_pick_image).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_pick_category:{
                Intent intent=new Intent(getActivity(),PickCategoryActivity.class);
                startActivityForResult(intent,PICK_FOR_SUPPLY|PICK_CATEGORY);
                break;
            }
            case R.id.tv_pick_description:{
                Intent intent=new Intent(getActivity(),EditDescription.class);
                intent.putExtra("description",tv_pick_description.getText().toString().trim());
                startActivityForResult(intent,PICK_FOR_SUPPLY|PICK_DESCRIPTION);
                break;
            }
            case R.id.tv_pick_region:{
                Intent intent=new Intent(getActivity(),EditDescription.class);
                startActivityForResult(intent,PICK_FOR_SUPPLY|PICK_REGION);
                break;
            }

            case R.id.tv_pick_image: {

                break;
            }
            case R.id.btn_publish: {

                Fragment parent = getParentFragment();

                if (parent instanceof PublishFragment) {
                    EbingoRequestParmater parmater = new EbingoRequestParmater(v.getContext());
                    parmater.put("type", TYPE_DEMAND);
                    parmater.put("company_id", Company.getInstance().getCompanyId());
                    parmater.put("category_id", tv_pick_category.getTag());
                    parmater.put("region_name", Company.getInstance().getRegion());
                    parmater.put("title", edit_title.getText().toString().trim());
                    parmater.put("description", tv_pick_description.getText().toString().trim());
                    parmater.put("contacts", edit_contact.getText().toString().trim());
                    parmater.put("contacts_phone", edit_phone.getText().toString().trim());
                    ((PublishFragment) parent).startPublish(parmater);
                    clearText();
                }

                break;
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String result=null;
        if (data!=null){
            result= data.getStringExtra("result");
        }
        requestCode=requestCode&(0xff);
        LogCat.i("--->","requestCode:"+Integer.toBinaryString(requestCode));
        if(result!=null){
            switch (requestCode){
                case PICK_CATEGORY:
                    tv_pick_category.setText(result);
                    tv_pick_category.setTag(data.getStringExtra("categoryId"));
                    break;
                case PICK_DESCRIPTION:
                    tv_pick_description.setText(result);
                    break;
                case PICK_REGION:
                    tv_pick_region.setText(result);
                    break;

            }
        }
    }
    /**
     * 清空文字
     */
    private void clearText(){
        tv_pick_category.setText(null);
        tv_pick_region.setText(null);
        edit_price.setText(null);

        tv_pick_description.setText(null);
        edit_contact.setText(null);
        edit_title.setText(null);

        edit_book_standard.setText(null);
        edit_contact.setText(null);
        edit_phone.setText(null);
    }
}
