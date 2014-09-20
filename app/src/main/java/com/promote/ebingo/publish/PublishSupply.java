package com.promote.ebingo.publish;


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
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.center.MyPrivilegeActivity;
import com.promote.ebingo.center.MySupplyActivity;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.publish.login.LoginManager;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.ImageUtil;
import com.promote.ebingo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import static com.promote.ebingo.publish.PublishFragment.Error;

import static com.promote.ebingo.publish.PublishFragment.*;

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
    private CheckBox upload_3d_cb;

    private TextView tv_3d_notice;
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
        return v;
    }

    private void show3dNotice(boolean show) {
        if (!show) {
            tv_3d_notice.setVisibility(View.GONE);
            return;
        }
        String prefix = "上传3D图片需要大量素材，发布成功后请等待我们与您联系，或现在";
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
        dialog.setMessage("是否拨打客服电话?");
        dialog.setPositiveButton("拨打", l);
        dialog.setNegativeButton("取消", l);
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
        String vipCode = Company.getInstance().getVipType();
        if (VipType.parse(vipCode).compareTo(VipType.VVIP) < 0) {
            EbingoDialog dialog = new EbingoDialog(getActivity());
            dialog.setTitle("尊敬的" + VipType.nameOf(vipCode));
            dialog.setMessage("您目前所属等级没有权限上传3D图片，请先升级");
            dialog.setPositiveButton("升 级", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getActivity(), MyPrivilegeActivity.class);
                    intent.putExtra(MyPrivilegeActivity.SHOW_VVIP, true);
                    upload_3d_cb.setChecked(false);
                    startActivity(intent);
                }
            });
            dialog.setNegativeButton("取 消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    upload_3d_cb.setChecked(false);
                }
            });
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
        EbingoRequestParmater parmater = null;
        Integer company_id = Company.getInstance().getCompanyId();
        Integer category_id = (Integer) tv_pick_category.getTag();
        String region_name = tv_pick_region.getText().toString().trim();
        String price = edit_price.getText().toString().trim();
        String image_url = edit_price.getText().toString().trim();
        String description = tv_pick_description.getText().toString().trim();
        String title = edit_title.getText().toString().trim();
        String contacts = edit_contact.getText().toString().trim();
        String contacts_phone = edit_phone.getText().toString().trim();
        String min_sell_num = edit_min_sell_num.getText().toString().trim();

        if (company_id == null) ContextUtil.toast("请重新登录！");
        else if (category_id == null) Error.showError(tv_pick_category, Error.CATEGORY_EMPTY);
        else if (TextUtils.isEmpty(region_name))
            Error.showError(tv_pick_region, Error.REGION_EMPTY);
        else if (TextUtils.isEmpty(title)) Error.showError(edit_title, Error.TITLE_EMPTY);

        else if (TextUtils.isEmpty(price)) Error.showError(edit_price, Error.PRICE_EMPTY);
        else if (TextUtils.isEmpty(image_url)) Error.showError(tv_pick_image, Error.IMAGE_EMPTY);
        else if (TextUtils.isEmpty(description))
            Error.showError(tv_pick_description, Error.DESCRIPTION_EMPTY);

        else if (TextUtils.isEmpty(min_sell_num))
            Error.showError(edit_min_sell_num, Error.MIN_SELL_NUM_EMPTY);
        else if (TextUtils.isEmpty(contacts)) Error.showError(edit_contact, Error.CONTACT_EMPTY);
        else if (TextUtils.isEmpty(contacts_phone)) Error.showError(edit_phone, Error.PHONE_EMPTY);
        else if (LoginManager.isMobile(contacts_phone))
            Error.showError(edit_phone, Error.PHONE_FORMAT_ERROR);
        else {
            parmater = new EbingoRequestParmater(getActivity());
            parmater.put("type", TYPE_SUPPLY);
            parmater.put("company_id", company_id);

            parmater.put("category_id", category_id);
            parmater.put("region_name", region_name);
            parmater.put("price", price);

            parmater.put("image_url", image_url);
            parmater.put("description", description);
            parmater.put("title", title);

            parmater.put("min_sell_num", min_sell_num);
            parmater.put("contacts", contacts);
            parmater.put("contacts_phone", contacts_phone);
            LogCat.i("--->" + parmater);
        }
        return parmater;
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
                if (data == null || data.getData() == null) return;
                Uri uri = data.getData();
                LogCat.i("--->", uri.toString());
                toPreviewActivity(uri);
                break;
            case PICK_CAMERA:
                toPreviewActivity(Uri.fromFile(getImageTempFile()));
                break;
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
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), CAMERA_PICTURE_NAME);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                LogCat.e("--->", "create Image File failed!!");
                file = null;
            }
        }
        return file;
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
                case R.id.btn_album:
                    openAlbum();
                    break;
                case R.id.btn_camera:
                    openCamera();
                    break;
            }
        }
    };


//    /**
//     * 选择获取图片的方式
//     *
//     * @param title
//     */
//    private void showPickDialog(String title) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle(title).setItems(new String[]{"相册", "拍照"}, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                switch (which) {
//                    case 0:
//                        openAlbum();
//                        break;
//                    case 1:
//                        openCamera();
//                        break;
//                }
//            }
//        }).show();
//    }


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
        startActivityForResult(intent, PICK_FOR_SUPPLY | PICK_CAMERA);
    }

    /**
     * 从相册中选择一张图片
     */
    private void openAlbum() {
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
        picked_image.setVisibility(View.GONE);
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

    private static class TextClick extends ClickableSpan {
        String span;

        private TextClick(String span) {
            this.span = span;
        }

        @Override
        public void onClick(View widget) {
            ContextUtil.toast(span);
        }
    }

}
