package com.promote.ebingo.publish;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.promote.ebingo.R;

/**
 * Created by acer on 2014/9/19.
 */
public class EbingoDialog extends AlertDialog {
    private P P;
    private TextView mTitleView;
    private TextView mMessage;
    private TextView mPositiveButton;
    private TextView mNegativeButton;
    private View btn_divider;
    public OnClickListener DEFAULT_LISTENER = new OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
        }
    };

    public EbingoDialog(Context context) {
        super(context);
        P = new P();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ebingo_dialog);
        mTitleView = (TextView) findViewById(android.R.id.title);
        mMessage = (TextView) findViewById(android.R.id.message);
        mPositiveButton = (TextView) findViewById(android.R.id.button1);
        mNegativeButton = (TextView) findViewById(android.R.id.button2);
        btn_divider = findViewById(R.id.btn_divider);
    }

    @Override
    public void setTitle(CharSequence title) {
        P.title = title;
        if (mTitleView != null) mTitleView.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(getContext().getString(titleId));
    }

    @Override
    public void setMessage(CharSequence message) {
        P.message = message;
        if (mMessage != null) mMessage.setText(message);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (P.title != null) mTitleView.setText(P.title);
        if (P.message != null) mMessage.setText(P.title);
        if (P.textPositive != null)
            mPositiveButton.setText(P.textPositive);

        if (P.textNegative != null)
            mNegativeButton.setText(P.textNegative);

        if (P.mPositiveListener != null) {
            mPositiveButton.setOnClickListener(new CustomListener(this, P.mPositiveListener, BUTTON_POSITIVE));

        }

        if (P.mNativeListener != null) {
            mPositiveButton.setOnClickListener(new CustomListener(this, P.mNativeListener, BUTTON_POSITIVE));
        }
        if (P.mPositiveListener == null || P.mNativeListener == null)
            btn_divider.setVisibility(View.GONE);
    }

    public void setPositiveButton(CharSequence text, OnClickListener positiveListener) {
        P.textPositive = text;
        P.mPositiveListener = positiveListener;
        if (mPositiveButton != null) {
            mPositiveButton.setText(text);
            mPositiveButton.setOnClickListener(new CustomListener(this, positiveListener, BUTTON_POSITIVE));
        }
    }

    public void setNegativeButton(CharSequence text, OnClickListener negativeListener) {
        P.textNegative = text;
        P.mNativeListener = negativeListener;
        if (mNegativeButton != null) {
            mNegativeButton.setText(text);
            mNegativeButton.setOnClickListener(new CustomListener(this, negativeListener, BUTTON_NEGATIVE));
        }
    }

    private class P {
        CharSequence textPositive;
        CharSequence textNegative;
        OnClickListener mPositiveListener;
        OnClickListener mNativeListener;
        CharSequence title;
        CharSequence message;
    }

    private class CustomListener implements View.OnClickListener {

        private OnClickListener mListener;
        private DialogInterface dialog;
        private int which;

        CustomListener(DialogInterface dialog, OnClickListener mListener, int which) {
            this.mListener = mListener;
            this.dialog = dialog;
            this.which = which;
        }

        @Override
        public void onClick(View v) {
            dialog.dismiss();
            if (mListener != null) mListener.onClick(dialog, which);
        }

    }
}
