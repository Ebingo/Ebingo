package com.promote.ebingoo.publish;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.promote.ebingoo.BaseActivity;
import com.promote.ebingoo.R;
import com.promote.ebingoo.application.HttpConstant;
import com.promote.ebingoo.bean.RegionBeen;
import com.promote.ebingoo.impl.EbingoHandler;
import com.promote.ebingoo.impl.EbingoRequestParmater;
import com.promote.ebingoo.util.ContextUtil;
import com.promote.ebingoo.util.Dimension;
import com.promote.ebingoo.util.FileUtil;
import com.promote.ebingoo.util.JsonUtil;
import com.promote.ebingoo.util.LogCat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by acer on 2014/9/9.
 */
public class PickRegionActivity extends BaseActivity {

    private List<RegionBeen> provinceList = new ArrayList<RegionBeen>();
    private List<RegionBeen> cityList = new ArrayList<RegionBeen>();
    private ProvinceAdapter provinceAdapter;
    private CityAdapter cityAdapter;//
    private int selected = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.double_list_layout);
        init();
    }

    private AdapterView.OnItemClickListener onProvinceClicked = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selected = position;
            getCity(provinceList.get(position).getId());
            provinceAdapter.notifyDataSetChanged();

        }
    };

    private void getCity(final int id) {
        cityList.clear();
        getDataFromCache(FileUtil.FILE_CITY_LIST + id, cityList);
        if (cityList.size() == 0) {
            //获取城市列表
            EbingoRequestParmater params = new EbingoRequestParmater(this);
            params.put("province", id);
            final Dialog dialog = DialogUtil.waitingDialog(this, "正在加载城市列表...");
            HttpUtil.post(HttpConstant.getCityList, params, new EbingoHandler() {
                @Override
                public void onSuccess(int statusCode, JSONObject response) {
                    try {
                        JSONArray array = response.getJSONArray("data");
                        final ArrayList<RegionBeen> temp = new ArrayList<RegionBeen>();
                        JsonUtil.getArray(array, RegionBeen.class, temp);
                        ContextUtil.saveCache(FileUtil.FILE_CITY_LIST + id, temp);
                        cityList.addAll(temp);
                        cityAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFail(int statusCode, String msg) {

                }

                @Override
                public void onFinish() {
                    dialog.dismiss();
                }
            });
        } else {
            cityAdapter.notifyDataSetChanged();
        }
    }

    private AdapterView.OnItemClickListener onCityClicked = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            RegionBeen province = provinceList.get(selected);
            RegionBeen city = cityList.get(position);
            Intent data = new Intent();
            data.putExtra("result", province.getName() + city.getName());
            data.putExtra("province_id", province.getId());
            data.putExtra("city_id", city.getId());
            setResult(RESULT_OK, data);
            finish();
        }
    };

    private void getDataFromCache(String cacheName, List data) {
        List temp = (List) FileUtil.readCache(this, cacheName);
        if (temp != null) {
            data.addAll(temp);
        }
    }

    private void init() {

        provinceAdapter = new ProvinceAdapter(this);
        cityAdapter = new CityAdapter(this);

        ListView lv_province = (ListView) findViewById(R.id.list1);
        lv_province.setAdapter(provinceAdapter);
        lv_province.setOnItemClickListener(onProvinceClicked);

        ListView lv_city = (ListView) findViewById(R.id.list2);
        lv_city.setAdapter(cityAdapter);
        lv_city.setOnItemClickListener(onCityClicked);

        getDataFromCache(FileUtil.FILE_PROVINCE_LIST, provinceList);
        if (provinceList.size() <= 0) getProvinceList();

    }

    private void getProvinceList() {
        HttpUtil.post(HttpConstant.getProvinceList, new EbingoRequestParmater(this), new EbingoHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                JSONArray array = null;
                try {
                    array = response.getJSONArray("data");
                    JsonUtil.getArray(array, RegionBeen.class, provinceList);
                    FileUtil.saveCache(getApplicationContext(), FileUtil.FILE_PROVINCE_LIST, provinceList);
                    provinceAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(int statusCode, String msg) {

            }

            @Override
            public void onFinish() {

            }
        });
    }


    class ProvinceAdapter extends BaseAdapter {
        private Activity context;
        private final int checkedColor = 0xffe5e5e5;
        private final int unCheckedColor = 0xffffffff;

        ProvinceAdapter(Activity context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return provinceList.size();
        }

        @Override
        public Object getItem(int position) {
            return provinceList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                holder.tv = (TextView) LayoutInflater.from(context).inflate(R.layout.activate_textview, null);
                holder.tv.setTextColor(0xff3a3a3a);
                holder.tv.setTextSize(16);
                convertView = holder.tv;
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv.setText(provinceList.get(position).getName());
            if (selected == position) {
                holder.tv.setBackgroundColor(checkedColor);
            } else {
                holder.tv.setBackgroundColor(unCheckedColor);
            }
            convertView.setTag(holder);
            return convertView;
        }

    }

    class CityAdapter extends BaseAdapter {
        private Activity context;

        CityAdapter(Activity context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return cityList.size();
        }

        @Override
        public Object getItem(int position) {
            return cityList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            LogCat.i(position + "");
            if (convertView == null) {
                holder = new ViewHolder();

                holder.tv = new TextView(context);
                holder.tv.setBackgroundColor(0xfff2f2f2);
                holder.tv.setTextColor(0xff808080);
                holder.tv.setTextSize(15);
                holder.tv.setGravity(Gravity.CENTER_VERTICAL);
                holder.tv.setPadding(dp(20),dp(7),0,dp(7));

                convertView = holder.tv;
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv.setText(cityList.get(position).getName());
            convertView.setTag(holder);
            return convertView;
        }
        public int dp(float value){
            return (int) Dimension.dp(value);
        }
    }


    static class ViewHolder {
        TextView tv;
    }
}
