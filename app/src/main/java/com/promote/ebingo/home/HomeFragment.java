package com.promote.ebingo.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.DisplayUtil;
import com.jch.lib.util.HttpUtil;
import com.jch.lib.util.ImageManager;
import com.jch.lib.view.PagerIndicator;
import com.jch.lib.view.PagerScrollView;
import com.jch.lib.view.ScrollListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.promote.ebingo.R;
import com.promote.ebingo.bean.Adv;
import com.promote.ebingo.bean.GetIndexBeanTools;
import com.promote.ebingo.bean.GetIndexBeanTools.GetIndexBean;
import com.promote.ebingo.bean.HotBean;
import com.promote.ebingo.bean.HotCategory;
import com.promote.ebingo.bean.TodayNum;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.search.SearchActivity;
import com.promote.ebingo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class HomeFragment extends Fragment implements ViewPager.OnPageChangeListener, View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_PARAM1 = "param1";
    public static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    /** banner viewpager. **/
    private ViewPager mainfragvp;
    /** banner viewpager indicator.**/
    private PagerIndicator mainfragpi;
    private BannerVagerAdapter mBannerPagerAdapter = null;
    private TextView mainSearchBarTv;
    private TextView maingetnum;
    private TextView mainsptnumtv;
    private TextView mainpricenumtv;
    private GridView mHotMarketGv;
    private HotMarketAdapter hotMarketAdapter;
    /** banner條滾動schedule. **/
    private ScheduledExecutorService scheduledExecutorService;

    private GetIndexBean mIndexBean = null;
    /** 热门市场. **/
    private ArrayList<HotCategory> hot_category = new ArrayList<HotCategory>();
    /** 熱門供應. **/
    private ArrayList<HotBean> hot_supply = new ArrayList<HotBean>();
    /** 热门需求. **/
    private ArrayList<HotBean> hot_demand = new ArrayList<HotBean>();

    /** 廣告大圖的緩存機制. **/
    private DisplayImageOptions mOptions;
    /** 圓形小圖片的緩存機制。 **/
    private DisplayImageOptions mCircleImageOptions;
    /** 公告条. **/
    ArrayList<Adv> mAds = new ArrayList<Adv>();
    /** 热门求购 **/
    private HoteBeanAdapter mHotBuyAdapter = null;
    /** 热门供应 **/
    private HoteBeanAdapter mHotSupplyAdapter = null;
    //test data.
    private int imgsRes[] = {R.drawable.test_main_2, R.drawable.test_main_1, R.drawable.test_main_2,R.drawable.test_main_1, R.drawable.test_main_2};

