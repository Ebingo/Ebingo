package com.promote.ebingo.publish;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.center.MySupplyActivity;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.ImageUtil;
import com.promote.ebingo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import static com.promote.ebingo.publish.PublishFragment.PICK_CAMERA;
import static com.promote.ebingo.publish.PublishFragment.PICK_CATEGORY;
import static com.promote.ebingo.publish.PublishFragment.PICK_DESCRIPTION;
import static com.promote.ebingo.publish.PublishFragment.PICK_FOR_SUPPLY;
import static com.promote.ebingo.publish.PublishFragment.PICK_IMAGE;
import static com.promote.ebingo.publish.PublishFragment.PICK_REGION;
import static com.promote.ebingo.publish.PublishFragment.PREVIEW;
import static com.promote.ebingo.publish.PublishFragment.TYPE_SUPPLY;

/**
 * Created by acer on 2014/9/2.
 */
public class PublishSupply extends Fragment implements View.OnClickListener {
    private EditText edit_title;
    private EditText edit_contact;
    private EditText edit_phone;
    private EditText edit_price;
    private EditText edit_min_sell_num;

    private TextView tv_pick_description;
    private TextView tv_pick_category;
    private TextView tv_pick_region;
    private TextView tv_pick_image;

    private ImageView picked_image;

