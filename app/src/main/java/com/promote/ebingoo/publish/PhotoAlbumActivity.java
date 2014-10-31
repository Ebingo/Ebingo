package com.promote.ebingoo.publish;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;

import com.promote.ebingoo.R;

/**
 * Android实现获取本机中所有图片
 * @Description: Android实现获取本机中所有图片

 * @FileName: MyDevicePhotoActivity.java

 * @Package com.device.photo

 * @Author Hanyonglu

 * @Date 2012-5-10 下午04:43:55 

 * @Version V1.0
 */
public class PhotoAlbumActivity extends FragmentActivity implements LoaderCallbacks<Cursor>{
    private Bitmap bitmap = null;
    private byte[] mContent = null;

    private ListView listView = null;
    private SimpleCursorAdapter simpleCursorAdapter = null;

    private static final String[] STORE_IMAGES = {
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.LATITUDE,
            MediaStore.Images.Media.LONGITUDE,
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.INTERNAL_CONTENT_URI.toString(),
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString(),
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_activity);

        listView = (ListView)findViewById(android.R.id.list);
        simpleCursorAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                null,
                STORE_IMAGES,
                new int[] {android.R.id.text1, android.R.id.text2});

        simpleCursorAdapter.setViewBinder(new ImageLocationBinder());
        listView.setAdapter(simpleCursorAdapter);
        // 注意此处是getSupportLoaderManager()，而不是getLoaderManager()方法。
        getSupportLoaderManager().initLoader(0, null, this);

        // 单击显示图片
        listView.setOnItemClickListener(new ShowItemImageOnClickListener());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        // TODO Auto-generated method stub
        // 为了查看信息，需要用到CursorLoader。
        CursorLoader cursorLoader = new CursorLoader(
                this,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                STORE_IMAGES,
                null,
                null,
                null);
        return cursorLoader;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub
        simpleCursorAdapter.swapCursor(null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
        // TODO Auto-generated method stub
        // 使用swapCursor()方法，以使旧的游标不被关闭．
        simpleCursorAdapter.swapCursor(cursor);
    }

    // 将图片的位置绑定到视图
    private class ImageLocationBinder implements ViewBinder{
        @Override
        public boolean setViewValue(View view, Cursor cursor, int arg2) {
            // TODO Auto-generated method stub
            if (arg2 == 1) {
                // 图片经度和纬度
                double latitude = cursor.getDouble(arg2);
                double longitude = cursor.getDouble(arg2 + 1);

                if (latitude == 0.0 && longitude == 0.0) {
                    ((TextView)view).setText("位置：未知");
                } else {
                    ((TextView)view).setText("位置：" + latitude + ", " + longitude);
                }

                // 需要注意：在使用ViewBinder绑定数据时，必须返回真；否则，SimpleCursorAdapter将会用自己的方式绑定数据。
                return true;
            } else {
                return false;
            }
        }
    }

    // 单击项显示图片事件监听器
    private class ShowItemImageOnClickListener implements OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {


//            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().
//                    appendPath(Long.toString(id)).build();
//            FileUtil file = new FileUtil();
//            ContentResolver resolver = getContentResolver();
//
//            // 从Uri中读取图片资源
//            try {
//                mContent = file.readInputStream(resolver.openInputStream(Uri.parse(uri.toString())));
//                bitmap = file.getBitmapFromBytes(mContent, null);
//                ivImageShow.setImageBitmap(bitmap);
//            } catch (Exception e) {
//                // TODO: handle exception
//                e.printStackTrace();
//            }

        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        if(bitmap != null){
            bitmap.recycle();
        }
    }
}