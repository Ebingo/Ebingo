package com.promote.ebingoo.application;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;

import com.promote.ebingoo.R;
import com.promote.ebingoo.view.RefreshListView;

import java.util.LinkedList;

/**
 * Created by acer on 2014/10/31.
 */
public class TestActivity extends ListActivity implements RefreshListView.RefreshListener {
    RefreshListView list;
    LinkedList<String> data = new LinkedList<String>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        list = (RefreshListView) findViewById(android.R.id.list);

        list.setRefreshListener(this);
        adapter = new ArrayAdapter<String>(this, R.layout.textview, data);
        setListAdapter(adapter);
        addData();
    }

    private void addData() {
        int size = data.size();
        for (int i = size; i < size + 20; i++) {
            data.add("打开门" + i);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                data.clear();
                addData();
                list.refreshFinished();
                list.setCanLoadMore(true);
            }
        }, 1000);

    }

    @Override
    public void onLoadMore() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                addData();
                if (data.size() > 30) list.loadMoreFinished(false);
                else list.loadMoreFinished(true);
            }
        }, 1000);

    }

    public void onClick(View v) {
        list.setCanRefresh(!list.canRefresh());
    }
}
