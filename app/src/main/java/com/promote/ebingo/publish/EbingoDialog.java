package com.promote.ebingo.publish;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.promote.ebingo.R;
import com.promote.ebingo.center.MyPrivilegeActivity;

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

    public enum DialogStyle {
        /**
         * 跳转到特权页面的对话框
         */
        STYLE_TO_PRIVILEGE,
        /**
         * 添加订阅标签超出上限
         */
        STYLE_CANNOT_ADD_TAG,
        /**
         * 信息已经被删除，点击“我知道了”会关闭当前Activity
         */
        STYLE_INFO_DELETED,
    }

    /**
     * 对话框工厂
     *
     * @param context
     * @param style
     * @return
     */
    public static EbingoDialog newInstance(final Activity context, DialogStyle style) {
        EbingoDialog dialog = new EbingoDialog(context);
        switch (style) {

            case STYLE_CANNOT_ADD_TAG: {
                VipType.VipInfo info = VipType.getCompanyInstance().getVipInfo();
                dialog.setTitle(R.string.warn);
                dialog.setMessage(context.getString(R.string.vip_add_tag, VipType.getCompanyInstance().name, info.book_tag_num));
                dialog.setPositiveButton(R.string.update_right_now, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        toMyPrivilegeActivity(context);
                    }
                });
                dialog.setNegativeButton(R.string.cancel, dialog.DEFAULT_LISTENER);
                break;
            }
            case STYLE_INFO_DELETED: {
                dialog.setTitle(R.string.warn);
                dialog.setMessage(R.string.info_has_deleted);
                dialog.setPositiveButton(R.string.i_know, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.finish();
                    }
                });
                break;
            }
            case STYLE_TO_PRIVILEGE: {
                dialog.setTitle(R.string.warn);
                dialog.setMessage(context.getString(R.string.no_permission, VipType.getCompanyInstance().name));
                dialog.setPositiveButton(R.string.update_right_now, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        toMyPrivilegeActivity(context);
                    }
                });
                dialog.setNegativeButton(R.string.cancel, dialog.DEFAULT_LISTENER);
                break;
            }
        }
        return dialog;
    }

    /**
     * 跳到我的特权，并且展示下一个等级vip信息
     *
     * @param context
     */
    private static void toMyPrivilegeActivity(Context context) {
        final VipType companyVipType = VipType.getCompanyInstance();
        VipType next = companyVipType.next();
        if (next == null)
            throw new RuntimeException(companyVipType.name + "is the highest Vip,can not be upgrade!");
        Intent intent = new Intent(context, MyPrivilegeActivity.class);
        intent.putExtra(MyPrivilegeActivity.SHOW_VIP_CODE, next.code);
        context.startActivity(intent);
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

    public void setMessage(int messageId) {
        setMessage(getContext().getResources().getString(messageId));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (P.title != null) mTitleView.setText(P.title);
        if (P.message != null) mMessage.setText(P.message);
        if (P.textPositive != null)
            mPositiveButton.setText(P.textPositive);

        if (P.textNegative != null)
            mNegativeButton.setText(P.textNegative);

        if (P.mPositiveListener != null) {
            mPositiveButton.setOnClickListener(new CustomListener(this, P.mPositiveListener, BUTTON_POSITIVE));
        } else mPositiveButton.setVisibility(View.GONE);

        if (P.mNativeListener != null) {
            mNegativeButton.setOnClickListener(new CustomListener(this, P.mNativeListener, BUTTON_NEGATIVE));
        } else mNegativeButton.setVisibility(View.GONE);

        if (P.mPositiveListener == null || P.mNativeListener == null)
            btn_divider.setVisibility(View.GONE);
    }

    public void setPositiveButton(int textId, OnClickListener positiveListener) {
        setPositiveButton(getContext().getResources().getString(textId), positiveListener);
    }

    public void setPositiveButton(CharSequence text, OnClickListener positiveListener) {
        P.textPositive = text;
        P.mPositiveListener = positiveListener;
        if (mPositiveButton != null) {
            mPositiveButton.setText(text);
            mPositiveButton.setOnClickListener(new CustomListener(this, positiveListener, BUTTON_POSITIVE));
        }
    }

    public void setNegativeButton(int textId, OnClickListener negativeListener) {
        setNegativeButton(getContext().getResources().getString(textId), negativeListener);
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
