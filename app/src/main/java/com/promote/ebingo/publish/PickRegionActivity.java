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

import com.promote.ebingo.BaseListActivity;
import com.promote.ebingo.R;
import com.promote.ebingo.util.Dimension;
import com.promote.ebingo.util.LogCat;

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

    private List<String> regionList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        regionList.add("安徽");
        regionList.add("山西");
        regionList.add("四川");
        regionList.add("重庆");
        regionList.add("江苏");
        regionList.add("浙江");
        regionList.add("天津");
        regionList.add("上海");
        regionList.add("北京");
        setListAdapter(new RegionAdapter(this));
        new Thread(new Runnable() {
            @Override
            public void run() {
//                LogCat.i("--->", "city:" + "");
//                String city = getCurrentCityName();
//                handler.sendMessage(handler.obtainMessage(1, city));
//                LogCat.i("--->", "city:" + city);
            }
        }).start();
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            regionList.add(msg.obj + "");
            LogCat.i("--->", msg.obj + "");
            ((RegionAdapter) getListAdapter()).notifyDataSetChanged();
            return false;
        }
    });

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
        data.putExtra("result", regionList.get(position));
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
            holder.tv.setText(regionList.get(position));
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
