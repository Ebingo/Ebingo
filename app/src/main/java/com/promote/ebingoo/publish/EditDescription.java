package com.promote.ebingoo.publish;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.promote.ebingoo.BaseActivity;
import com.promote.ebingoo.R;

/**
 * Created by acer on 2014/9/4.
 */
public class EditDescription extends BaseActivity implements View.OnClickListener {
    public static final String CONTENT = "description";
    private EditText edit_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_description);
        findViewById(R.id.commit_title_done).setOnClickListener(this);
        edit_description = ((EditText) findViewById(R.id.edit_description));
        edit_description.setText(getIntent().getStringExtra(CONTENT));
        edit_description.setSelection(edit_description.length());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_back_btn:
                finish();
                break;
            case R.id.commit_title_done:
                Intent result = new Intent();
                result.putExtra("result", edit_description.getText().toString().trim());
                setResult(RESULT_OK, result);
                finish();
                break;

            default:
                super.onClick(v);

        }
    }
}
