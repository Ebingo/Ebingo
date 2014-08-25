package com.promote.ebingo.home;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jch.lib.util.DisplayUtil;
import com.jch.lib.view.PagerIndicator;
import com.promote.ebingo.R;
import com.promote.ebingo.util.LogCat;

import org.w3c.dom.Text;

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
    private TextView mainhote1sttv;
    private TextView mainhote2sttv;
    private TextView mainhote3sttv;
    private TextView mainhote4sttv;
    private TextView mainhote5sttv;
    private TextView mainhote6sttv;
    private ImageView mainhotesptimg;
    private TextView mainhote1spttiteltv;
    private TextView mainhote1spttitlenumtv;
    private TextView mainhote1subtitletv;
    private TextView mainhotespt1subtitlenumtv;
    private ImageView mainhot1sptimg;
    private TextView mainhote2spttiteltv;
    private TextView mainhote2spttitlenumtv;
    private TextView mainhote2subtitletv;
    private TextView mainhotespt2subtitlenumtv;
    private ImageView mainhot2sptimg;
    private ImageView mainhotegetimg;
    private TextView mainhote1gettiteltv;
    private TextView mainhote1gettitlenumtv;
    private TextView mainhoteget1subtitletv;
    private TextView mainhoteget1subtitlenumtv;
    private ImageView mainhot1getimg;
    private TextView mainhote2gettiteltv;
    private TextView mainhote2gettitlenumtv;
    private TextView mainhoteget2subtitletv;
    private TextView mainhoteget2subtitlenumtv;
    private ImageView mainhot2getimg;
    /** banner條滾動schedule. **/
    private ScheduledExecutorService scheduledExecutorService;

    //test data.
    private int imgsRes[] = {R.drawable.test_main_2,R.drawable.test_main_1,R.drawable.test_main_2,R.drawable.test_main_1};

    private int mBannerBaseWidth = 720;
    private int mBannerBaseHeight = 287;

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
        mainhote1sttv = (TextView)  view.findViewById(R.id.main_hote_1st_tv);
        mainhote2sttv = (TextView)  view.findViewById(R.id.main_hote_2st_tv);
        mainhote3sttv = (TextView)  view.findViewById(R.id.main_hote_3st_tv);
        mainhote4sttv = (TextView)  view.findViewById(R.id.main_hote_4st_tv);
        mainhote5sttv = (TextView)  view.findViewById(R.id.main_hote_5st_tv);
        mainhote6sttv = (TextView)  view.findViewById(R.id.main_hote_6st_tv);
        mainhotesptimg = (ImageView)  view.findViewById(R.id.main_hote_spt_img);
        mainhote1spttiteltv = (TextView)  view.findViewById(R.id.main_hote_1spt_titel_tv);
        mainhote1spttitlenumtv = (TextView)  view.findViewById(R.id.main_hote_1spt__title_num_tv);
        mainhote1subtitletv = (TextView)  view.findViewById(R.id.main_hote_1subtitle_tv);
        mainhotespt1subtitlenumtv = (TextView)  view.findViewById(R.id.main_hote_spt__1subtitle_num_tv);
        mainhot1sptimg = (ImageView)  view.findViewById(R.id.main_hot_1spt_img);
        mainhote2spttiteltv = (TextView)  view.findViewById(R.id.main_hote_2spt_titel_tv);
        mainhote2spttitlenumtv = (TextView)  view.findViewById(R.id.main_hote_2spt__title_num_tv);
        mainhote2subtitletv = (TextView)  view.findViewById(R.id.main_hote_2subtitle_tv);
        mainhotespt2subtitlenumtv = (TextView)  view.findViewById(R.id.main_hote_spt__2subtitle_num_tv);
        mainhot2sptimg = (ImageView)  view.findViewById(R.id.main_hot_2spt_img);
        mainhotegetimg = (ImageView)  view.findViewById(R.id.main_hote_get_img);
        mainhote1gettiteltv = (TextView)  view.findViewById(R.id.main_hote_1get_titel_tv);
        mainhote1gettitlenumtv = (TextView)  view.findViewById(R.id.main_hote_1get__title_num_tv);
        mainhoteget1subtitletv = (TextView)  view.findViewById(R.id.main_hote_get_1subtitle_tv);
        mainhoteget1subtitlenumtv = (TextView) view.findViewById(R.id.main_hote_get__1subtitle_num_tv);
        mainhot1getimg = (ImageView)  view.findViewById(R.id.main_hot_1get_img);
        mainhote2gettiteltv = (TextView)  view.findViewById(R.id.main_hote_2get_titel_tv);
        mainhote2gettitlenumtv = (TextView)  view.findViewById(R.id.main_hote_2get__title_num_tv);
        mainhoteget2subtitletv = (TextView)  view.findViewById(R.id.main_hote_get_2subtitle_tv);
        mainhoteget2subtitlenumtv = (TextView)  view.findViewById(R.id.main_hote_get__2subtitle_num_tv);
        mainhot2getimg = (ImageView)  view.findViewById(R.id.main_hot_2get_img);


        mBannerPagerAdapter = new BannerVagerAdapter(imgsRes, getActivity().getApplicationContext());
        mainfragpi.setTotalPage(imgsRes.length);
        mainfragpi.setCurrentPage(0);
        mainfragvp.setAdapter(mBannerPagerAdapter);
        mainfragvp.setCurrentItem(mBannerPagerAdapter.getStartpoiont());
        mainfragvp.setOnPageChangeListener(this);
        DisplayUtil.reSizeViewByScreenWidth(mainfragvp, mBannerBaseWidth, mBannerBaseHeight, getActivity());
        loopPager();
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

        private ArrayList<String> imgsUrls = new ArrayList<String>();
        private Context mContext = null;
        private ArrayList<ImageView> imgs = new ArrayList<ImageView>();
        private int imgsRes[] ;

        public BannerVagerAdapter(ArrayList<String> imgsUrls, Context context){
            this.imgsUrls = imgsUrls;
            this.mContext = context;

            ImageView imgView = new ImageView(context);
            for (int i = 0; i < imgsUrls.size(); i++){
                imgView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                imgView.setBackgroundResource(imgsRes[i]);
                //TODO

                imgs.add(imgView);
            }

        }

        /**
         * @param imgsRes
         * @param context
         */
        public BannerVagerAdapter(int[] imgsRes, Context context){
            this.mContext = context;
            this.imgsRes = imgsRes;

            for (int i = 0; i < imgsRes.length; i++){
                ImageView imgView = new ImageView(context);
                imgView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imgView.setBackgroundResource(imgsRes[i]);
                imgs.add(imgView);
            }

        }

        @Override
        public int getCount() {

            return Integer.MAX_VALUE;
        }



        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        // 代表的是当前传进来的对象，是不是要在我当前页面显示的
        @Override
        public Object instantiateItem(ViewGroup container, int position) {


            int loopPosition = getCurPosition(position);
            ImageView imgView = imgs.get(loopPosition);

            ViewGroup parentView = (ViewGroup) imgView.getParent();
            if (parentView != null){
                parentView.removeView(imgView);
            }
            container.addView(imgView);

            return imgView;// 如果当前显示的View跟你传进来的是同一个View,说明就是要显示的 view
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            container.removeView((View)object);
//            super.destroyItem(container, position, object);
        }

        /**
         * 獲得循環中第幾項。
         * @param position 循环中总的下标.
         * @return
         */
         public int getCurPosition(int position){
             int loopPosition = position % imgsRes.length;
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


}
