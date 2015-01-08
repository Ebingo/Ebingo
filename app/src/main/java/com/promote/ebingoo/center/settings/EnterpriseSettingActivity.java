package com.promote.ebingoo.center.settings;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.jch.lib.util.VaildUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingoo.BaseActivity;
import com.promote.ebingoo.R;
import com.promote.ebingoo.application.HttpConstant;
import com.promote.ebingoo.bean.Company;
import com.promote.ebingoo.bean.RegionBeen;
import com.promote.ebingoo.impl.EbingoHandler;
import com.promote.ebingoo.impl.EbingoRequestParmater;
import com.promote.ebingoo.impl.ImageDownloadTask;
import com.promote.ebingoo.publish.PhotoAlbumActivity;
import com.promote.ebingoo.publish.login.LoginManager;
import com.promote.ebingoo.util.ContextUtil;
import com.promote.ebingoo.util.Dimension;
import com.promote.ebingoo.util.FileUtil;
import com.promote.ebingoo.util.ImageUtil;
import com.promote.ebingoo.util.JsonUtil;
import com.promote.ebingoo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 企业设置
 */
public class EnterpriseSettingActivity extends BaseActivity {

    private static final int PICK_IMAGE = 1;
    private ImageView image_enterprise;
    private EditText edit_enterprise_name;
    private EditText edit_enterprise_address;
    private EditText edit_enterprise_phone;
    private EditText edit_enterprise_site;
    private EditText edit_enterprise_email;
    private TextView tv_pick_province;
    private TextView tv_pick_city;
    private TextView tv_error;
    private ArrayList<RegionBeen> provinceList = new ArrayList<RegionBeen>();//省份列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enterprise_setting);
        image_enterprise = findImage(R.id.image_enterprise);


        edit_enterprise_name = findEdit(R.id.edit_enterprise_name);
        edit_enterprise_address = findEdit(R.id.edit_enterprise_address);
        edit_enterprise_phone = findEdit(R.id.edit_enterprise_phone);
        edit_enterprise_site = findEdit(R.id.edit_enterprise_site);
        edit_enterprise_email = findEdit(R.id.edit_enterprise_email);

