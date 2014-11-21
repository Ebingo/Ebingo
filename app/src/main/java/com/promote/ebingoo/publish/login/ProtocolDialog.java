package com.promote.ebingoo.publish.login;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.promote.ebingoo.R;
import com.promote.ebingoo.util.BaseDialog;

/**
 * Created by ACER on 2014/11/20.
 */
public class ProtocolDialog extends BaseDialog implements CompoundButton.OnCheckedChangeListener {

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        dismiss();
        callback.isChecked(isChecked);
    }

    public interface CheckCallback {

        public void isChecked(boolean checked);

    }

    /**
     * 填充内容.
     */
    private View contentView;
    private TextView protocoldialogtv;
    private ScrollView protocoldialogsv;
    private CheckBox protocoldialogcb;

    private CheckCallback callback;

    public ProtocolDialog(Context context) {
        super(context);
        contentView = LayoutInflater.from(context).inflate(R.layout.center_protocol_dialog_layout, null);
        setView(contentView);
        initialize();
    }

    public static ProtocolDialog build(Activity activity) {

        return new ProtocolDialog(activity);
    }

    public void setProtocolStr(String str) {
        protocoldialogtv.setText(str);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void setCallback(CheckCallback callback) {
        this.callback = callback;
    }

    private void initialize() {

        protocoldialogtv = (TextView) findViewById(R.id.protocol_dialog_tv);
        protocoldialogsv = (ScrollView) findViewById(R.id.protocol_dialog_sv);
        protocoldialogcb = (CheckBox) findViewById(R.id.protocol_dialog_cb);

        protocoldialogcb.setOnCheckedChangeListener(this);
    }
}
