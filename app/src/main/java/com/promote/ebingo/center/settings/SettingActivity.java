package com.promote.ebingo.center.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.jch.lib.util.HttpUtil;
import com.jch.lib.util.ImageManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.promote.ebingo.BaseActivity;
import com.promote.ebingo.R;
import com.promote.ebingo.application.EbingoApp;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.impl.EbingoHandler;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.publish.login.LoginActivity;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.FileUtil;
import com.promote.ebingo.util.LogCat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

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
            case R.id.check_update: {
                requestVersionCode(getApplicationContext());
                break;
            }
            case R.id.clear_cache: {
                FileUtil.clearCache(getApplicationContext());
                ImageLoader.getInstance().clearDiskCache();
                ImageLoader.getInstance().clearMemoryCache();
                ContextUtil.toast(R.string.clear_success);
                break;
            }
            case R.id.suggestion:
                toActivity(SuggestionActivity.class);
                break;
            case R.id.logout:
                Company.clearInstance();
                ContextUtil.cleanCurComany();
                FileUtil.deleteFile(this, FileUtil.FILE_COMPANY);
                toActivity(LoginActivity.class);
                finish();
                break;
            default:
                super.onClick(v);
        }
    }

    private void requestVersionCode(final Context context) {
        EbingoRequestParmater param=new EbingoRequestParmater(context);
        HttpUtil.post("",param,new EbingoHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                try {
                    if (response.getInt("version")>EbingoApp.localeVersion){
                        builder.setMessage(response.getString("msg"));
                        builder.setPositiveButton(R.string.update_right_now,new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                    }else{
                        builder.setMessage(R.string.already_new_version);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(int statusCode, String msg) {

            }

            @Override
            public void onFinish() {

            }
        });
    }
}
