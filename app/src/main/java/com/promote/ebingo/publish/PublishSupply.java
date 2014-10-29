package com.promote.ebingo.publish;


import android.app.Activity;
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
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.jch.lib.util.ImageManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.BaseListActivity;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.bean.CompanyVipInfo;
import com.promote.ebingo.bean.DetailInfoBean;
import com.promote.ebingo.center.MySupplyActivity;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.publish.login.LoginManager;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.FileUtil;
import com.promote.ebingo.util.ImageUtil;
import com.promote.ebingo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import static com.promote.ebingo.publish.PublishFragment.APPLY_3D;
import static com.promote.ebingo.publish.PublishFragment.CROP;
import static com.promote.ebingo.publish.PublishFragment.PICK_CAMERA;
import static com.promote.ebingo.publish.PublishFragment.PICK_CATEGORY;
import static com.promote.ebingo.publish.PublishFragment.PICK_DESCRIPTION;
import static com.promote.ebingo.publish.PublishFragment.PICK_FOR_SUPPLY;
import static com.promote.ebingo.publish.PublishFragment.PICK_IMAGE;
import static com.promote.ebingo.publish.PublishFragment.PICK_REGION;
import static com.promote.ebingo.publish.PublishFragment.PREVIEW;
import static com.promote.ebingo.publish.PublishFragment.TYPE_SUPPLY;
import static com.promote.ebingo.publish.PublishFragment.PublishController;
/**
 * Created by acer on 2014/9/2.
 */
public class PublishSupply extends Fragment implements View.OnClickListener, PublishEditActivity.EditInfo {
    private EditText edit_title;
    private EditText edit_contact;
    private EditText edit_phone;
    private EditText edit_price;
    private EditText edit_min_sell_num;
    private EditText edit_unit;

    private TextView tv_pick_description;
    private TextView tv_pick_category;
    private TextView tv_pick_region;
    private TextView tv_pick_image;

    private ImageView picked_image;
    private CheckBox upload_3d_cb;

