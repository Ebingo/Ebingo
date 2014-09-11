package com.promote.ebingo.publish.login;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.publish.PreviewActivity;
import com.promote.ebingo.publish.PublishBaseActivity;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.ImageUtil;
import com.promote.ebingo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import static com.promote.ebingo.publish.login.RegisterActivity.REQUEST_CODE;

/**
 * Created by acer on 2014/9/9.
 */
public class EnterpriseSettingActivity extends PublishBaseActivity {

    private static final int PICK_IMAGE = 1;
    private static final int PICK_CAMERA = 2;
    private static final int PREVIEW = 3;
    private ImageView image_enterprise;
    private EditText edit_enterprise_name;
    private EditText edit_enterprise_address;
    private EditText edit_enterprise_phone;
    private EditText edit_enterprise_site;
    private EditText edit_enterprise_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enterprise_setting);
        setBackTitle("企业设置");

        image_enterprise = findImage(R.id.image_enterprise);

        edit_enterprise_name = findEdit(R.id.edit_enterprise_name);
        edit_enterprise_address = findEdit(R.id.edit_enterprise_address);
        edit_enterprise_phone = findEdit(R.id.edit_enterprise_phone);
        edit_enterprise_site = findEdit(R.id.edit_enterprise_site);
        edit_enterprise_email = findEdit(R.id.edit_enterprise_email);

        image_enterprise.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_done:
                EbingoRequestParmater parmater = new EbingoRequestParmater(this);
                final Integer company_id=6;
                final String image_url = image_enterprise.getTag() + "";
                final String name = edit_enterprise_name.getText().toString().trim();
                final String company_tel = edit_enterprise_phone.getText().toString().trim();
                final String region = edit_enterprise_address.getText().toString().trim();
                final String website = edit_enterprise_site.getText().toString().trim();
                final String email = edit_enterprise_email.getText().toString().trim();

                parmater.put("company_id", company_id);
                parmater.put("image", image_url);
                parmater.put("name", name);
                parmater.put("company_tel", company_tel);
                parmater.put("region", region);
                parmater.put("website", website);
                parmater.put("email", email);
                LogCat.i("--->" + parmater);
                HttpUtil.post(HttpConstant.updateCompanyInfo, parmater, new JsonHttpResponseHandler("utf-8") {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            JSONObject result=response.getJSONObject("response");
                            if(HttpConstant.CODE_OK.equals(result.getString("code"))){
                                Company company=Company.getInstance();
                                company.setCompanyId(company_id);
                                company.setImage(image_url);
                                company.setName(name);
                                company.setCompanyTel(company_tel);
                                company.setRegion(region);
                                company.setWebsite(website);
                                company.setEmail(email);
                                setResult(RESULT_OK,new Intent());
                                finish();
                            }else{
                                ContextUtil.toast(response);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });
                break;
            case R.id.image_enterprise:
                showPickDialog();
                break;
            default:
                super.onClick(v);
        }

    }

    /**
     * 根据uri上传上传图片
     *
     * @param uri
     */
    private void uploadImage(Uri uri) {
        final Dialog dialog = DialogUtil.waitingDialog(this, "正在上传图片...");
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            image_enterprise.setImageBitmap(bitmap);
            Company.getInstance().setImageUri(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        EbingoRequestParmater parmater = new EbingoRequestParmater(this);
        parmater.put("image", ImageUtil.base64Encode(bitmap));
        HttpUtil.post(HttpConstant.uploadImage, parmater, new JsonHttpResponseHandler("utf-8") {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                ContextUtil.toast(response);
                try {
                    JSONObject result = response.getJSONObject("response");
                    if (HttpConstant.CODE_OK.equals(result.getString("code")))
                        image_enterprise.setTag(result.getString("data"));
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

    /**
     * 选择获取图片的方式
     */
    private void showPickDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("企业形象").setItems(new String[]{"相册", "拍照"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (which) {
                    case 0:
                        goToAlbum();
                        break;
                    case 1:
                        openCamera();
                        break;
                }
            }
        }).create().show();
    }

    /**
     * 从相册中选择一张图片
     */
    private void goToAlbum() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "EbingooPics");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                LogCat.d("--->", "failed to create directory");
                return;
            }
        }
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, PICK_IMAGE);
    }

    /**
     * 打开相机拍照
     */
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getImageCacheUri());
        startActivityForResult(intent, PICK_CAMERA);
    }

    private Uri getImageCacheUri() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "enterprise_image");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                LogCat.e("--->", "create Image File failed!!");
            }
        }
        return Uri.fromFile(file);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case PICK_CAMERA:
                toPreviewActivity(getImageCacheUri());
                break;
            case PICK_IMAGE:
                if (data == null) return;
                Uri uri = data.getData();
                LogCat.i("--->", uri.toString());
                if (uri != null) {
                    toPreviewActivity(uri);
                }
                break;
            case PREVIEW:
                if (data == null) return;
                uploadImage(data.getData());
                break;
        }
    }

    /**
     * 将image战士到PreviewActivity中
     *
     * @param image
     */
    private void toPreviewActivity(Uri image) {
        if (PreviewActivity.isPreviewing()) {
            return;
        }
        Intent intent = new Intent(this, PreviewActivity.class);
        intent.setData(image);
        startActivityForResult(intent, PREVIEW);
    }

}
