package com.promote.ebingo.search;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.promote.ebingo.R;

public class SearchActivity extends Activity implements View.OnClickListener{


    private ImageView searchbackbtn;
    private CheckBox searchcategrycb;
    private EditText searchbaret;
    private ImageButton searchbtn;
    private LinearLayout searchheadcenterll;
    private TextView searchkeytv;
    private LinearLayout searchresultkeyll;
    private ListView searchlv;
    private Button searchclearbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initialize();
    }


    private void initialize() {

        searchbackbtn = (ImageView) findViewById(R.id.search_back_btn);
        searchcategrycb = (CheckBox) findViewById(R.id.search_categry_cb);
        searchbaret = (EditText) findViewById(R.id.search_bar_et);
        searchbtn = (ImageButton) findViewById(R.id.search_btn);
        searchheadcenterll = (LinearLayout) findViewById(R.id.search_head_center_ll);
        searchkeytv = (TextView) findViewById(R.id.search_key_tv);
        searchresultkeyll = (LinearLayout) findViewById(R.id.search_result_key_ll);
        searchlv = (ListView) findViewById(R.id.search_lv);
        searchclearbtn = (Button) findViewById(R.id.search_clear_btn);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id){

            case  R.id.search_back_btn:{
                onBackPressed();
                this.finish();
            }

        }

    }
}
