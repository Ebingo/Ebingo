package com.promote.ebingoo.InformationActivity;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.jch.lib.view.Dialog.niftyefffectslib.BaseDialog;
import com.promote.ebingoo.R;

/**
 * Created by ACER on 2014/9/24.
 */
public class VipDialog extends BaseDialog {


    private TextView calldialogcallbtn;

    public VipDialog(Context context) {
        super(context);

        setContentView(R.layout.vip_dialog);
        initialize();

    }


    private void initialize() {

        calldialogcallbtn = (TextView) findViewById(R.id.vip_dialog_ok);
        calldialogcallbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


}
