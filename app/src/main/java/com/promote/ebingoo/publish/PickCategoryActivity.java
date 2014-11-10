package com.promote.ebingoo.publish;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jch.lib.util.HttpUtil;
import com.jch.lib.util.ImageManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingoo.BaseActivity;
import com.promote.ebingoo.R;
import com.promote.ebingoo.application.HttpConstant;
import com.promote.ebingoo.bean.CategoryBeen;
import com.promote.ebingoo.impl.EbingoRequestParmater;
import com.promote.ebingoo.util.ContextUtil;
import com.promote.ebingoo.util.JsonUtil;
import com.promote.ebingoo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by acer on 2014/9/2.
 */
public class PickCategoryActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    /**
     * 一级分类列表
     */
    ArrayList<CategoryBeen> parentList = new ArrayList<CategoryBeen>();
    /**
     * 当前展示的子分类列表
     */
    ArrayList<CategoryBeen> curSubList = new ArrayList<CategoryBeen>();
    /**
     * 一级分类adapter
     */
    CategoryAdapter1 adapter1;
    /**
     * 二级分类adapter
     */
    CategoryAdapter2 adapter2;
    /**
     * 一级分类ListView
     */
    ListView lv_category_1;
    /**
     * 二级分类ListView
     */
    ListView lv_category_2;
    /**
     * 总数据源
     */
    SparseArray<ArrayList<CategoryBeen>> data = new SparseArray<ArrayList<CategoryBeen>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pick_category);
        init();
    }

    private void init() {
        adapter1 = new CategoryAdapter1(this);
        adapter2 = new CategoryAdapter2(this);

        lv_category_1 = (ListView) findViewById(R.id.lv_category_1);
        lv_category_1.setOnItemClickListener(this);
        lv_category_1.setAdapter(adapter1);

        lv_category_2 = (ListView) findViewById(R.id.lv_category_2);
        lv_category_2.setOnItemClickListener(onSubItemClicked);
        lv_category_2.setAdapter(adapter2);

        reqestCategory();
    }

    /**
     * 从网络获取分类列表
     */
    private void reqestCategory() {
        EbingoRequestParmater params = new EbingoRequestParmater(this);
        HttpUtil.post(HttpConstant.getCategories, params, new JsonHttpResponseHandler("utf-8") {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                LogCat.i("getCategories", response + "");
                try {
                    JSONArray array = response.getJSONArray("response");
                    ArrayList<CategoryBeen> temp = new ArrayList<CategoryBeen>();
                    JsonUtil.getArray(array, CategoryBeen.class, temp);

                    int size = temp.size();
                    //遍历temp，按照CategoryBeen的parentId进行排列组合
                    for (int i = 0; i < size; i++) {
                        CategoryBeen been = temp.get(i);
                        //get the parent_id
                        Integer parent_id = been.getParent_id();
                        ArrayList<CategoryBeen> son = data.get(parent_id);
                        if (son == null) {
                            //create new List,if does not exits
                            son = new ArrayList<CategoryBeen>();
                            data.put(parent_id, son);
                        }
                        son.add(been);
                    }

                    //从数据集里，取出parentId=0的集合，即一级分类
                    parentList.addAll(data.get(0));

                    //刷新列表视图
                    adapter1.notifyDataSetChanged();

                    //选中第一条
                    adapter1.setSelected(0);

                    //获取parentList的第一条的二级分类
                    setSubCategory(parentList.get(0).getId());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                ContextUtil.toast(responseString);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    /**
     * 二级分类点击事件
     */
    private AdapterView.OnItemClickListener onSubItemClicked = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent data = new Intent();
            CategoryBeen selectCategory = curSubList.get(position);
            data.putExtra("categoryId", selectCategory.getId());
            data.putExtra("result", selectCategory.getName());
            setResult(RESULT_OK, data);
            finish();
        }
    };

    /**
     * 一级分类点击处理
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        adapter1.setSelected(position);
        setSubCategory(parentList.get(position).getId());
    }

    /**
     * 根据parentId,获取二级分类
     */
    private void setSubCategory(int parentId) {
        curSubList.clear();
        ArrayList<CategoryBeen> temp = data.get(parentId);
        if (temp != null) {
            curSubList.addAll(temp);
        }
        adapter2.notifyDataSetChanged();
    }


    class CategoryAdapter1 extends BaseAdapter {
        private Activity context;
        private final int checkedDrawable = R.drawable.category_list_item_bg;
        private final int unCheckedDrawable = R.drawable.click_gray_bg;
        private int selected = -1;

        /**
         * @param selected
         */

        public void setSelected(int selected) {
            this.selected = selected;
            notifyDataSetChanged();

        }

        CategoryAdapter1(Activity context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return parentList.size();
        }

        @Override
        public Object getItem(int position) {
            return parentList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolderCategory holder;
            if (convertView == null) {
                holder = new ViewHolderCategory();
                convertView = LayoutInflater.from(context).inflate(R.layout.category_item_list, null);
                holder.tv_category = (TextView) convertView.findViewById(R.id.tv_category);
                holder.tv_sub_category = (TextView) convertView.findViewById(R.id.tv_sub_category);
                holder.ic = (ImageView) convertView.findViewById(R.id.ic_category);
            } else {
                holder = (ViewHolderCategory) convertView.getTag();
            }
            CategoryBeen been = parentList.get(position);
            ImageManager.load(been.getImage(), holder.ic);
            holder.tv_category.setText(been.getName());

            StringBuilder hotCategory = new StringBuilder();
            try {
                ArrayList<CategoryBeen> son = data.get(been.getId());

                hotCategory
                        .append(son.get(0).getName())
                        .append(" ")
                        .append(son.get(1).getName());
            } catch (IndexOutOfBoundsException e) {
                LogCat.w("hotCategory less than 2");
            } catch (NullPointerException e) {
                LogCat.w("no hotCategory");
            }

            holder.tv_sub_category.setText(hotCategory);
            if (selected == position)
                convertView.setBackgroundDrawable(context.getResources().getDrawable(checkedDrawable));
            else
                convertView.setBackgroundDrawable(context.getResources().getDrawable(unCheckedDrawable));
            convertView.setTag(holder);
            return convertView;
        }

    }

    class CategoryAdapter2 extends BaseAdapter {
        private Activity context;

        CategoryAdapter2(Activity context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return curSubList.size();
        }

        @Override
        public Object getItem(int position) {
            return curSubList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolderSubCategory holder;
            if (convertView == null) {
                holder = new ViewHolderSubCategory();
                holder.tv = (TextView) LayoutInflater.from(context).inflate(R.layout.activate_textview, null);
                holder.tv.setBackgroundColor(0xfff2f2f2);
                holder.tv.setTextColor(0xff808080);
                holder.tv.setTextSize(14);
                holder.tv.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                convertView = holder.tv;
            } else {
                holder = (ViewHolderSubCategory) convertView.getTag();
            }
            holder.tv.setText(curSubList.get(position).getName());
            convertView.setTag(holder);
            return convertView;
        }

    }


    static class ViewHolderSubCategory {
        TextView tv;
    }

    static class ViewHolderCategory {
        TextView tv_sub_category;
        TextView tv_category;
        ImageView ic;
    }

}
