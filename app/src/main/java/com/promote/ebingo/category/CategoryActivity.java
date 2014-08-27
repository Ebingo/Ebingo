package com.promote.ebingo.category;

        import android.os.Bundle;
        import android.support.v7.app.ActionBarActivity;
        import android.widget.CheckBox;
        import android.widget.ListView;

        import com.promote.ebingo.R;

public class CategoryActivity extends ActionBarActivity {

    private CheckBox categoryleftcb;
    private CheckBox categoryrightcb;
    private ListView categorylv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        initialize();
    }


    private void initialize() {

        categoryleftcb = (CheckBox) findViewById(R.id.category_left_cb);
        categoryrightcb = (CheckBox) findViewById(R.id.category_right_cb);
        categorylv = (ListView) findViewById(R.id.category_lv);
    }
}
