package com.promote.ebingoo.publish;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.promote.ebingoo.BaseActivity;
import com.promote.ebingoo.R;
import com.promote.ebingoo.application.Constant;
import com.promote.ebingoo.bean.DetailInfoBean;

public class PublishEditActivity extends BaseActivity {

    public static final String INFO = "infoBean";
    public static final String TYPE = "type";
    public static final String EDIT = "edit";

    private Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_edit);
        init();
    }

    private void init() {
        DetailInfoBean infoBean = (DetailInfoBean) getIntent().getSerializableExtra(INFO);
        if (infoBean != null) {
            if ("pc".equals(infoBean.getFrom())) showDialog();
            if (Constant.PUBLISH_SUPPLY.equals(infoBean.getType())) {
                mFragment = new PublishSupply();
                setTitle(R.string.title_activity_publish_edit_supply);
            } else {
                mFragment = new PublishDemand();
                setTitle(R.string.title_activity_publish_edit_demand);
            }
        } else {
            String type = getIntent().getStringExtra(TYPE);
            if (Constant.PUBLISH_SUPPLY.equals(type)) {
                mFragment = new PublishSupply();
                setTitle(R.string.publish_supply);
            } else {
                mFragment = new PublishDemand();
                setTitle(R.string.publish_demand);
            }
        }
        Bundle args = new Bundle();
        args.putBoolean(EDIT, true);
        mFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().add(R.id.content, mFragment).commit();
        ((EditInfo) mFragment).edit(infoBean);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mFragment.onActivityResult(requestCode, resultCode, data);
    }

    private void showDialog() {
        new AlertDialog.Builder(this).setTitle(R.string.warn)
                .setMessage("当前信息由PC端发布，请至PC端修改该信息！")
                .setPositiveButton(R.string.i_know, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public interface EditInfo {
        public void edit(DetailInfoBean infoBean);
    }
}
