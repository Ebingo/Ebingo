package com.promote.ebingoo.center.settings;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.promote.ebingoo.BaseActivity;
import com.promote.ebingoo.R;
import com.promote.ebingoo.bean.Company;
import com.promote.ebingoo.publish.EbingoDialog;
import com.promote.ebingoo.publish.login.LoginActivity;
import com.promote.ebingoo.util.ContextUtil;
import com.promote.ebingoo.util.FileUtil;

/**
 * Created by acer on 2014/9/16.
 */
public class SettingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            TextView tv_version = (TextView) findViewById(R.id.tv_version_name);
            tv_version.setText(getString(R.string.current_version, versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.company_setting:
                if (Company.getInstance().getCompanyId()==null){
                    toActivity(LoginActivity.class);
                    finish();
                }else{
                    toActivity(EnterpriseSettingActivity.class);
                }
                break;
            case R.id.about_us:
                toActivity(AboutUsActivity.class);
                break;
            case R.id.check_update: {
                VersionManager.requestVersionCode(SettingActivity.this, true);
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
            case R.id.logout: {
                EbingoDialog dialog = new EbingoDialog(this);
                dialog.setTitle(R.string.warn);
                dialog.setMessage(R.string.confirm_logout);
                DialogInterface.OnClickListener l = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            Company.clearInstance();
                            ContextUtil.cleanCurComany();
                            FileUtil.deleteFile(SettingActivity.this, FileUtil.FILE_COMPANY);
                            toActivity(LoginActivity.class);
                            finish();
                        }
                    }
                };
                dialog.setPositiveButton(R.string.yes, l);
                dialog.setNegativeButton(R.string.cancel, l);
                dialog.show();
                break;
            }
            default:
                super.onClick(v);
        }
    }


}
