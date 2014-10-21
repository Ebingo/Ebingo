package com.promote.ebingo.center.settings;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.promote.ebingo.R;

/**
 * Created by acer on 2014/10/15.
 */
public class ApkProgressDialog extends Dialog {
    public ApkProgressDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.progress);
        findViewById(R.id.btn_hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setProgress(int progress) {
        ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
        TextView progressText = (TextView) findViewById(R.id.progressText);
        bar.setProgress(progress);
        progressText.setText(getContext().getResources().getString(R.string.downloaded,progress));
    }
}