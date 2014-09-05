package com.promote.ebingo.publish;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.promote.ebingo.R;

/**
 * Created by acer on 2014/9/4.
 */
public class EditDescription extends Activity implements View.OnClickListener{
    private EditText edit_description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_description);
        CheckBox a;
        findViewById(R.id.common_back_btn).setOnClickListener(this);
        ((TextView)findViewById(R.id.common_title_tv)).setText("产品描述");
        edit_description= ((EditText)findViewById(R.id.edit_description));
        edit_description.setText(getIntent().getStringExtra("description"));
        edit_description.setSelection(edit_description.length());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.common_back_btn:
                finish();
                break;
            case R.id.commit:
                Intent result=new Intent();
                result.putExtra("result",edit_description.getText().toString().trim());
                setResult(RESULT_OK,result);
                finish();
                break;

        }
    }
}
