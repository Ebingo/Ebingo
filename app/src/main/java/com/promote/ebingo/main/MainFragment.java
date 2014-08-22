package com.promote.ebingo.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jch.lib.view.PagerIndicator;
import com.promote.ebingo.R;


public class MainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_PARAM1 = "param1";
    public static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ViewPager mainfragvp;
    private PagerIndicator mainfragpi;
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


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initialize(view);

        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


    private void initialize(View view) {

        mainfragvp = (ViewPager) view.findViewById(R.id.main_frag_vp);
        mainfragpi = (PagerIndicator)  view.findViewById(R.id.main_frag_pi);
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
    }
}