    private final String CAMERA_PICTURE_NAME = "publish_upload.png";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.publish_supply, null);
        edit_title = (EditText) v.findViewById(R.id.edit_title);
        edit_contact = (EditText) v.findViewById(R.id.edit_contact);
        edit_phone = (EditText) v.findViewById(R.id.edit_phone);
        edit_price = (EditText) v.findViewById(R.id.edit_price);
        edit_min_sell_num = (EditText) v.findViewById(R.id.edit_min_sell_num);

        tv_pick_category = (TextView) v.findViewById(R.id.tv_pick_category);
        tv_pick_description = (TextView) v.findViewById(R.id.tv_pick_description);
        tv_pick_region = (TextView) v.findViewById(R.id.tv_pick_region);
        tv_pick_image = (TextView) v.findViewById(R.id.tv_pick_image);

        picked_image = (ImageView) v.findViewById(R.id.picked_image);

        tv_pick_category.setOnClickListener(this);
        tv_pick_description.setOnClickListener(this);
        tv_pick_region.setOnClickListener(this);
        tv_pick_image.setOnClickListener(this);
        picked_image.setOnClickListener(this);
        v.findViewById(R.id.btn_publish).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_pick_category: {
                Intent intent = new Intent(getActivity(), PickCategoryActivity.class);
                getActivity().startActivityForResult(intent, PICK_FOR_SUPPLY | PICK_CATEGORY);
                break;
            }
            case R.id.tv_pick_description: {
                Intent intent = new Intent(getActivity(), EditDescription.class);
                intent.putExtra("description", tv_pick_description.getText().toString().trim());
                getActivity().startActivityForResult(intent, PICK_FOR_SUPPLY | PICK_DESCRIPTION);
                break;
            }
            case R.id.tv_pick_region: {
                Intent intent = new Intent(getActivity(), PickRegionActivity.class);
                getActivity().startActivityForResult(intent, PICK_FOR_SUPPLY | PICK_REGION);
                break;
            }
            case R.id.picked_image:
            case R.id.tv_pick_image: {
                showPupWindow();
                break;
            }
            case R.id.btn_publish: {

                EbingoRequestParmater parmater = new EbingoRequestParmater(v.getContext());
                parmater.put("type", TYPE_SUPPLY);
                parmater.put("company_id", Company.getInstance().getCompanyId());

                parmater.put("category_id", tv_pick_category.getTag());
                parmater.put("region_name", tv_pick_region.getText().toString().trim());
                parmater.put("price", edit_price.getText().toString().trim());

                parmater.put("image_url", edit_price.getText().toString().trim());
                parmater.put("description", tv_pick_description.getText().toString().trim());
                parmater.put("title", edit_title.getText().toString().trim());

                parmater.put("min_sell_num", edit_min_sell_num.getText().toString().trim());
                parmater.put("contacts", edit_contact.getText().toString().trim());
                parmater.put("contacts_phone", edit_phone.getText().toString().trim());
                LogCat.i("--->" + parmater);
                startPublish(parmater);
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String result = null;
        if (data != null) {
            result = data.getStringExtra("result");
        }
        requestCode = requestCode & (0xfff);
        LogCat.i("--->", "requestCode:" + Integer.toBinaryString(requestCode));
        switch (requestCode) {
            case PICK_CATEGORY:
                tv_pick_category.setText(result);
                tv_pick_category.setTag(data.getIntExtra("categoryId", 0));
                LogCat.i("--->", "categoryId:" + tv_pick_category.getTag());
                break;
            case PICK_DESCRIPTION:
                tv_pick_description.setText(result);
                break;
            case PICK_REGION:
                tv_pick_region.setText(result);
                break;
            case PICK_IMAGE:
                if (data == null) return;
                Uri uri = data.getData();
                LogCat.i("--->", uri.toString());
                if (uri != null) {
                    toPreviewActivity(uri);
                }
                break;
            case PICK_CAMERA:
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), CAMERA_PICTURE_NAME);
                if (!file.exists()) {
                    if (!file.mkdirs()) {
                        LogCat.e("--->", "create Image File failed!!");
                        return;
                    }
                }
                toPreviewActivity(Uri.fromFile(file));
                break;
            case PREVIEW:
                if (data == null) return;
                uploadImage(data.getData());
                break;
        }
    }

    /**
     * 根据uri上传上传图片
     *
     * @param uri
     */
    private void uploadImage(Uri uri) {
        final Dialog dialog = DialogUtil.waitingDialog(getActivity(), "正在上传图片...");
        ContentResolver resolver = getActivity().getContentResolver();
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(resolver, uri);
            picked_image.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        EbingoRequestParmater parmater = new EbingoRequestParmater(getActivity());
        parmater.put("image", ImageUtil.base64Encode(bitmap));
        HttpUtil.post(HttpConstant.uploadImage, parmater, new JsonHttpResponseHandler("utf-8") {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                ContextUtil.toast(response);
                try {
                    JSONObject result = response.getJSONObject("response");
                    String image_url = result.getString("data");
                    LogCat.i("--->", image_url);
                    if (HttpConstant.CODE_OK.equals(result.getString("code")))
                        tv_pick_image.setTag(image_url);
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
     * 将image战士到PreviewActivity中
     *
     * @param image
     */
    private void toPreviewActivity(Uri image) {
        if (PreviewActivity.isPreviewing() || getActivity() == null) {
            return;
        }
        LogCat.i("--->", "toPreviewActivity:getActivity=" + getActivity());
        Intent intent = new Intent(getActivity(), PreviewActivity.class);
        intent.setData(image);
        getActivity().startActivityForResult(intent, PICK_FOR_SUPPLY | PREVIEW);
    }

    private PopupWindow window;
    View.OnClickListener popupWindowListener = new View.OnClickListener() {//popupWindow点击事件处理
        @Override
        public void onClick(View v) {
            window.dismiss();
            switch (v.getId()) {
                case R.id.btn_upload_normal_pic:
                    showPickDialog("上传普通图片");
                    break;
                case R.id.btn_upload_3d_pic:
                    showPickDialog("上传3D图片");
                    break;
            }
        }
    };

    /**
     * 选择获取图片的方式
     *
     * @param title
     */
    private void showPickDialog(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setItems(new String[]{"相册", "拍照"}, new DialogInterface.OnClickListener() {
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
     * 打开相机拍照
     */
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File out = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), CAMERA_PICTURE_NAME);
        Uri uri = Uri.fromFile(out);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, PICK_FOR_SUPPLY | PICK_CAMERA);
    }


    /**
     * 弹出图片选择窗口
     */
    public void showPupWindow() {
        final View contentView = View.inflate(getActivity(), R.layout.pick_picture_method, null);
        final View content_layout = contentView.findViewById(R.id.content_layout);
        contentView.findViewById(R.id.btn_upload_normal_pic).setOnClickListener(popupWindowListener);
        contentView.findViewById(R.id.btn_upload_3d_pic).setOnClickListener(popupWindowListener);
        contentView.findViewById(R.id.btn_cancel).setOnClickListener(popupWindowListener);
        window = new PopupWindow(contentView, 0, 0, true);
        window.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        window.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        window.setAnimationStyle(android.R.style.Animation_InputMethod);
        window.showAtLocation(getView(), Gravity.BOTTOM, 0, 0);

        contentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP)
                    if (event.getY() < contentView.getHeight() - content_layout.getHeight())
                        window.dismiss();
                return true;
            }
        });
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
        startActivityForResult(i, PICK_FOR_SUPPLY | PICK_IMAGE);
    }


    /**
     * 清空文字
     */
    private void clearText() {
        tv_pick_category.setText(null);
        tv_pick_region.setText(null);
        edit_price.setText(null);

        tv_pick_description.setText(null);
        edit_contact.setText(null);
        edit_title.setText(null);

        edit_min_sell_num.setText(null);
        edit_contact.setText(null);
        edit_phone.setText(null);

        picked_image.setImageBitmap(null);
    }

    public void startPublish(EbingoRequestParmater parmater) {

        final ProgressDialog dialog = DialogUtil.waitingDialog(getActivity());
        HttpUtil.post(HttpConstant.saveInfo, parmater, new JsonHttpResponseHandler("utf-8") {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                ContextUtil.toast(response);
                try {
                    JSONObject result = response.getJSONObject("response");
                    if (HttpConstant.CODE_OK.equals(result.getString("code"))) {
                        Intent intent = new Intent(getActivity(), MySupplyActivity.class);
                        startActivity(intent);
                        clearText();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                ContextUtil.toast(responseString);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dialog.dismiss();
            }
        });
    }

}
