package com.promote.ebingo.InformationActivity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.jch.lib.util.InstanceFragmentAdapter;
import com.promote.ebingo.R;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link com.promote.ebingo.InformationActivity.SupplyDemandInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 * <p/>
 * 供求信息。
 */
public class SupplyDemandInfoFragment extends InterpriseBaseFragment implements RadioGroup.OnCheckedChangeListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FrameLayout fragsdfram;
    private RadioButton fragiprisesupplyll;
    private RadioButton fragiprisedemandll;
    private RadioGroup fragiprisesdcg;
    private String mSupplyFragmentName = "supplyFragmentName";
    private String mDemandFragmentName = "demandFragmentName";


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment SupplyDemandInfo.
     */
    // TODO: Rename and change types and number of parameters
    public static SupplyDemandInfoFragment newInstance(String param1) {
        SupplyDemandInfoFragment fragment = new SupplyDemandInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public SupplyDemandInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_supply_demand_info, container, false);

        initialize(view);
        return view;
    }


    private void initialize(View view) {
        fragiprisesdcg = (RadioGroup) view.findViewById(R.id.frag_iprise_s_d_cg);
        fragsdfram = (FrameLayout) view.findViewById(R.id.frag_s_d_fram);
        fragiprisesupplyll = (RadioButton) view.findViewById(R.id.frag_iprise_supply_ll);
        fragiprisedemandll = (RadioButton) view.findViewById(R.id.frag_iprise_demand_ll);

        fragiprisesdcg.setOnCheckedChangeListener(this);
        InstanceFragmentAdapter ifa = InstanceFragmentAdapter.newInstance(getChildFragmentManager());
        ifa.replaceFramgent(R.id.frag_s_d_fram, mDemandFragmentName, mSupplyFragmentName, new InstanceFragmentAdapter.FragmentFactory() {
            @Override
            public Fragment createFragment() {
                return InterpriseSupplyInfo.newInstance(getInterprsetId(), null);

            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        FragmentManager fm = getChildFragmentManager();
        switch (checkedId) {
            case R.id.frag_iprise_supply_ll: {


                InstanceFragmentAdapter ifa = InstanceFragmentAdapter.newInstance(fm);
                ifa.replaceFramgent(R.id.frag_s_d_fram, mDemandFragmentName, mSupplyFragmentName, new InstanceFragmentAdapter.FragmentFactory() {
                    @Override
                    public Fragment createFragment() {
                        return InterpriseSupplyInfo.newInstance(getInterprsetId(), null);

                    }
                });

                break;
            }

            case R.id.frag_iprise_demand_ll: {

                InstanceFragmentAdapter ifa = InstanceFragmentAdapter.newInstance(fm);
                ifa.replaceFramgent(R.id.frag_s_d_fram, mSupplyFragmentName, mDemandFragmentName, new InstanceFragmentAdapter.FragmentFactory() {
                    @Override
                    public Fragment createFragment() {
                        return InterpriseDemandInfo.newInstance(getInterprsetId(), null);

                    }
                });

                break;
            }
            default: {

            }
        }

    }


}
