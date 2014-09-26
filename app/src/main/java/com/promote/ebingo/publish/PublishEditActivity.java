package com.promote.ebingo.publish;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.promote.ebingo.BaseActivity;
import com.promote.ebingo.R;

public class PublishEditActivity extends BaseActivity {

    public static final String TYPE = "0";
    public static final String TYPE_DEMAND = "1";
    public static final String TYPE_SUPPLY = "2";
    private Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_edit);
        String type = getIntent().getStringExtra(TYPE);
        if (TYPE_SUPPLY.equals(type)){
            mFragment = new PublishSupply();
            setTitle(R.string.title_activity_publish_edit);
        }
        else mFragment = new PublishDemand();
        getSupportFragmentManager().beginTransaction().add(R.id.content, mFragment).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mFragment.onActivityResult(requestCode, resultCode, data);
    }
}
