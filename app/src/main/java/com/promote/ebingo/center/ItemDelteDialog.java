package com.promote.ebingo.center;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.promote.ebingo.R;

/**
 * Created by ACER on 2014/9/11.
 * <p/>
 * listView 删除一个item项的dialog。
 */
public class ItemDelteDialog extends Dialog {


    public interface DeleteItemListener {
        public void onItemDelete(View view, int itemId);
    }

    private TextView centerdelnametv;
    private TextView centerdeldeltetv;
    private TextView centerdelcanceltv;
    private DeleteItemListener mDeleteItemListener;
    private CharSequence title;
    /**
     * 所要删除条的id. *
     */
    private int mId;

    public ItemDelteDialog(Context context, DeleteItemListener clickListener) {

        super(context);
        this.mDeleteItemListener = clickListener;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.center_delete_view);
        initialize();
    }

    @Override
    protected void onStart() {
        super.onStart();
        centerdelnametv.setText(title);
    }

    private void initialize() {

        centerdelnametv = (TextView) findViewById(R.id.center_del_name_tv);
        centerdeldeltetv = (TextView) findViewById(R.id.center_del_delte_tv);
        centerdelcanceltv = (TextView) findViewById(R.id.center_del_cancel_tv);

        centerdelcanceltv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        this.setCanceledOnTouchOutside(true);
        centerdeldeltetv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mDeleteItemListener.onItemDelete(v, mId);
            }
        });
    }

    public void setItemText(CharSequence itemText, int id) {
        title = itemText;
        if (centerdelnametv != null)
            centerdelnametv.setText(itemText);

        this.mId = id;
    }
}
