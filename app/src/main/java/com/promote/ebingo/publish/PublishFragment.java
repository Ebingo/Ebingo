package com.promote.ebingo.publish;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.promote.ebingo.R;
import com.promote.ebingo.publish.login.LoginDialog;

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
    private FragmentManager manager;
    private PublishDemandFragment demandFragment;
    private  PublishSupplyFragment supplyFragment;
    private View view;

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
        manager=getChildFragmentManager();
        supplyFragment=new PublishSupplyFragment();
        manager.beginTransaction().add(R.id.publish_content,supplyFragment).commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_publish, container, false);
        RadioGroup group=(RadioGroup)view.findViewById(R.id.publish_type );
        group.setOnCheckedChangeListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (loginDialog==null){
            loginDialog=new LoginDialog(getActivity());
//            loginDialog.setCancelable(false);
        }
        loginDialog.show();;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
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
                    if(demandFragment==null){
                        demandFragment=new PublishDemandFragment();
                    }
                changeFrag(demandFragment,supplyFragment);
                break;
            case R.id.rb_publish_supply:
                changeFrag(supplyFragment,demandFragment);
                break;
        }
    }

    /**
     * 隐藏显示相应的frag，并将设置当前的fragment。
     *
     * @param showFrag 将要显示的frag
     * @param hideFrag 要隐藏的frag。
     */
    private void changeFrag(Fragment showFrag, Fragment hideFrag) {
        FragmentTransaction ft = manager.beginTransaction();
        if (showFrag.isAdded()) {
            ft.show(showFrag);
        } else {
            ft.add(R.id.publish_content, showFrag, null);

        }
        if (hideFrag != null && hideFrag.isAdded()) {
            ft.hide(hideFrag);
        }
        ft.commitAllowingStateLoss();
    }
}