        tv_pick_province = (TextView) findViewById(R.id.pick_province);
        tv_pick_city = (TextView) findViewById(R.id.pick_city);
        tv_error = (TextView) findViewById(R.id.tv_error);
        tv_error.setVisibility(View.GONE);
        tv_pick_province.setOnClickListener(this);
        tv_pick_city.setOnClickListener(this);
        image_enterprise.setOnClickListener(this);
        initData();
    }

    private void initData() {
        Company company = Company.getInstance();
        if (!TextUtils.isEmpty(company.getName())) edit_enterprise_name.setText(company.getName());
        edit_enterprise_address.setText(company.getAddress());
        edit_enterprise_site.setText(company.getWebsite());
        edit_enterprise_email.setText(company.getEmail());
        edit_enterprise_phone.setText(company.getCompanyTel());
        image_enterprise.setContentDescription(company.getImage());

        tv_pick_province.setText(company.getProvince_name());
        tv_pick_province.setTag(company.getProvince_id());
        tv_pick_city.setText(company.getCity_name());
        tv_pick_city.setTag(company.getCity_id());

        setHeadImage(Company.getInstance().getImageUri());
    }

    /**
     * 设置头像
     *
     * @param uri 图片的uri
     */
    public void setHeadImage(Uri uri) {

        ImageDownloadTask task = new ImageDownloadTask() {
            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null)
                    image_enterprise.setImageBitmap(ImageUtil.roundBitmap(bitmap, (int) Dimension.dp(48)));
            }
        };

        if (uri != null) {//本地有头像
            task.loadBY(uri);
        } else {
            final String imageUrl = Company.getInstance().getImage();
            if (TextUtils.isEmpty(imageUrl)) {//没有头像URL
                LogCat.e("--->", "setHeadImage uriError uri=" + uri);
                image_enterprise.setImageResource(R.drawable.center_head);
            } else {//有远程头像URL
                task.loadBy(imageUrl);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_commit:
                EnterPriseInfo info = getInputInfo();
                tv_error.setVisibility(View.GONE);
                if (info.check(getApplicationContext())) {
                    commit(info);
                }
                break;
            case R.id.image_enterprise:
                Intent intent = new Intent(this, PhotoAlbumActivity.class);
                intent.putExtra(PhotoAlbumActivity.EXTRA_CAMERA_OUTPUT_PATH, ImageUtil.getImageTempFile(ImageUtil.IMAGE_TEMP_COMPANY_IMAGE).getAbsolutePath());
                intent.putExtra(PhotoAlbumActivity.EXTRA_CAMERA_OUTPUT_WIDTH, 110);
                intent.putExtra(PhotoAlbumActivity.EXTRA_CAMERA_OUTPUT_HEIGHT, 110);
                startActivityForResult(intent, PICK_IMAGE);
                break;
            case R.id.pick_province: {
                getProvinceList();
                break;
            }
            case R.id.pick_city: {
                Integer province_id = (Integer) tv_pick_province.getTag();
                if (province_id == null) {
                    ContextUtil.toast("请先选择省份！");
                    return;
                }
                getCityList(province_id);
                break;
            }
            default:
                super.onClick(v);
        }
    }

    private void getProvinceList() {
        provinceList = (ArrayList<RegionBeen>) ContextUtil.read(FileUtil.FILE_PROVINCE_LIST);
        if (provinceList != null) {
            showProvinceDialog();
            return;
        } else {
            provinceList = new ArrayList<RegionBeen>();
        }
        final Dialog dialog = DialogUtil.waitingDialog(this, "正在加载省份列表...");

        HttpUtil.post(HttpConstant.getProvinceList, new EbingoRequestParmater(this), new EbingoHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    JSONArray array = response.getJSONArray("data");
                    JsonUtil.getArray(array, RegionBeen.class, provinceList);
                    showProvinceDialog();
                    ContextUtil.saveCache(FileUtil.FILE_PROVINCE_LIST, provinceList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(int statusCode, String msg) {
                LogCat.i("--->", msg + "");
            }

            @Override
            public void onFinish() {
                dialog.dismiss();
            }
        });
    }

    private void showProvinceDialog() {
        int length = provinceList.size();
        final String[] provinces = new String[length];
        for (int i = 0; i < length; i++) {
            provinces[i] = provinceList.get(i).getName();
        }
        ListDialog dialog = ListDialog.newInstance(provinces, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!provinces[which].equals(tv_pick_province.getText().toString())) {
                    tv_pick_city.setText(null);
                    tv_pick_province.setText(provinces[which]);
                    tv_pick_province.setTag(provinceList.get(which).getId());
                }
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    /**
     * 获取城市列表
     *
     * @param province_id
     */
    private void getCityList(final int province_id) {

        ArrayList<RegionBeen> cityList = (ArrayList<RegionBeen>) ContextUtil.read(FileUtil.FILE_CITY_LIST + province_id);
        if (cityList != null) {
            showCityDialog(cityList);
            return;
        }
        EbingoRequestParmater params = new EbingoRequestParmater(this);
        params.put("province", province_id);
        final Dialog dialog = DialogUtil.waitingDialog(this, "正在加载城市列表...");
        HttpUtil.post(HttpConstant.getCityList, params, new EbingoHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    JSONArray array = response.getJSONArray("data");
                    final ArrayList<RegionBeen> cityList = new ArrayList<RegionBeen>();
                    JsonUtil.getArray(array, RegionBeen.class, cityList);
                    ContextUtil.saveCache(FileUtil.FILE_CITY_LIST + province_id, cityList);
                    showCityDialog(cityList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(int statusCode, String msg) {

            }

            @Override
            public void onFinish() {
                dialog.dismiss();
            }
        });
    }

    private void showCityDialog(final ArrayList<RegionBeen> cityList) {
        int length = cityList.size();
        final String[] cities = new String[length];
        for (int i = 0; i < length; i++) {
            cities[i] = cityList.get(i).getName();
        }
        ListDialog.newInstance(cities, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RegionBeen city = cityList.get(which);
                tv_pick_city.setText(city.getName());
                tv_pick_city.setTag(city.getId());
            }
        }).show(getSupportFragmentManager(), null);
    }

    private String getString(TextView tv) {
        return tv.getText().toString();
    }

    /**
     * 点击提交按钮执行事件
     */
    private void commit(final EnterPriseInfo info) {

        final Dialog dialog = DialogUtil.waitingDialog(this, "正在更新数据...");
        HttpUtil.post(HttpConstant.updateCompanyInfo, info.getParams(this), new EbingoHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                Company company = Company.getInstance();
                info.apply(company);
                ContextUtil.toast("修改成功！");
                FileUtil.saveFile(getApplicationContext(), FileUtil.FILE_COMPANY, company);
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFail(int statusCode, String msg) {
                ContextUtil.toast(msg);
            }

            @Override
            public void onFinish() {
                dialog.dismiss();
            }
        });
    }

    /**
     * 获取用户填入的信息
     */
    private EnterPriseInfo getInputInfo() {
        final EnterPriseInfo info = new EnterPriseInfo();

        info.company_id = Company.getInstance().getCompanyId();
        info.image = image_enterprise.getContentDescription() + "";
        info.name = getString(edit_enterprise_name);
        info.company_tel = getString(edit_enterprise_phone);
        info.address = getString(edit_enterprise_address);
        info.website = getString(edit_enterprise_site);
        info.email = getString(edit_enterprise_email);
        info.province = getString(tv_pick_province);
        info.city = getString(tv_pick_city);
        info.province_id = (Integer) tv_pick_province.getTag();
        info.city_id = (Integer) tv_pick_city.getTag();
        return info;
    }


    /**
     * 根据uri上传上传图片
     *
     * @param uri
     */
    private void uploadImage(Bitmap uri) {
        if (uri == null) return;
        final Dialog dialog = DialogUtil.waitingDialog(this, "正在上传图片...");
        Bitmap bitmap = uri;
        try {
//
            Uri avatar=ImageUtil.saveBitmap(bitmap,"company"+System.currentTimeMillis());
            image_enterprise.setImageBitmap(ImageUtil.roundBitmap(bitmap, (int) Dimension.dp(48)));
            Company.getInstance().setImageUri(avatar);
        } catch (Exception e) {
            e.printStackTrace();
        }
        EbingoRequestParmater parmater = new EbingoRequestParmater(this);
        parmater.put("image", ImageUtil.base64Encode(bitmap));

        HttpUtil.post(HttpConstant.uploadImage, parmater, new JsonHttpResponseHandler("utf-8") {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                LogCat.i("--->", response + "");
                try {
                    JSONObject result = response.getJSONObject("response");
                    if (HttpConstant.CODE_OK.equals(result.getString("code")))
                        image_enterprise.setContentDescription(result.getString("data"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                dialog.dismiss();
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogCat.i("--->", "onActivityResult " + " " + requestCode + " " + resultCode + " " + data);
        if (resultCode != RESULT_OK) return;

        switch (requestCode) {

            case PICK_IMAGE:
                uploadImage((Bitmap)data.getParcelableExtra("data"));
                break;

        }
    }

    void showError(String msg) {
        tv_error.setText(msg);
        tv_error.setVisibility(View.VISIBLE);
    }

    class EnterPriseInfo {
        Integer company_id;
        String image;
        String name;
        String company_tel;
        Integer province_id;
        Integer city_id;
        String province;
        String city;
        String address;
        String website;
        String email;

        EbingoRequestParmater getParams(Context context) {
            EbingoRequestParmater parmater = new EbingoRequestParmater(context);
            parmater.put("company_id", company_id);
            parmater.put("image", image);
            parmater.put("name", name);
            parmater.put("company_tel", company_tel);
            parmater.put("province", province_id);//此处传的是id，而不是省名
            parmater.put("city", city_id);//传id，而不是名称
            parmater.put("address", address);
            parmater.put("website", website);
            parmater.put("email", email);
            return parmater;
        }

        void apply(Company company) {
            company.setImage(image);
            company.setName(name);
            company.setCompanyTel(company_tel);
            company.setProvince_id(province_id);
            company.setCity_id(city_id);
            company.setProvince_name(province);
            company.setCity_name(city);
            company.setAddress(address);
            company.setWebsite(website);
            company.setEmail(email);
        }

        boolean check(Context context) {
            if (TextUtils.isEmpty(name)) {
                showError(getString(R.string.company_name_null));
                return false;
            }
            if (!LoginManager.isMobile(company_tel) && !LoginManager.isPhone(company_tel)) {
                showError(getString(R.string.tel_format_error));
                return false;
            }

            String msg = VaildUtil.validEmail(email);
            if (!TextUtils.isEmpty(msg)) {
                showError(msg);
                return false;
            }
            return true;
        }
    }
}
