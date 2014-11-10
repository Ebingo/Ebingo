package com.promote.ebingoo.publish;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.jch.lib.util.ImageManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.promote.ebingoo.BaseActivity;
import com.promote.ebingoo.R;
import com.promote.ebingoo.util.ContextUtil;
import com.promote.ebingoo.util.Dimension;
import com.promote.ebingoo.util.LogCat;

import java.io.File;
import java.util.LinkedList;

public class PhotoAlbumActivity extends BaseActivity implements AdapterView.OnItemClickListener {


    GridView grid;
    private LinkedList<String> urls = new LinkedList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_activity);
        grid = (GridView) findViewById(R.id.grid);
        LogCat.i("onCreate");
        loadPictures();
    }

    private void loadPictures() {
        String[] projection = {
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.Media._ID
        };

        Cursor cursor = managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, null, null,
                MediaStore.Images.Media._ID);

        while (cursor.moveToNext()) {
            urls.add(cursor.getString(0));
        }
        cursor.close();
        final AlbumAdapter adapter = new AlbumAdapter(this);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            Intent intent=new Intent(this,CameraActivity.class);
            startActivity(intent);
        }else{
            Intent data = new Intent();
            data.setData(Uri.fromFile(new File(parent.getItemAtPosition(position) + "")));
            setResult(RESULT_OK, data);
            finish();
        }


    }

    class AlbumAdapter extends BaseAdapter {
        private Context mContext;

        DisplayImageOptions options;
        View header;
        int width;

        AlbumAdapter(Context context) {
            this.mContext = context;
            options = ContextUtil.getSquareImgOptions();
            header = LayoutInflater.from(mContext).inflate(R.layout.camera_header, null);
            width = (int) (getWindowManager().getDefaultDisplay().getWidth() / 3 - Dimension.dp(1));
            header.setLayoutParams(new AbsListView.LayoutParams(width, width));
        }

        @Override
        public int getCount() {
            return urls.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            if (position == 0) {
                return "";
            } else {
                return urls.get(position - 1);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                convertView = header;
                header.setTag(null);
            } else {
                ViewHolder holder;
                if (convertView == null || convertView.getTag() == null) {
                    holder = new ViewHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_photo, null);
                    holder.imageView = (ImageView) convertView.findViewById(R.id.image);
                    holder.imageView.setLayoutParams(new AbsListView.LayoutParams(width, width));
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                ImageManager.load("file://" + urls.get(position - 1), holder.imageView, options);
                convertView.setTag(holder);
            }

            return convertView;
        }
    }

    static class ViewHolder {
        ImageView imageView;
    }
}