    private DetailInfoBean mDetailInfo;
    private TextView tv_3d_notice;
    private String info_id;
    private final String CAMERA_PICTURE_NAME = "supply_image.png";
    PublishController controller = new PublishController();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.publish_supply, null);
        edit_title = (EditText) v.findViewById(R.id.edit_title);
        edit_contact = (EditText) v.findViewById(R.id.edit_contact);
        edit_phone = (EditText) v.findViewById(R.id.edit_phone);
        edit_price = (EditText) v.findViewById(R.id.edit_price);
        edit_min_sell_num = (EditText) v.findViewById(R.id.edit_min_sell_num);
        edit_unit = (EditText) v.findViewById(R.id.edit_unit);

        tv_pick_category = (TextView) v.findViewById(R.id.tv_pick_category);
        tv_pick_description = (TextView) v.findViewById(R.id.tv_pick_description);
        tv_pick_region = (TextView) v.findViewById(R.id.tv_pick_region);
        tv_pick_image = (TextView) v.findViewById(R.id.tv_pick_image);
        tv_3d_notice = (TextView) v.findViewById(R.id.tv_3d_notice);

        picked_image = (ImageView) v.findViewById(R.id.picked_image);
        upload_3d_cb = (CheckBox) v.findViewById(R.id.upload_3d_cb);

        tv_pick_category.setOnClickListener(this);
        tv_pick_description.setOnClickListener(this);
        tv_pick_region.setOnClickListener(this);
        tv_pick_image.setOnClickListener(this);

        picked_image.setOnClickListener(this);
        upload_3d_cb.setOnClickListener(this);
        v.findViewById(R.id.btn_publish).setOnClickListener(this);
        if (getArguments() == null || !getArguments().getBoolean(PublishEditActivity.EDIT, false)) {//如果是编辑，就不加载模板
            mDetailInfo = (DetailInfoBean) FileUtil.readCache(getActivity(), FileUtil.PUBLISH_SUPPLY_MODULE);
        }
        edit(mDetailInfo);
        LogCat.i("--->", "onCreateView edit");
        return v;
    }

    private void show3dNotice(boolean show) {
        if (!show) {
            tv_3d_notice.setVisibility(View.GONE);
            return;
        }
        String prefix = "上传全景图片需要大量素材，发布成功后请等待我们与您联系，或现在";
        String link = "联系我们";
        String content = prefix + link;
        SpannableString ss = new SpannableString(content);
        ss.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                callService();
            }
        }, prefix.length(), content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_3d_notice.setVisibility(View.VISIBLE);
        tv_3d_notice.setText(ss);
        tv_3d_notice.setMovementMethod(LinkMovementMethod.getInstance());

    }

    /**
     * 拨打客服电话
     */
    private void callService() {
        final String mNumber = getString(R.string.customer_service_number);
        DialogInterface.OnClickListener l = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mNumber));
                        getActivity().startActivity(intent);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };
        EbingoDialog dialog = new EbingoDialog(getActivity());
        dialog.setTitle("拨打电话");
        dialog.setMessage(getString(R.string.dial_number_notice, "客服"));
        dialog.setPositiveButton("拨打", l);
        dialog.setNegativeButton(getString(R.string.cancel), l);
        dialog.show();
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
                EbingoRequestParmater parmater = checkInformation();
                if (parmater != null) {
                    startPublish(parmater);
                }
                break;
            }
            case R.id.upload_3d_cb: {
//                callService();
                if (has3dPermission()) show3dNotice(upload_3d_cb.isChecked());
                break;
            }
        }
    }

    /**
     * 判断用户是否具有上传3D图片权限
     *
     * @return
     */
    private boolean has3dPermission() {
        CompanyVipInfo info = Company.getInstance().getVipInfo();
        if (info.getDisplay_3d_num() <= 0) {
            upload_3d_cb.setChecked(false);
            EbingoDialog dialog = EbingoDialog.newInstance(getActivity(), EbingoDialog.DialogStyle.STYLE_TO_PRIVILEGE);
            dialog.setMessage(getString(R.string.notice_3d));
            dialog.show();
            return false;
        }
        return true;
    }

    /**
     * 执行信息校验，并返回参数
     *
     * @return
     */
    private EbingoRequestParmater checkInformation() {

        controller.company_id = Company.getInstance().getCompanyId();
        controller.category_id = (Integer) tv_pick_category.getTag();
        controller.region_name = tv_pick_region.getText().toString().trim();
        controller.price = edit_price.getText().toString().trim();
        controller.image_url = (String) picked_image.getContentDescription();
        controller.description = (String) tv_pick_description.getContentDescription();
        controller.title = edit_title.getText().toString().trim();
        controller.contacts = edit_contact.getText().toString().trim();
        controller.contacts_phone = edit_phone.getText().toString().trim();
        controller.min_sell_num = edit_min_sell_num.getText().toString().trim();
        controller.unit = edit_unit.getText().toString().trim();
        controller.apply_3d=upload_3d_cb.isChecked();
        int code = controller.checkSupply();
        if (code > 0) {
            controller.showError(code);
            return null;
        }

        return controller.getSupplyParams(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String result = null;
        if (data != null) {
            result = data.getStringExtra("result");
        }
        requestCode = requestCode & (PublishFragment.REQUEST_MASK);
        LogCat.i("--->", "requestCode:" + Integer.toBinaryString(requestCode));
        switch (requestCode) {
            case PICK_CATEGORY:
                tv_pick_category.setText(result);
                if (data != null) tv_pick_category.setTag(data.getIntExtra("categoryId", 0));
                LogCat.i("--->", "categoryId:" + tv_pick_category.getTag());
                break;
            case PICK_DESCRIPTION:
                if (resultCode == Activity.RESULT_OK) {
                    tv_pick_description.setText(result);
                    tv_pick_description.setContentDescription(result);

                }
                break;
            case PICK_REGION:
                tv_pick_region.setText(result);
                break;
            case PICK_IMAGE: {
                if (data == null || data.getData() == null) return;
                Uri uri = data.getData();
                LogCat.i("--->", uri.toString());
                cropImage(uri);
                break;
            }
            case PICK_CAMERA:
                cropImage(Uri.fromFile(getImageTempFile()));
                break;

            case CROP: {
                if (data == null || resultCode != Activity.RESULT_OK) return;
                toPreviewActivity(Uri.fromFile(getImageTempFile()));
                break;
            }

            case PREVIEW:
                if (data == null) return;
                uploadImage(data.getData());
                break;
            case APPLY_3D:
                ContextUtil.toast(result);
                break;
        }
    }

    private File getImageTempFile() {
        return new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), CAMERA_PICTURE_NAME);
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
            picked_image.setVisibility(View.VISIBLE);
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
                LogCat.i("--->", response + "");
                try {
                    JSONObject result = response.getJSONObject("response");
                    String image_url = result.getString("data");
                    if (HttpConstant.CODE_OK.equals(result.getString("code")))
                        picked_image.setContentDescription(image_url);
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
                case R.id.btn_album:
                    openAlbum();
                    break;
                case R.id.btn_camera:
                    openCamera();
                    break;
            }
        }
    };


    /**
     * 弹出图片选择窗口
     */
    public void showPupWindow() {
        final View contentView = View.inflate(getActivity(), R.layout.pick_picture_method, null);
        final View content_layout = contentView.findViewById(R.id.content_layout);
        contentView.findViewById(R.id.btn_album).setOnClickListener(popupWindowListener);
        contentView.findViewById(R.id.btn_camera).setOnClickListener(popupWindowListener);
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
     * 打开相机拍照
     */
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getImageTempFile()));
        getActivity().startActivityForResult(intent, PICK_FOR_SUPPLY | PICK_CAMERA);
    }

    /**
     * 从相册中选择一张图片
     */
    private void openAlbum() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        getActivity().startActivityForResult(i, PICK_FOR_SUPPLY | PICK_IMAGE);
    }

    private void cropImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 640);
        intent.putExtra("aspectY", 400);
        intent.putExtra("outputX", 640);
        intent.putExtra("outputY", 400);
        intent.putExtra("output", Uri.fromFile(getImageTempFile()));// 保存到原文件
        intent.putExtra("outputFormat", "JPEG");// 返回格式
        intent.putExtra("return-data", false);
        getActivity().startActivityForResult(intent, PICK_FOR_SUPPLY | CROP);
    }

    /**
     * 清空文字
     */
    private void clearText() {
//        tv_pick_category.setText(null);
//        tv_pick_region.setText(null);
        edit_price.setText(null);

        tv_pick_description.setText(null);
        edit_title.setText(null);

        edit_min_sell_num.setText(null);
//        edit_contact.setText(null);
//        edit_phone.setText(null);
        picked_image.setVisibility(View.GONE);
        picked_image.setImageBitmap(null);
        picked_image.setContentDescription(null);
        edit_unit.setText(null);
    }


    public void startPublish(EbingoRequestParmater parmater) {
        if (mDetailInfo != null)
            parmater.put("info_id", mDetailInfo.getInfo_id());
        final ProgressDialog dialog = DialogUtil.waitingDialog(getActivity());
        HttpUtil.post(HttpConstant.saveInfo, parmater, new JsonHttpResponseHandler("utf-8") {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONObject result = response.getJSONObject("response");
                    if (HttpConstant.CODE_OK.equals(result.getString("code"))) {
                        Intent intent = new Intent(getActivity(), MySupplyActivity.class);
                        intent.putExtra(BaseListActivity.ARG_REFRESH, true);
                        startActivity(intent);
                        saveUsualData();
                        clearText();
                        if (getActivity() instanceof PublishEditActivity) {
                            getActivity().finish();
                        }
                        ContextUtil.toast("发布成功！");
                    } else {
                        ContextUtil.toast("发布失败！" + result.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ContextUtil.toast("发布失败，数据错误。");
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

    /**
     * 保存常用数据
     */
    private void saveUsualData() {
        DetailInfoBean saveBean = new DetailInfoBean();
        saveBean.setCategory_id((Integer) tv_pick_category.getTag());
        saveBean.setCategory_name(tv_pick_category.getText().toString());
        saveBean.setContacts(edit_contact.getText().toString());
        saveBean.setPhone_num(edit_phone.getText().toString());
        saveBean.setRegion(tv_pick_region.getText().toString());
        FileUtil.saveCache(getActivity(), FileUtil.PUBLISH_SUPPLY_MODULE, saveBean);
    }


    @Override
    public void edit(DetailInfoBean infoBean) {
        LogCat.i("--->", " edit:" + infoBean);
        if (infoBean == null) return;
        mDetailInfo = infoBean;
        if (tv_pick_image != null) {
            tv_pick_category.setTag(infoBean.getCategory_id());
            tv_pick_category.setText(infoBean.getCategory_name());
            tv_pick_region.setText(infoBean.getRegion());
            edit_title.setText(infoBean.getTitle());

            edit_price.setText(infoBean.getPrice());
            edit_unit.setText(infoBean.getUnit());
            edit_min_sell_num.setText(infoBean.getMin_sell_num());
            tv_pick_description.setText(infoBean.getDescription());
            tv_pick_description.setContentDescription(infoBean.getDescription());
            edit_contact.setText(infoBean.getContacts());
            edit_phone.setText(infoBean.getPhone_num());
            String image = infoBean.getImage();
            picked_image.setContentDescription(image);
            if (!TextUtils.isEmpty(image)) {
                picked_image.setVisibility(View.VISIBLE);
                ImageManager.load(image, picked_image);
            }
        }
    }
}
