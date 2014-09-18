package com.promote.ebingo.publish;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.BaseActivity;
import com.promote.ebingo.R;
import com.promote.ebingo.center.CallRecordActivity;
import com.promote.ebingo.util.ContextUtil;

/**
 * Created by acer on 2014/9/18.
 */
public class Apply3dActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_apply_3d);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_apply_vip:
                ContextUtil.toast("您的申请已收到，我们会尽快联系您，请您耐心等待");
                break;
            case R.id.btn_consult:
                final String number = getString(R.string.customer_service_number);
                DialogInterface.OnClickListener l = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
                                startActivity(intent);
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("拨打电话")
                        .setMessage("是否拨打" + number + "?")
                        .setPositiveButton("拨打", l)
                        .setNegativeButton("取消", l)
                        .show();
                break;
        }
        super.onClick(v);
    }
}