//    private int mBannerBaseWidth = 720;
//    private int mBannerBaseHeight = 287;
    private Point imageSize = new Point(720, 287);
    private TextView mainhote8sttv;
    private TextView mainhote7sttv;

    private ScrollListView mHotBuyLv = null;
    private ScrollListView mSupplyLv = null;
    private PagerScrollView homesv;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        scheduledExecutorService = Executors
                .newSingleThreadScheduledExecutor();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initialize(view);

        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
        }
    }


    private void initialize(View view) {
        mainSearchBarTv = (TextView) view.findViewById(R.id.search_bar_tv);
        mainfragvp = (ViewPager) view.findViewById(R.id.main_frag_vp);
        mainfragpi = (PagerIndicator)  view.findViewById(R.id.main_frag_pi);
        mainfragpi.setCurrentResource(R.drawable.indicator_cur);
        mainfragpi.setDefaultResource(R.drawable.indicator_nomal);
        maingetnum = (TextView)  view.findViewById(R.id.main_get_num);
        mainsptnumtv = (TextView)  view.findViewById(R.id.main_spt_num_tv);
        mainpricenumtv = (TextView)  view.findViewById(R.id.main_price_num_tv);
        homesv = (PagerScrollView) view.findViewById(R.id.home_sv);
        mHotMarketGv = (GridView) view.findViewById(R.id.main_hotmarket_gv);

        mHotBuyLv = (ScrollListView) view.findViewById(R.id.home_hot_buy_lv);
        mHotBuyLv.setParentScrollView(homesv);
        mSupplyLv = (ScrollListView) view.findViewById(R.id.home_hotsupply_lv);
        mSupplyLv.setParentScrollView(homesv);

        mBannerPagerAdapter = new BannerVagerAdapter(getActivity().getApplicationContext());
        mainfragvp.setAdapter(mBannerPagerAdapter);
        mainfragvp.setOnPageChangeListener(this);
        mainfragpi.setCurrentPage(mBannerPagerAdapter.getCurPosition(mBannerPagerAdapter.getStartpoiont()));
        DisplayUtil.reSizeViewByScreenWidth(mainfragvp, imageSize.x, imageSize.y, getActivity());

        hotMarketAdapter = new HotMarketAdapter(getActivity().getApplicationContext(), hot_category, mOptions);
        mHotMarketGv.setSelector(new BitmapDrawable());
        mHotMarketGv.setOnItemClickListener(new HotMarketOCL());
        mHotMarketGv.setAdapter(hotMarketAdapter);

        mHotBuyAdapter = new HoteBeanAdapter(getActivity(), mOptions, mCircleImageOptions, hot_demand, imageSize);
        mHotBuyLv.setAdapter(mHotBuyAdapter);
        mHotBuyLv.setOnItemClickListener(new HotBuyOCL());
        mHotSupplyAdapter = new HoteBeanAdapter(getActivity(), mOptions, mCircleImageOptions, hot_supply, imageSize);
        mSupplyLv.setAdapter(mHotSupplyAdapter);
        mSupplyLv.setOnItemClickListener(new HotSupplyOCL());

        mainSearchBarTv.setOnClickListener(this);

        loopPager();
        getIndex();
        initImgOperation();
    }

    /**
     * 配置圖片緩存。
     */
    private void initImgOperation(){

        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        mOptions = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .showImageForEmptyUri(R.drawable.loading)
                .showImageOnLoading(R.drawable.loading)
                .showImageOnFail(R.drawable.loading)
                .cacheInMemory(true).cacheOnDisc(true).build();

        mCircleImageOptions = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .showImageForEmptyUri(R.drawable.circle_img)
                .showImageOnLoading(R.drawable.circle_img)
                .showImageOnFail(R.drawable.circle_img)
                .cacheInMemory(true).cacheOnDisc(true).build();
    }


    /**
     * 自动循环播放banner viewPager.
     */
    private void loopPager(){

        // 指定两秒钟切花一张图片
        scheduledExecutorService.scheduleAtFixedRate(
                new LooperPagerTask(), 10, 7, TimeUnit.SECONDS);

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id){
            case R.id.search_bar_tv:{

                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);

                break;
            }

            default:{

            }

        }

    }


    /**
     * 定时滚动banner的Task.
     */
    private class LooperPagerTask implements  Runnable{

        @Override
        public void run() {

            int curentItem = mainfragvp.getCurrentItem();
            curentItem++;
            Message msg = new Message();
            msg.what = LOOPERAGR;
            msg.arg1 = curentItem;
            looperHandler.sendMessage(msg);
        }
    }

    private final static int LOOPERAGR = 1;

    /**
     * 循環滾動banner的handler
     */
    private Handler looperHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == LOOPERAGR){

                int curItem = msg.arg1;
                mainfragvp.setCurrentItem(curItem);

            }


        }
    };


    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        mainfragpi.setCurrentPage(mBannerPagerAdapter.getCurPosition(i));
        LogCat.d("org Position--:"+ i+"--- loop position--:"+mBannerPagerAdapter.getCurPosition(i));
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    /**
     * 頂部循環滾動banner條viewPager適配器。
     */
    class BannerVagerAdapter extends PagerAdapter{

        /** 循环滚动的基本起始项。 **/
        private static final int STARTPOIONT = 300;

//        private ArrayList<String> imgsUrls = new ArrayList<String>();
        private Context mContext = null;
        private ArrayList<ImageView> imgs = new ArrayList<ImageView>();

        public BannerVagerAdapter(Context context){
            this.mContext = context;

                ImageView imgView = new ImageView(context);
                imgView.setScaleType(ImageView.ScaleType.CENTER);
                imgView.setBackgroundResource(R.drawable.loading);
                imgs.add(imgView);
            }


        @Override
        public void notifyDataSetChanged() {
            imgs.clear();
            for (int i = 0; i < mAds.size(); i++) {
                ImageView imgView = new ImageView(mContext);
                imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                imgView.setBackgroundResource(imgsRes[i]);
                ImageManager.load(mAds.get(i).getSrc(), imgView, mOptions);
                setImageViewListner(imgView, mAds.get(i).getType());
                imgs.add(imgView);
                super.notifyDataSetChanged();
            }
        }

        /**
         * 给每一个imageView添加点击事件。
         * @param imgView
         * @param AdvType   点击事件类别。
         */
        private void setImageViewListner(ImageView imgView, int AdvType){
            final Intent intent = new Intent();
            switch (AdvType){
//               intent.setClass()
            }

            imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    startActivity(intent);
                }
            });




        }



        @Override
        public int getCount() {

            return Integer.MAX_VALUE;
        }



        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;       //如果当前显示的View跟你传进来的是同一个View,说明就是要显示的 view
        }

        // 代表的是当前传进来的对象，是不是要在我当前页面显示的
        @Override
        public Object instantiateItem(ViewGroup container, int position) {


            int loopPosition = getCurPosition(position);
            ImageView imgView = imgs.get(loopPosition);
            ViewGroup parent = (ViewGroup)imgView.getParent();
            if (parent != null){
                parent.removeView(imgView);
            }

            container.addView(imgView, 0);


            return imgView;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

//            container.removeView((View)object);
//            super.destroyItem(container, position, object);
        }

        /**
         * 獲得循環中第幾項。
         * @param position 循环中总的下标.
         * @return
         */
         public int getCurPosition(int position){
             int loopPosition = position % imgs.size();
             return loopPosition;
         }

        /**
         * 獲得循環listView的起始項。
         * @return
         */
         public int getStartpoiont(){
             return STARTPOIONT;
         }

    }

    /**
     * 從服務器獲取首頁信息。
     */
    private void getIndex(){

        final ProgressDialog dialog = DialogUtil.waitingDialog(getActivity());
        EbingoRequestParmater parma = new EbingoRequestParmater(getActivity().getApplicationContext());
        parma.put("company_id", 0);

        HttpUtil.post(HttpConstant.getIndex, parma, new JsonHttpResponseHandler("utf-8"){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                LogCat.d("home data -- : " + response.toString());

                GetIndexBean indexBean=  GetIndexBeanTools.getIndexBeanJson(response.toString());
                ArrayList<Adv> advs = indexBean.getAds();
                if (advs != null){
                    mAds.addAll(advs);
                }

                ArrayList<HotCategory> hotCategories = indexBean.getHot_category();
                if(hotCategories != null){
                    hot_category.addAll(hotCategories);
                }

                ArrayList<HotBean> hotDemands = indexBean.getHot_demand();
                if (hotDemands != null){
                    hot_demand.addAll(hotDemands);
                    mHotBuyAdapter.notifyDataSetChanged();
                }

                ArrayList<HotBean> hotSupplys = indexBean.getHot_supply();
               if (hotSupplys != null){
                   hot_supply.addAll(hotSupplys);
                   mHotSupplyAdapter.notifyDataSetChanged();
               }

                mIndexBean = indexBean;
                initTodayData();
                setAdvPager();
                initHotMarket();

                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                dialog.dismiss();
            }
        });

    }

    /**
     * 设置滚动广告图片.
     */
    private void setAdvPager(){
        mainfragvp.removeAllViews();
        mBannerPagerAdapter.notifyDataSetChanged();
        mainfragpi.setTotalPage(mAds.size());
        mainfragpi.setCurrentPage(mBannerPagerAdapter.getCurPosition(mBannerPagerAdapter.getStartpoiont()));
        mainfragvp.setOnPageChangeListener(this);
        mainfragvp.setCurrentItem(mBannerPagerAdapter.getStartpoiont());
    }

    /**
     * 初始化今日数据。
     */
    private void initTodayData(){
        TodayNum todayNum = mIndexBean.getToday_num();
        maingetnum.setText(String.valueOf(todayNum.getDemand_num()));
        mainsptnumtv.setText(String.valueOf(todayNum.getSupply_num()));
        mainpricenumtv.setText(String.valueOf(todayNum.getCall_num()));
    }

    /**
     * 初始化热门市场。
     */
    private void initHotMarket(){

        hotMarketAdapter.notifyDataSetChanged();
    }

    /**
     * 這門市場監聽。
     */
    public class HotMarketOCL implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //TODO
            HotCategory category = hot_category.get(position);

        }
    }

    /**
     * 熱門需求監聽。
     */
    public class HotBuyOCL implements AdapterView.OnItemClickListener{


        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    }

    /**
     * 热门供应监听
     */
    public class HotSupplyOCL implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    }



}
