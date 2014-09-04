package com.promote.ebingo.publish;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.promote.ebingo.R;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.publish.login.LoginDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link PublishFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class PublishFragment extends Fragment implements RadioGroup.OnCheckedChangeListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private LoginDialog loginDialog;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RadioGroup tabs;
    private ViewPager content;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PublishFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PublishFragment newInstance(String param1, String param2) {
        PublishFragment fragment = new PublishFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public PublishFragment() {
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_publish, container, false);
        tabs=(RadioGroup)view.findViewById(R.id.publish_type );
        content=(ViewPager)view.findViewById(R.id.publish_content);
        tabs.setOnCheckedChangeListener(this);
        PublishContentAdapter adapter=new PublishContentAdapter(getChildFragmentManager(),content,tabs);
        adapter.add(new PublishSupplyFragment()).add(new PublishDemandFragment());
        content.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        showLoginDialog();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
          showLoginDialog();
        }
    }

    private void showLoginDialog(){
        if (loginDialog==null){
            loginDialog=new LoginDialog(getActivity());
//            loginDialog.setCancelable(false);
        }
        if(Company.getInstance().getCompanyId()==null){
            loginDialog.show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.rb_publish_demand:
                content.setCurrentItem(1);
                break;
            case R.id.rb_publish_supply:
                content.setCurrentItem(0);
                break;
        }
    }

    class PublishContentAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

        private RadioGroup tabs;
        private ViewPager content;
        private List<Fragment> fragments;
        public PublishContentAdapter(FragmentManager fm,ViewPager content,RadioGroup tabs) {
            super(fm);
            fragments=new ArrayList<Fragment>();
            this.tabs=tabs;
            this.content=content;
            this.content.setOnPageChangeListener(this);
        }

        public PublishContentAdapter add(Fragment f){
            fragments.add(f);
            return this;
        }

        @Override
        public void onPageScrolled(int i, float v, int i2) {

        }

        @Override
        public void onPageSelected(int i) {
            ((RadioButton)tabs.getChildAt(i)).setChecked(true);
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }
    }
}
