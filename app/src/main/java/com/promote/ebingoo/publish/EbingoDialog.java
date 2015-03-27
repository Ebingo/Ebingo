package com.promote.ebingoo.publish;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.promote.ebingoo.R;
import com.promote.ebingoo.center.MyPrivilegeActivity;

/**
 * Created by acer on 2014/9/19.
 */
public class EbingoDialog extends AlertDialog {
    public OnClickListener DEFAULT_LISTENER = new OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
        }
    };
    private P P;
    private TextView mTitleView;
    private TextView mMessage;
    private TextView mPositiveButton;
    private TextView mNegativeButton;
    private TextView mNeutralButton;
    private View btn_divider1;
    private View btn_divider2;

    public EbingoDialog(Context context) {
        super(context);
        P = new P();
    }

    /**
     * 对话框工厂
     *
     * @param context
     * @param style
     * @return
     */
    public static EbingoDialog newInstance(final Context context, DialogStyle style) {
        EbingoDialog dialog = new EbingoDialog(context);
        switch (style) {

            case STYLE_INFO_DELETED: {
                dialog.setTitle(R.string.warn);
                dialog.setMessage(R.string.info_has_deleted);
                dialog.setPositiveButton(R.string.i_know, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (context instanceof Activity) {
                            ((Activity) context).finish();
                        }
                    }
                });
                break;
            }
            case STYLE_TO_PRIVILEGE: {
                String vipName = VipType.getCompanyInstance().name;
                dialog.setTitle(context.getString(R.string.dear_somebody, vipName));
                dialog.setMessage(context.getString(R.string.no_permission, vipName));
                dialog.setPositiveButton(R.string.update_right_now, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final VipType companyVipType = VipType.getCompanyInstance();
                        VipType next = companyVipType.next();
                        if (next == null)
                            throw new RuntimeException(companyVipType.name + "is the highest Vip,can not be upgrade!");
                        Intent intent = new Intent(context, MyPrivilegeActivity.class);
                        intent.putExtra(MyPrivilegeActivity.SHOW_VIP_CODE, next.code);
                        context.startActivity(intent);
                    }
                });
                dialog.setNegativeButton(R.string.cancel, dialog.DEFAULT_LISTENER);
                break;
            }

            case STYLE_I_KNOW: {
                dialog.setTitle(R.string.warn);
                dialog.setPositiveButton(R.string.i_know, dialog.DEFAULT_LISTENER);
                break;
            }
            case STYLE_CALL_PHONE: {
                dialog.setMesIcon(R.drawable.tell);
                dialog.setNegativeButton(R.string.cancel, dialog.DEFAULT_LISTENER);
                break;
            }

        }
        return dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ebingo_dialog);
        mTitleView = (TextView) findViewById(android.R.id.title);
        mMessage = (TextView) findViewById(android.R.id.message);
        mPositiveButton = (TextView) findViewById(R.id.btn_positive);
        mNegativeButton = (TextView) findViewById(R.id.button_negative);
        mNeutralButton = (TextView) findViewById(R.id.button_neutral);
        btn_divider1 = findViewById(R.id.btn_divider1);
        btn_divider2 = findViewById(R.id.btn_divider2);
    }

    public void setMesIcon(int icon) {
        P.msgIcon = icon;
        if (mMessage != null) {
            Drawable ic = getContext().getResources().getDrawable(P.msgIcon);
            ic.setBounds(0, 0, ic.getMinimumWidth(), ic.getMinimumHeight());
            mMessage.setCompoundDrawables(ic, null, null, null);
        }
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

        if (P.mPositiveListener != null) {
            setPositiveButton(P.textPositive, P.mPositiveListener);
        } else {
            mPositiveButton.setVisibility(View.GONE);
        }

        if (P.mNeutralListener != null) {
            setNeutralButton(P.textNeutral, P.mNeutralListener);
        } else {
            mNeutralButton.setVisibility(View.GONE);
            btn_divider1.setVisibility(View.GONE);
        }

        if (P.mNativeListener != null) {
            setNegativeButton(P.textNegative, P.mNativeListener);
        } else {
            mNegativeButton.setVisibility(View.GONE);
            btn_divider2.setVisibility(View.GONE);
        }

        if (P.msgIcon != 0) {
            Drawable ic = getContext().getResources().getDrawable(P.msgIcon);
            ic.setBounds(0, 0, ic.getMinimumWidth(), ic.getMinimumHeight());
            mMessage.setCompoundDrawables(ic, null, null, null);
        }
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

    public void setNeutralButton(int textId, OnClickListener neutralListener) {
        setNeutralButton(getContext().getResources().getString(textId), neutralListener);
    }

    public void setNeutralButton(CharSequence text, OnClickListener neutralListener) {
        P.textNeutral = text;
        P.mNeutralListener = neutralListener;
        if (mNeutralButton != null) {
            mNeutralButton.setText(text);
            mNeutralButton.setOnClickListener(new CustomListener(this, neutralListener, BUTTON_NEUTRAL));
        }
    }

    public enum DialogStyle {
        /**
         * 跳转到特权页面的对话框
         */
        STYLE_TO_PRIVILEGE,

        /**
         * 信息已经被删除，点击“我知道了”会关闭当前Activity
         */
        STYLE_INFO_DELETED,
        /**
         * title:温馨提示
         * 点击:我知道了
         */
        STYLE_I_KNOW,
        /**
         * 拨打电话
         */
        STYLE_CALL_PHONE
    }

    private class P {
        CharSequence textPositive;
        CharSequence textNeutral;
        CharSequence textNegative;
        OnClickListener mPositiveListener;
        OnClickListener mNativeListener;
        OnClickListener mNeutralListener;
        CharSequence title;
        CharSequence message;
        int msgIcon;
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
