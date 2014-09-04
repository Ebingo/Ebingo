package com.promote.ebingo.publish;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.promote.ebingo.R;

/**
 * Created by acer on 2014/9/4.
 */
public class EditDescription extends Activity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_description);
        findViewById(R.id.common_back_btn).setOnClickListener(this);
        ((TextView)findViewById(R.id.common_title_tv)).setText("产品描述");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.common_back_btn:
                finish();
                break;
            case R.id.commit:
                String description= ((EditText)findViewById(R.id.edit_description)).getText().toString();
                Intent result=new Intent();
                result.putExtra("des",description);
                setResult(RESULT_OK,result);
                finish();
                break;

        }
    }
}
