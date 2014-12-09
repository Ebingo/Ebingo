package com.promote.ebingoo.InformationActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.jch.lib.util.VaildUtil;
import com.promote.ebingoo.R;
import com.promote.ebingoo.publish.EbingoDialog;
import com.promote.ebingoo.util.LogCat;

/**
 * Created by ACER on 2014/12/9.
 */
public class WebViewJavaInterface {

    private Activity activity;
    private Handler handler;
    private WebView scanwb;


    public WebViewJavaInterface(Activity activity, WebView webView) {
        this.activity = activity;
        handler = new Handler(activity.getMainLooper());
        scanwb = webView;
    }

    /**
     * 推送Intent到消息队列的接口
     */
    private class JavaInterfaceRunnable implements Runnable {
        private Intent postIntent;

        private JavaInterfaceRunnable(Intent postIntent) {
            this.postIntent = postIntent;
        }

        @Override
        public void run() {
            activity.startActivity(postIntent);
        }
    }

    @JavascriptInterface()
    public void jumpToSupply(int id) {
        LogCat.i("---------jumpToSupply");
        Intent intent = new Intent(activity, ProductInfoActivity.class);
        intent.putExtra(ProductInfoActivity.ARG_ID, id);
        handler.post(new JavaInterfaceRunnable(intent));

    }

    @JavascriptInterface()
    public void jumpToDemand(int id) {
        LogCat.i("---------jumpToDemand");
        Intent intent = new Intent(activity, BuyInfoActivity.class);
        intent.putExtra(BuyInfoActivity.DEMAND_ID, id);
        handler.post(new JavaInterfaceRunnable(intent));
    }

    @JavascriptInterface()
    public void jumpToCompany(int id) {
        LogCat.i("---------jumpToCompany");
        Intent intent = new Intent(activity, InterpriseInfoActivity.class);
        intent.putExtra(InterpriseInfoActivity.ARG_ID, id);
        handler.post(new JavaInterfaceRunnable(intent));
    }

    @JavascriptInterface
    public String callPhone(final String number, final String contacts) {
        if (TextUtils.isEmpty(number) || number.equals(VaildUtil.validPhone(number))) return "fail";

        handler.post(new Runnable() {
            @Override
            public void run() {
                EbingoDialog dialog = EbingoDialog.newInstance(activity, EbingoDialog.DialogStyle.STYLE_CALL_PHONE);
                dialog.setTitle(contacts);
                dialog.setMessage(activity.getString(R.string.dial_number_notice, number));
                dialog.setPositiveButton(R.string.make_call, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LogCat.i("--->", "dial:" + number);
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                        activity.startActivity(intent);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        return "OK";
    }

    @JavascriptInterface
    public String share() {

        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Intent.ACTION_SEND); // 启动分享发送的属性
                intent.setType("text/plain"); // 分享发送的数据类型
                intent.putExtra(Intent.EXTRA_SUBJECT, scanwb.getTitle()); // 分享的主题
                intent.putExtra(Intent.EXTRA_TEXT, scanwb.getTitle() + scanwb.getUrl()); // 分享的内容
                activity.startActivity(Intent.createChooser(intent, "分享到"));
            }
        });
        return "OK";
    }

}
