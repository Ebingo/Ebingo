package com.promote.ebingoo.find;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jch.lib.util.ImageManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.promote.ebingoo.BaseFragment;
import com.promote.ebingoo.R;
import com.promote.ebingoo.bean.CategoryBeen;
import com.promote.ebingoo.category.CategoryActivity;
import com.promote.ebingoo.impl.EbingoRequest;
import com.promote.ebingoo.search.SearchActivity;
import com.promote.ebingoo.util.ContextUtil;
import com.promote.ebingoo.util.FileUtil;

import java.util.ArrayList;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link com.promote.ebingoo.find.FindFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    /**
     * 二维码扫描按钮。 *
     */
    private ImageButton mScanIb = null;

    private GridView fragfindgv;
    private LinearLayout searchll;
    private ImageView searchlogoimg;
    private TextView searchbartv;
    private TextView mNoDataView;
    private ArrayList<CategoryBeen> mCategoryBeens = new ArrayList<CategoryBeen>();

    private MyGrideAdapter adapter = null;
    /**
     * 廣告大圖的緩存機制. *
     */
    private DisplayImageOptions mOptions;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FindFragment.
     */
    public static FindFragment newInstance(String param1, String param2) {
        FindFragment fragment = new FindFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FindFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        mOptions = ContextUtil.getCircleImgOptions();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_find, container, false);
        initialize(view);
        return view;

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
//            fragfindgv.startLayoutAnimation();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void initialize(ViewGroup view) {
        searchlogoimg = (ImageView) view.findViewById(R.id.search_logo_img);
        searchbartv = (TextView) view.findViewById(R.id.search_bar_tv);
        fragfindgv = (GridView) view.findViewById(R.id.frag_find_gv);
        fragfindgv.setSelector(new BitmapDrawable());
        mNoDataView = (TextView) view.findViewById(R.id.nodate_tv);
        mScanIb = (ImageButton) view.findViewById(R.id.scan_ib);

        mNoDataView.setText(getResources().getString(R.string.refresh));
        String[] categorys = getResources().getStringArray(R.array.category_data);
        adapter = new MyGrideAdapter(getActivity().getApplicationContext());
        fragfindgv.setAdapter(adapter);
        searchbartv.setOnClickListener(this);
        fragfindgv.setOnItemClickListener(this);
        mNoDataView.setOnClickListener(this);
        mScanIb.setOnClickListener(this);
        getCategoryList();
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.search_bar_tv: {

                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.scan_ib: {
                scan2Code();
                break;
            }

            case R.id.nodate_tv: {

                getCategoryList();
                break;
            }
            default: {

            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.adv_item_click);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra(CategoryActivity.ARG_ID, mCategoryBeens.get(position).getId());
                intent.putExtra(CategoryActivity.ARG_NAME, mCategoryBeens.get(position).getName());
                startActivity(intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.findViewById(R.id.find_item_ll).startAnimation(animation);


    }


    /**
     * category gridview adapter.
     */
    private class MyGrideAdapter extends BaseAdapter {

        Context context = null;

        public MyGrideAdapter(Context context) {

            this.context = context;
        }


        @Override
        public int getCount() {
            return mCategoryBeens.size();
        }

        @Override
        public Object getItem(int position) {
            return mCategoryBeens.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {

                holder = new ViewHolder();
                convertView = (View) LayoutInflater.from(this.context).inflate(R.layout.find_gridview_item, null);
                holder.imgView = (ImageView) convertView.findViewById(R.id.find_grid_item_img);
                holder.text = (TextView) convertView.findViewById(R.id.find_grid_item_tv);
                convertView.setTag(holder);
                convertView.setOnTouchListener(new GradItemCLS());

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            CategoryBeen categoryBeen = mCategoryBeens.get(position);
            ImageManager.load(categoryBeen.getImage(), holder.imgView, mOptions);
            holder.text.setText(categoryBeen.getName());

            return convertView;
        }

    }

    private class GradItemCLS implements View.OnTouchListener {


        @Override
        public boolean onTouch(View v, MotionEvent event) {


            int action = event.getAction();
            switch (action) {

                case MotionEvent.ACTION_DOWN: {

                    break;
                }
                case MotionEvent.ACTION_UP: {

                    break;
                }

                default: {

                }
            }

            return false;
        }
    }

    private void noData() {
        mNoDataView.setVisibility(View.VISIBLE);
        fragfindgv.setVisibility(View.GONE);
    }

    private void haseData() {
        mNoDataView.setVisibility(View.GONE);
        fragfindgv.setVisibility(View.VISIBLE);
    }

    /**
     * 从网络获取数据。
     */
    private void getCategoryList() {
        haseData();
        EbingoRequest.getCategoryList(getActivity(), new EbingoRequest.RequestCallBack<ArrayList<CategoryBeen>>() {
            @Override
            public void onFaild(int resultCode, String msg) {

                ArrayList<CategoryBeen> categoryBeens = (ArrayList<CategoryBeen>) ContextUtil.read(FileUtil.CATEGORY_CACH);

                if (categoryBeens == null || categoryBeens.size() == 0) {
                    //nodata
                    noData();
                } else {
                    mCategoryBeens.clear();
                    mCategoryBeens.addAll(categoryBeens);
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onSuccess(ArrayList<CategoryBeen> resultObj) {
                mCategoryBeens.clear();
                mCategoryBeens.addAll(resultObj);
                adapter.notifyDataSetChanged();
            }
        });

    }


    static class ViewHolder {
        ImageView imgView;
        TextView text;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
