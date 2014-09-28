package com.promote.ebingo.publish;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jch.lib.util.HttpUtil;
import com.promote.ebingo.BaseListActivity;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.RegionBeen;
import com.promote.ebingo.impl.EbingoHandler;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.Dimension;
import com.promote.ebingo.util.FileUtil;
import com.promote.ebingo.util.JsonUtil;
import com.promote.ebingo.util.LogCat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by acer on 2014/9/9.
 */
public class PickRegionActivity extends BaseListActivity {

    private List<RegionBeen> regionList = new ArrayList<RegionBeen>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void getProvinceList(){
        HttpUtil.post(HttpConstant.getProvinceList,new EbingoRequestParmater(this),new EbingoHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                JSONArray array = null;
                try {
                    array = response.getJSONArray("data");
                    JsonUtil.getArray(array, RegionBeen.class, regionList);
                    getAdapter().notifyDataSetChanged();
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

    private void init() {
        setListAdapter(new RegionAdapter(this));
        enableCache(FileUtil.FILE_PROVINCE_LIST,regionList);
        if (regionList.size()==0)getProvinceList();
    }

    public String getCurrentCityName() {
        String cityName = null;
        final LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_FINE);
//        criteria.setAltitudeRequired(false);
//        criteria.setBearingRequired(false);
//        criteria.setCostAllowed(false);
//        criteria.setPowerRequirement(Criteria.POWER_LOW);
//        manager.setTestProviderEnabled("gps", true);
//        final String provider = manager.getBestProvider(criteria, true);
        Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (location == null) {
            LogCat.e("--->location=null");
            return null;
        }
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Geocoder gc = new Geocoder(this, Locale.getDefault());
        try {
            // 取得地址相关的一些信息\经度、纬度
            List<Address> addresses = gc.getFromLocation(latitude, longitude, 1);
            StringBuilder sb = new StringBuilder();
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                sb.append(address.getLocality()).append("\n");
                cityName = sb.toString();
            }
        } catch (IOException e) {
        }
        LogCat.e("--->cityName" + cityName);

        return cityName;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent data = new Intent();
        data.putExtra("result", regionList.get(position).getName());
        setResult(RESULT_OK, data);
        finish();
    }

    class RegionAdapter extends BaseAdapter {
        private Activity context;


        RegionAdapter(Activity context) {
            this.context = context;

        }

        @Override
        public int getCount() {
            return regionList.size();
        }

        @Override
        public Object getItem(int position) {
            return regionList.get(position);
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
                holder.tv.setLayoutParams(new AbsListView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
                holder.tv.setTextColor(context.getResources().getColor(R.color.black));
                holder.tv.setTextSize(18);
                holder.tv.setClickable(false);
                holder.tv.setBackgroundColor(Color.WHITE);
                holder.tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
                holder.tv.setPadding(dp(12), dp(8), dp(6), dp(8));
                convertView = holder.tv;
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv.setText(regionList.get(position).getName());
            convertView.setTag(holder);
            return convertView;
        }

        private int dp(float value) {
            return (int) Dimension.dp(value);
        }


    }

    static class ViewHolder {
        TextView tv;
    }
}
