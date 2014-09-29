package com.promote.ebingo.center.settings;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.promote.ebingo.R;

import java.util.ArrayList;

/**
 * Created by acer on 2014/9/29.
 */
public class ListDialog extends DialogFragment implements AdapterView.OnItemClickListener {
    private static final String ITEMS = "items";
    private ListView mListView;
    private DialogInterface.OnClickListener l;

    public static ListDialog newInstance(String[] items, DialogInterface.OnClickListener l) {
        ListDialog listDialog = new ListDialog();
        Bundle args = new Bundle();
        args.putStringArray(ITEMS, items);
        listDialog.setArguments(args);
        listDialog.setListener(l);
        return listDialog;
    }

    private void setListener(DialogInterface.OnClickListener l) {
        this.l = l;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
        }else{
            builder=new AlertDialog.Builder(getActivity());
        }
        mListView = new ListView(getActivity());
        mListView.setDivider(getResources().getDrawable(R.drawable.list_divider));
        mListView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mListView.setOnItemClickListener(this);
        mListView.setBackgroundColor(getResources().getColor(R.color.white));
        builder.setView(mListView);
        mListView.setAdapter(new ArrayAdapter<CharSequence>(getActivity(), R.layout.simple_list_item, android.R.id.text1, getArguments().getStringArray(ITEMS)));
        return builder.create();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dismiss();
        l.onClick(getDialog(), position);
    }
}
