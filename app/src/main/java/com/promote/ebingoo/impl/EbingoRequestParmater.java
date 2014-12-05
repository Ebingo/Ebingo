package com.promote.ebingoo.impl;

import android.content.Context;

import com.loopj.android.http.RequestParams;
import com.promote.ebingoo.util.DeviceInfo;

/**
 * Created by jch on 2014/8/28.
 */
public class EbingoRequestParmater extends RequestParams {


    public EbingoRequestParmater(Context context) {
        super();
        put("os", "android");
        put("time", DeviceInfo.getmCurTimeToken());
        put("uuid", DeviceInfo.getImei(context));
        put("secret", DeviceInfo.getAuth(context));
        put("version", DeviceInfo.getVersionCode(context));
    }

}
