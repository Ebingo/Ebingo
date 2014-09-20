package com.promote.ebingo.InformationActivity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.promote.ebingo.R;

import java.lang.reflect.Field;

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
    private InterpriseDemandInfo demandInfo = null;
    private InterpriseSupplyInfo supplyInfo = null;

    /**
     * 本activity是否已经运行过. *
     */
    private boolean isRunned = false;


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

        addFragment();

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
        displayFragmentByChecked(fragiprisesdcg.getCheckedRadioButtonId());
    }

    /**
     * 将供应和求购的fragment添加到view中.
     */
    private void addFragment() {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        supplyInfo = InterpriseSupplyInfo.newInstance(getInterprsetId(), null);
        demandInfo = InterpriseDemandInfo.newInstance(getInterprsetId(), null);
        fragmentTransaction.add(R.id.frag_s_d_fram, demandInfo, mSupplyFragmentName);
        fragmentTransaction.add(R.id.frag_s_d_fram, supplyInfo, mDemandFragmentName);
        fragmentTransaction.hide(supplyInfo);
        fragmentTransaction.hide(demandInfo);
        fragmentTransaction.commitAllowingStateLoss();
        fragmentTransaction = null;
        fragmentManager.executePendingTransactions();
    }

    /**
     * 显示隐藏fragment。
     *
     * @param hidFramgent
     * @param showFragment
     */
    private void displayFramgent(Fragment hidFramgent, Fragment showFragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.hide(hidFramgent);
        ft.show(showFragment);
        ft.commitAllowingStateLoss();
        ft = null;
        fragmentManager.executePendingTransactions();

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 根据单选按钮显示供应或求购.
     *
     * @param checkId
     */
    private void displayFragmentByChecked(int checkId) {
        FragmentManager fm = getChildFragmentManager();
        switch (checkId) {
            case R.id.frag_iprise_supply_ll: {
                displayFramgent(demandInfo, supplyInfo);

                break;
            }

            case R.id.frag_iprise_demand_ll: {

                displayFramgent(supplyInfo, demandInfo);

                break;
            }
            default: {

            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        displayFragmentByChecked(checkedId);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }
}
