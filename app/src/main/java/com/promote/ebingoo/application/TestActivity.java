package com.promote.ebingoo.application;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;

import com.promote.ebingoo.R;
import com.promote.ebingoo.view.SListView;

import java.util.LinkedList;

/**
 * Created by acer on 2014/10/31.
 */
public class TestActivity extends ListActivity implements SListView.OnRefreshListener {
    SListView list;
    LinkedList<String> data = new LinkedList<String>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        list = (SListView) findViewById(android.R.id.list);

        list.setOnRefreshListener(this);
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
                list.onRefreshComplete();
            }
        }, 1000);

    }

    public void onLoadMore() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                addData();
                if (data.size() > 100) {
                    list.setHasMore(false);
                }
            }
        }, 1000);

    }

    public void onClick(View v) {

    }
}
