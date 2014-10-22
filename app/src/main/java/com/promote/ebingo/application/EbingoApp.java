package com.promote.ebingo.application;

import android.app.Application;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.view.WindowManager;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.promote.ebingo.R;
import com.promote.ebingo.center.settings.VersionManager;
import com.promote.ebingo.publish.EbingoDialog;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.LogCat;

import java.io.File;

/**
 * Created by Administrator on 2014/8/21.
 */
public class EbingoApp extends Application {

    public static final String EBINGO_COMPANY_DATA = "ebingo_user_data_prefer";
    public static final String COMPANY_NAME = "company_name";
    public static final String COMPANY_PWD = "company_pwd";
    public static final String PREF_DELETE = "delete";
    /**
     * 当前公司本地信息。用于默认登录 *
     */
    public SharedPreferences mCurCompanyData = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mCurCompanyData = getApplicationContext().getSharedPreferences(EBINGO_COMPANY_DATA, MODE_PRIVATE);
        initImageLoaderConfiguration();
        ContextUtil.init(getApplicationContext());
        deleteApkIfExist();
    }

    private void deleteApkIfExist() {
        String version = null;
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        final File apk = VersionManager.getDownloadFile(getApplicationContext(), version);
        final float fileSize = apk.length() / (1024 * 1024f);
        final SharedPreferences sp = getSharedPreferences(PREF_DELETE, MODE_PRIVATE);

        if (apk.exists()&&sp.getBoolean(apk.getName(), true)) {
            EbingoDialog dialog = new EbingoDialog(getApplicationContext());
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            dialog.setTitle(R.string.warn);
            dialog.setMessage(getString(R.string.delete_apk_notice, apk.getName(), fileSize));
            dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    apk.delete();
                    ContextUtil.toast(getString(R.string.deleted_apk_notice, apk.getName(), fileSize));
                }
            });
            dialog.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //如果用户点击取消，下次不再提醒
                    sp.edit().putBoolean(apk.getName(),false).commit();
                }
            });
            dialog.show();
        }
    }

    /**
     * 配置imgloader.
     */
    public void initImageLoaderConfiguration() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(1 * 1024 * 1024))
                .diskCacheSize(50 * 1024 * 1024)
                .threadPoolSize(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCache(
                        new UnlimitedDiscCache(new File(Constant.IMG_DIR
                        ))
                )
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs().build();
        ImageLoader.getInstance().init(config);
    }

    /**
     * 将当前公司保名称存到本地。
     *
     * @param name
     */
    public void saveCurCompanyName(String name) {
        SharedPreferences.Editor editor = mCurCompanyData.edit();
        editor.putString(COMPANY_NAME, name);
        editor.commit();
    }

    /**
     * 将当前公司密码保存到本地。
     *
     * @param pwd 已经过MD5加密。
     */
    public void saveCurCompanyPwd(String pwd) {
        SharedPreferences.Editor editor = mCurCompanyData.edit();
        editor.putString(COMPANY_PWD, pwd);
        editor.commit();
    }

    /**
     * @return
     */
    public String getCurCompanyName() {

        String name = mCurCompanyData.getString(COMPANY_NAME, null);
        return name;
    }

    /**
     * @return md5加密后
     */
    public String getCurCompanyPwd() {

        return mCurCompanyData.getString(COMPANY_PWD, null);
    }

    /**
     * 清除current company.
     */
    public void cleanCurComany() {
        mCurCompanyData.edit().clear().commit();
    }

}
