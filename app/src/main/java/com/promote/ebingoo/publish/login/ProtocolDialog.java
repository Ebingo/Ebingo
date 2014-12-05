package com.promote.ebingoo.publish.login;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.CompoundButton;

import com.jch.lib.view.MyCustomDialog;
import com.promote.ebingoo.R;

/**
 * Created by ACER on 2014/11/20.
 */
public class ProtocolDialog extends MyCustomDialog implements CompoundButton.OnCheckedChangeListener {

    /**
     * 填充内容.
     */
    private View contentView;
    private WebView protocoldialogtv;
    private ReadedCallback callback;
    public ProtocolDialog(Context context) {
        super(context);

    }

    public static ProtocolDialog build(Activity activity) {
        return new ProtocolDialog(activity);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        dismiss();
        if (callback != null)
            callback.isChecked(isChecked);
    }

    @Override
    protected View onCreateView() {

        contentView = LayoutInflater.from(getContext()).inflate(R.layout.center_protocol_dialog_layout, null);
        initialize();
        return contentView;
    }

    public void setProtocolStr(String str) {
        protocoldialogtv.loadData(str, "text/html", "UTF-8");
    }

    public void setProTocolHtm(String url) {
        protocoldialogtv.loadUrl(url);
    }

    public void setCallback(ReadedCallback callback) {
        this.callback = callback;
    }

    private void initialize() {

        protocoldialogtv = (WebView) contentView.findViewById(R.id.protocol_dialog_tv);

        setCanceledOnTouchOutside(true);
    }

    public interface ReadedCallback {

        public void isChecked(boolean checked);

    }
}
