package com.promote.ebingo.publish;


import android.app.Activity;
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

/**
 * Created by acer on 2014/9/2.
 */
public class PublishSupplyFragment extends Fragment implements View.OnClickListener{
    private final int PICK_CATEGORY=1;
    private final int PICK_DESCRIPTION=2;
    private final int PICK_LABEL=3;

    TextView tv_category;
    TextView tv_description;
    TextView tv_label;

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
        tv_label =(TextView)view.findViewById(R.id.tv_label);
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
                Intent intent = new Intent(getActivity(), ChooseCategoryActivity.class);
                startActivityForResult(intent, PICK_CATEGORY);
                break;
            }
            case R.id.pick_description: {
                Intent intent = new Intent(getActivity(), ChooseCategoryActivity.class);
                startActivityForResult(intent,PICK_DESCRIPTION);
                break;
            }
            case R.id.pick_label:{
                Intent intent = new Intent(getActivity(), ChooseCategoryActivity.class);
                startActivityForResult(intent,PICK_LABEL);
                break;}

            case R.id.btn_publish:{
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String result = data.getStringExtra("result");
        if(resultCode== Activity.RESULT_OK&&result!=null){
            switch (requestCode){
                case PICK_CATEGORY:
                    tv_category.setText(result);
                    break;
                case PICK_DESCRIPTION:
                    tv_description.setText(result);
                    break;
                case PICK_LABEL:
                    tv_label.setText(result);
                    break;

            }
        }
    }
}
