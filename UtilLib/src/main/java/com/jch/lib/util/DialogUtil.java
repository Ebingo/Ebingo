package com.jch.lib.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.jch.lib.R;

/**
 * 自定义公共的dialog.
 * 
 * @author Administrator
 * 
 */
public class DialogUtil {

	/**
	 * 
	 * 网络连接提示.
	 * 
	 * @param context
	 * @return
	 */
	public static Dialog netLineWarning(Context context) {

		Dialog dialog = new Dialog(context);

		return dialog;

	}

	/**
	 * 旋转等待dialog.
	 * 
	 * @param context
	 * @return
	 */
	public static ProgressDialog waitingDialog(Context context) {
		return waitingDialog(context,"数据访问中");
	}

    public static ProgressDialog waitingDialog(Context context,String msg){
        return ProgressDialog.show(context, "", msg, true);
    }

	public static AlertDialog msgSinglBtnAlertDialog(Context context, String msg) {

		AlertDialog dialog = new AlertDialog.Builder(context).setMessage(msg)
				.setPositiveButton(R.string.ok, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
		return dialog;

	}

}
