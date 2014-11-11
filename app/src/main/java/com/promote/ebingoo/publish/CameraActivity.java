package com.promote.ebingoo.publish;

import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.promote.ebingoo.BaseActivity;
import com.promote.ebingoo.R;

/**
 * Created by acer on 2014/11/10.
 */
public class CameraActivity extends BaseActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
    }
}
