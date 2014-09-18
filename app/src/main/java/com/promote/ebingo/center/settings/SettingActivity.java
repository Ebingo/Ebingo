package com.promote.ebingo.center.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.promote.ebingo.BaseActivity;
import com.promote.ebingo.R;
import com.promote.ebingo.application.EbingoApp;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.publish.login.LoginActivity;
import com.promote.ebingo.util.ContextUtil;

/**
 * Created by acer on 2014/9/16.
 */
public class SettingActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.company_setting:
                toActivity(EnterpriseSettingActivity.class);
                break;
            case R.id.about_us:
                toActivity(AboutUsActivity.class);
                break;
            case R.id.check_update:
                new AlertDialog.Builder(SettingActivity.this)
                        .setMessage("已经是最新版本了！")
                        .setPositiveButton("我知道了",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                break;
            case R.id.clear_cache:

                ContextUtil.toast("清除成功！");
                break;
            case R.id.suggestion:
                toActivity(SuggestionActivity.class);
                break;
            case R.id.logout:
                Company.getInstance().clearCompany();
//                ((EbingoApp)getApplicationContext()).cleanCurComany();
                toActivity(LoginActivity.class);
                finish();
                break;
            default:super.onClick(v);
        }
    }
}
