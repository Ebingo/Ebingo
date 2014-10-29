package com.promote.ebingo.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jch.lib.util.DisplayUtil;
import com.jch.lib.util.ImageManager;
import com.jch.lib.view.PagerIndicator;
import com.jch.lib.view.PagerScrollView;
import com.jch.lib.view.ScrollListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.promote.ebingo.BaseFragment;
import com.promote.ebingo.InformationActivity.BuyInfoActivity;
import com.promote.ebingo.InformationActivity.CodeScanOnlineActivity;
import com.promote.ebingo.InformationActivity.InterpriseInfoActivity;
import com.promote.ebingo.InformationActivity.ProductInfoActivity;
import com.promote.ebingo.R;
import com.promote.ebingo.bean.Adv;
import com.promote.ebingo.bean.GetIndexBeanTools.GetIndexBean;
import com.promote.ebingo.bean.HotBean;
import com.promote.ebingo.bean.HotCategory;
import com.promote.ebingo.bean.TodayNum;
import com.promote.ebingo.category.CategoryActivity;
import com.promote.ebingo.impl.EbingoRequest;
import com.promote.ebingo.impl.SimpleHomeBean;
import com.promote.ebingo.search.SearchActivity;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.FileUtil;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class HomeFragment extends BaseFragment implements ViewPager.OnPageChangeListener, View.OnClickListener {

    public interface HomeFragmentListener {
        public void moreHotMarket();
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_PARAM1 = "param1";
    public static final String ARG_PARAM2 = "param2";
    /**
     * 主页回调函数. *
     */
    public HomeFragmentListener mHomeFragmentListener = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    /**
     * banner viewpager. *
     */
    private ViewPager mainfragvp;
    /**
     * banner viewpager indicator.*
     */
    private PagerIndicator mainfragpi;
    private BannerVagerAdapter mBannerPagerAdapter = null;
    private TextView mainSearchBarTv;
    private TextView maingetnum;
    private TextView mainsptnumtv;
    private TextView mainpricenumtv;
    private GridView mHotMarketGv;
    private HotMarketAdapter hotMarketAdapter;
    /**
     * 二维码扫描按钮。 *
     */
    private ImageButton mScanIb = null;
    /**
     * banner條滾動schedule. *
     */
    private ScheduledExecutorService scheduledExecutorService;

    private GetIndexBean mIndexBean = null;
    /**
     * 热门市场. *
     */
    private ArrayList<HotCategory> hot_category = new ArrayList<HotCategory>();
    /**
     * 熱門供應. *
     */
    private ArrayList<HotBean> hot_supply = new ArrayList<HotBean>();
    /**
     * 热门需求. *
     */
    private ArrayList<HotBean> hot_demand = new ArrayList<HotBean>();

    /**
     * 廣告大圖的緩存機制. *
     */
    private DisplayImageOptions mOptions;
    /**
     * 圓形小圖片的緩存機制。 *
     */
    private DisplayImageOptions mCircleImageOptions;
    /**
     * 公告条. *
     */
    ArrayList<Adv> mAds = new ArrayList<Adv>();
    /**
     * 热门求购 *
     */
    private HoteBeanAdapter mHotBuyAdapter = null;
    /**
     * 热门供应 *
     */
    private HoteBeanAdapter mHotSupplyAdapter = null;

    private Point imageSize = new Point(320, 118);

    private Point hote_d_s_imgSize = new Point(300, 118);

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
        initImgOperation();
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

    private void initialize(View view) {

        mainSearchBarTv = (TextView) view.findViewById(R.id.search_bar_tv);
        mainfragvp = (ViewPager) view.findViewById(R.id.main_frag_vp);
        mainfragpi = (PagerIndicator) view.findViewById(R.id.main_frag_pi);
        mainfragpi.setCurrentResource(R.drawable.indicator_cur);
        mainfragpi.setDefaultResource(R.drawable.indicator_nomal);
        maingetnum = (TextView) view.findViewById(R.id.main_get_num);
        mainsptnumtv = (TextView) view.findViewById(R.id.main_spt_num_tv);
        mainpricenumtv = (TextView) view.findViewById(R.id.main_price_num_tv);
        homesv = (PagerScrollView) view.findViewById(R.id.home_sv);
        mHotMarketGv = (GridView) view.findViewById(R.id.main_hotmarket_gv);
        mScanIb = (ImageButton) view.findViewById(R.id.scan_ib);

        mHotBuyLv = (ScrollListView) view.findViewById(R.id.home_hot_buy_lv);
        mHotBuyLv.setParentScrollView(homesv);
        mSupplyLv = (ScrollListView) view.findViewById(R.id.home_hotsupply_lv);
        mSupplyLv.setParentScrollView(homesv);

        //初始化默认数据.
        mIndexBean = SimpleHomeBean.initSimpleHomeBean();

        mBannerPagerAdapter = new BannerVagerAdapter(getActivity().getApplicationContext());
        mainfragvp.setAdapter(mBannerPagerAdapter);
        mainfragvp.setOnPageChangeListener(this);
        mainfragpi.setCurrentPage(mBannerPagerAdapter.getCurPosition(mBannerPagerAdapter.getStartpoiont()));
        DisplayUtil.reSizeViewByScreenWidth(mainfragvp, imageSize.x, imageSize.y, getActivity());


        hot_category.addAll(mIndexBean.getHot_category());
        hotMarketAdapter = new HotMarketAdapter(getActivity().getApplicationContext(), hot_category, ContextUtil.getCircleImgOptions());
        mHotMarketGv.setSelector(new BitmapDrawable());
        mHotMarketGv.setOnItemClickListener(new HotMarketOCL());
        mHotMarketGv.setAdapter(hotMarketAdapter);

        hot_demand.addAll(mIndexBean.getHot_demand());
        mHotBuyAdapter = new HoteBeanAdapter(getActivity(), mOptions, mCircleImageOptions, hot_demand, hote_d_s_imgSize);
        mHotBuyLv.setAdapter(mHotBuyAdapter);
        mHotBuyLv.setOnItemClickListener(new HotBuyOCL());

        hot_supply.addAll(mIndexBean.getHot_supply());
        mHotSupplyAdapter = new HoteBeanAdapter(getActivity(), mOptions, mCircleImageOptions, hot_supply, hote_d_s_imgSize);
        mSupplyLv.setAdapter(mHotSupplyAdapter);
        mSupplyLv.setOnItemClickListener(new HotSupplyOCL());

        mainSearchBarTv.setOnClickListener(this);
        mScanIb.setOnClickListener(this);

        loopPager();
        getIndex();

    }

    /**
     * 配置圖片緩存。
     */
    private void initImgOperation() {

        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        mOptions = ContextUtil.getRectangleImgOptions();

        mCircleImageOptions = ContextUtil.getSquareImgOptions();
    }


    /**
     * 自动循环播放banner viewPager.
     */
    private void loopPager() {

        // 指定两秒钟切花一张图片
        scheduledExecutorService.scheduleAtFixedRate(
                new LooperPagerTask(), 10, 7, TimeUnit.SECONDS);

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

            case R.id.scan_ib: {        //二维码扫描。

                scan2Code();
            }

            default: {

            }

        }

    }


    /**
     * 定时滚动banner的Task.
     */
    private class LooperPagerTask implements Runnable {

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

            if (msg.what == LOOPERAGR) {

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
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    /**
     * 頂部循環滾動banner條viewPager適配器。
     */
    class BannerVagerAdapter extends PagerAdapter {

        /**
         * 循环滚动的基本起始项。 *
         */
        private static final int STARTPOIONT = 300;

        //        private ArrayList<String> imgsUrls = new ArrayList<String>();
        private Context mContext = null;
        private ArrayList<ImageView> imgs = new ArrayList<ImageView>();

        public BannerVagerAdapter(Context context) {
            this.mContext = context;

            ImageView imgView = new ImageView(context);

            imgView.setImageDrawable(getResources().getDrawable(R.drawable.loading_big_img));
            LinearLayout.LayoutParams imgLayoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            imgView.setLayoutParams(imgLayoutParam);
            imgs.add(imgView);
        }


        @Override
        public void notifyDataSetChanged() {
            imgs.clear();
            for (int i = 0; i < mAds.size(); i++) {

                ImageView imgView = new ImageView(mContext);
                imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Adv adv = mAds.get(i);
                ImageManager.load(adv.getSrc(), imgView, mOptions);
                setImageViewListner(imgView, adv.getType(), mAds.get(i).getContent());
                imgs.add(imgView);
                super.notifyDataSetChanged();
            }
        }

        /**
         * 给每一个imageView添加点击事件。
         *
         * @param imgView
         * @param AdvType 点击事件类别。
         */
        private void setImageViewListner(ImageView imgView, final int AdvType, final String content) {

            imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    switch (AdvType) {
                        case 1: {            //go 產品詳情頁
                            if (isNetworkConnected()) {
                                Intent imgIntent = new Intent(getActivity(), ProductInfoActivity.class);
                                imgIntent.putExtra(ProductInfoActivity.ARG_ID, Integer.valueOf(content));
                                startActivity(imgIntent);
                            }
                            break;
                        }

                        case 2: {        //go 求购詳情頁

                            if (isNetworkConnected()) {
                                Intent intent = new Intent(getActivity(), BuyInfoActivity.class);
                                intent.putExtra(BuyInfoActivity.DEMAND_ID, Integer.valueOf(content));
                                startActivity(intent);
                            }


                            break;
                        }

                        case 3: {        //go 企業詳情


                            if (isNetworkConnected()) {
                                Intent intent = new Intent(getActivity(), InterpriseInfoActivity.class);
                                intent.putExtra(InterpriseInfoActivity.ARG_ID, Integer.valueOf(content));
                                startActivity(intent);
                                break;
                            }

                        }

                        case 4: {        //外聯web頁面.

                            if (isNetworkConnected()) {
                                Intent intent = new Intent(getActivity(), CodeScanOnlineActivity.class);
//                                intent.setAction("android.intent.action.VIEW");
//                                Uri content_uri = Uri.parse(content);
//                                intent.setData(content_uri);
                                intent.putExtra(CodeScanOnlineActivity.URLSTR, content);
                                startActivity(intent);


                            }
                            break;
                        }

                        default: {

                        }
                    }


                }
            });


        }


        @Override
        public int getCount() {
            if (imgs.size() == 1 || imgs.size() == 0) {
                return 1;
            }
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
            ViewGroup parent = (ViewGroup) imgView.getParent();
            if (parent != null) {
                parent.removeView(imgView);
            }

            container.addView(imgView, 0);


            return imgView;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

        }

        /**
         * 獲得循環中第幾項。
         *
         * @param position 循环中总的下标.
         * @return
         */
        public int getCurPosition(int position) {

            int imgsize = imgs.size();
            if (imgsize == 0) {
                return 0;
            }
            int loopPosition = position % imgs.size();
            return loopPosition;
        }

        /**
         * 獲得循環listView的起始項。
         *
         * @return
         */
        public int getStartpoiont() {
            return STARTPOIONT;
        }

    }

    /**
     * 從服務器獲取首頁信息。
     */
    private void getIndex() {


        EbingoRequest.getHomedata(getActivity(), new EbingoRequest.RequestCallBack<GetIndexBean>() {
            @Override
            public void onFaild(int resultCode, String msg) {
                GetIndexBean indexBean = (GetIndexBean) ContextUtil.read(FileUtil.HOEM_DATA_CACh);
                if (indexBean == null) {
                    Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                } else {
                    initGetData(indexBean);
                }

            }

            @Override
            public void onSuccess(GetIndexBean resultObj) {
                GetIndexBean indexBean = resultObj;
                if (indexBean == null) {
                    indexBean = (GetIndexBean) ContextUtil.read(FileUtil.HOEM_DATA_CACh);
                }
                if (indexBean != null) {
                    initGetData(indexBean);
                }

            }
        });

    }

    private void initGetData(GetIndexBean indexBean) {
        ArrayList<Adv> advs = indexBean.getAds();
        if (advs != null) {
            mAds.clear();
            mAds.addAll(advs);
        }

        ArrayList<HotCategory> hotCategories = indexBean.getHot_category();
        if (hotCategories != null) {
            hot_category.clear();
            hot_category.addAll(hotCategories);
        }

        ArrayList<HotBean> hotDemands = indexBean.getHot_demand();
        if (hotDemands != null) {
            hot_demand.clear();
            hot_demand.addAll(hotDemands);
            mHotBuyAdapter.notifyDataSetChanged();

        }

        ArrayList<HotBean> hotSupplys = indexBean.getHot_supply();
        if (hotSupplys != null) {
            hot_supply.clear();
            hot_supply.addAll(hotSupplys);
            mHotSupplyAdapter.notifyDataSetChanged();
        }

        mIndexBean = indexBean;
        initTodayData();
        setAdvPager();
        initHotMarket();
    }

    /**
     * 设置滚动广告图片.
     */
    private void setAdvPager() {
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
    private void initTodayData() {
        TodayNum todayNum = mIndexBean.getToday_num();
        maingetnum.setText(String.valueOf(todayNum.getDemand_num()));
        mainsptnumtv.setText(String.valueOf(todayNum.getSupply_num()));
        mainpricenumtv.setText(String.valueOf(todayNum.getCall_num()));
    }

    /**
     * 初始化热门市场。
     */
    private void initHotMarket() {

        hotMarketAdapter.notifyDataSetChanged();
    }

    /**
     * 這門市場監聽。
     */
    public class HotMarketOCL implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.adv_item_click);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (isNetworkConnected()) {
                        if (position < hot_category.size()) {
                            HotCategory category = hot_category.get(position);
                            Intent intent = new Intent(getActivity(), CategoryActivity.class);
                            intent.putExtra(CategoryActivity.ARG_ID, category.getId());
                            intent.putExtra(CategoryActivity.ARG_NAME, category.getName());
                            startActivity(intent);
                        } else {        //跳转到发现界面.
                            mHomeFragmentListener.moreHotMarket();
                        }
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            view.startAnimation(animation);


        }
    }

    /**
     * 熱門需求監聽。
     */
    public class HotBuyOCL implements AdapterView.OnItemClickListener {


        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (isNetworkConnected()) {
                HotBean hotBean = hot_demand.get(position);
                Intent intent = new Intent(getActivity(), BuyInfoActivity.class);
                intent.putExtra(BuyInfoActivity.DEMAND_ID, hotBean.getId());
                startActivity(intent);
            }

        }
    }

    /**
     * 热门供应监听
     */
    public class HotSupplyOCL implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (isNetworkConnected()) {
                HotBean hotBean = hot_supply.get(position);
                Intent intent = new Intent(getActivity(), ProductInfoActivity.class);
                intent.putExtra(ProductInfoActivity.ARG_ID, hotBean.getId());
                startActivity(intent);
            }

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mHomeFragmentListener = (HomeFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
        }
        mHomeFragmentListener = null;
    }

}
