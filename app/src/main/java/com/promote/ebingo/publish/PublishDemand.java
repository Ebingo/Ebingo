package com.promote.ebingo.publish;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.promote.ebingo.R;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.impl.EbingoRequestParmater;

import static com.promote.ebingo.publish.PublishFragment.*;

/**
 * 发布求购
 * Created by acer on 2014/9/2.
 */
public class PublishDemand extends Fragment implements View.OnClickListener{

    TextView tv_category;
    TextView tv_description;
    TextView tv_tags;

    EditText edit_title;
    EditText edit_contact;
    EditText edit_mobile;

    Button publish;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_publish_supply, container,false);
        init(view);
        return view;
    }

    private void init(View view) {
        tv_category =(TextView)view.findViewById(R.id.tv_category);
        tv_tags =(TextView)view.findViewById(R.id.tv_tags);
        tv_description =(TextView)view.findViewById(R.id.tv_description);

        edit_title=(EditText)view.findViewById(R.id.edit_title);
        edit_contact=(EditText)view.findViewById(R.id.edit_contact);
        edit_mobile=(EditText)view.findViewById(R.id.edit_mobile);

        publish=(Button)view.findViewById(R.id.btn_publish);

        view.findViewById(R.id.pick_category).setOnClickListener(this);
        view.findViewById(R.id.pick_description).setOnClickListener(this);
        view.findViewById(R.id.pick_label).setOnClickListener(this);

        publish.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pick_category: {
                Intent intent = new Intent(getActivity(), PickCategoryActivity.class);
                startActivityForResult(intent, PICK_FOR_DEMAND | PICK_CATEGORY);
                break;
            }
            case R.id.pick_description: {
                Intent intent = new Intent(getActivity(), EditDescription.class);
                intent.putExtra("description",tv_description.getText().toString().trim());
                startActivityForResult(intent,PICK_FOR_DEMAND|PICK_DESCRIPTION);
                break;
            }
            case R.id.pick_label:{
                Intent intent = new Intent(getActivity(), PickCategoryActivity.class);
                startActivityForResult(intent,PICK_FOR_DEMAND| PICK_TAGS);
                break;
            }

            case R.id.btn_publish:{

                Fragment parent=getParentFragment();
                if (parent instanceof  PublishFragment){
                    EbingoRequestParmater parmater=new EbingoRequestParmater(v.getContext());
                    parmater.put("type",TYPE_DEMAND);
                    parmater.put("company_id", Company.getInstance().getCompanyId());
                    parmater.put("category_id",tv_category.getTag());
                    parmater.put("region_name", Company.getInstance().getRegion());
                    parmater.put("title",edit_title.getText().toString().trim());
                    parmater.put("tags",tv_tags.getText().toString().trim());
                    parmater.put("description",tv_description.getText().toString().trim());
                    parmater.put("contacts",edit_contact.getText().toString().trim());
                    parmater.put("contacts_phone",edit_mobile.getText().toString().trim());
                    ((PublishFragment)parent).startPublish(parmater);
                }
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String result = null;
        if (data!=null){
            result=data.getStringExtra("result");
        }
        requestCode=requestCode&(0xff);
        if(result!=null){
            switch (requestCode){
                case PICK_CATEGORY:
                    tv_category.setText(result);
                    tv_category.setTag(data.getIntExtra("categoryId",0));
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
}
