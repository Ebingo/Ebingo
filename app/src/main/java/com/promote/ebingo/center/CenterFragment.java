package com.promote.ebingo.center;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.bean.CurCompanyNumBeanTools;
import com.promote.ebingo.bean.CurrentCompanyBaseNumBean;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.publish.login.LoginActivity;
import com.promote.ebingo.util.Dimension;
import com.promote.ebingo.util.ImageUtil;
import com.promote.ebingo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.promote.ebingo.center.CenterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link com.promote.ebingo.center.CenterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CenterFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ImageView centerheadiv;
    private TextView centerloginbtn;
    private LinearLayout centtopimgll;
    private TextView centersupplynumtv;
    private TextView centerdemandnumtv;
    private TextView centercollectnumtv;
    private TextView centermsgnumtv;
    private TextView centsupplytv;
    private TextView centdemandtv;
    private TextView centcollettv;
    private TextView centbooktv;
    private TextView centprivilegetv;
    private TextView centtellhistorytv;
    private TextView centsettingtv;
    private TextView centprofiletv;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CenterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CenterFragment newInstance(String param1, String param2) {
        CenterFragment fragment = new CenterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public CenterFragment() {
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
        View view = inflater.inflate(R.layout.fragment_center, container, false);

        initialize(view);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onResume() {

        setHeadImage(Company.getInstance().getImageUri());
        super.onResume();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getCurrentCompanyBaseNum();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initialize(View view) {

        centerheadiv = (ImageView) view.findViewById(R.id.center_head_iv);
        centerloginbtn = (TextView) view.findViewById(R.id.center_login_btn);
        centtopimgll = (LinearLayout) view.findViewById(R.id.cent_top_img_ll);
        centersupplynumtv = (TextView) view.findViewById(R.id.center_supply_num_tv);
        centerdemandnumtv = (TextView) view.findViewById(R.id.center_demand_num_tv);
        centercollectnumtv = (TextView) view.findViewById(R.id.center_collect_num_tv);
        centermsgnumtv = (TextView) view.findViewById(R.id.center_msg_num_tv);
        centsupplytv = (TextView) view.findViewById(R.id.cent_supply_tv);
        centdemandtv = (TextView) view.findViewById(R.id.cent_demand_tv);
        centcollettv = (TextView) view.findViewById(R.id.cent_collet_tv);
        centbooktv = (TextView) view.findViewById(R.id.cent_book_tv);
        centprivilegetv = (TextView) view.findViewById(R.id.cent_privilege_tv);
        centtellhistorytv = (TextView) view.findViewById(R.id.cent_tell_history_tv);
        centsettingtv = (TextView) view.findViewById(R.id.cent_setting_tv);
        centprofiletv = (TextView) view.findViewById(R.id.cent_profile_tv);

        centerloginbtn.setOnClickListener(this);
        centprivilegetv.setOnClickListener(this);
        setHeadImage(Company.getInstance().getImageUri());
    }


    /**
     * 设置头像
     *
     * @param uri
     */
    public void setHeadImage(Uri uri) {
        if (uri == null) {
            LogCat.e("--->", "setHeadImage uriError uri=" + uri);
            return;
        }

        try {
            Bitmap bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
            centerheadiv.setImageBitmap(ImageUtil.roundBitmap(bm, (int) Dimension.dp(48)));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.center_head_iv: {

                if (isLogined()) {

                } else {

                }

                break;
            }

            case R.id.center_login_btn: {

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);

                break;
            }

            case R.id.cent_top_img_ll: {

                break;
            }
            case R.id.center_supply_num_tv: {


                break;
            }
            case R.id.center_demand_num_tv: {

                break;
            }
            case R.id.center_collect_num_tv: {

                break;
            }
            case R.id.center_msg_num_tv: {

                break;
            }
            case R.id.cent_supply_tv: {
                Intent intent = new Intent(getActivity(), MySupplyActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.cent_demand_tv: {
                Intent intent = new Intent(getActivity(), MyDemandActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.cent_collet_tv: {
                Intent intent = new Intent(getActivity(), MyCollectionActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.cent_book_tv: {
                Intent intent = new Intent(getActivity(), MyBookActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.cent_privilege_tv: {
                Intent intent = new Intent(getActivity(), MyPrivilegeActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.cent_profile_tv: {

                break;
            }

            case R.id.cent_setting_tv: {

                break;
            }
            case R.id.cent_tell_history_tv: {

                break;
            }
            default: {

            }
        }

    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    /**
     * 获取当前登录公司的基本参数
     */
    private void getCurrentCompanyBaseNum() {

        String urlStr = HttpConstant.getCurrentCompanyBaseNum;
        EbingoRequestParmater parmater = new EbingoRequestParmater(getActivity().getApplicationContext());
        parmater.put("company_id", Company.getInstance().getCompanyId());
        final ProgressDialog dialog = DialogUtil.waitingDialog(getActivity());

        HttpUtil.post(urlStr, parmater, new JsonHttpResponseHandler("UTF-8") {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                CurCompanyNumBeanTools curCompanyNumBeanTools = CurCompanyNumBeanTools.getCurConpanyNumBeanTools(response.toString());

                if (curCompanyNumBeanTools != null) {
                    if (curCompanyNumBeanTools.getCode() == 100) {

                        CurrentCompanyBaseNumBean currentCompanyBaseNumBean = curCompanyNumBeanTools.getData();
                        centersupplynumtv.setText(currentCompanyBaseNumBean.getSupply());
                        centerdemandnumtv.setText(currentCompanyBaseNumBean.getDemand());
                        centercollectnumtv.setText(currentCompanyBaseNumBean.getWishlist());
                        centermsgnumtv.setText(currentCompanyBaseNumBean.getNews());
                    }
                }

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
     * 是否已经登录。
     *
     * @return
     */
    private boolean isLogined() {

        Company company = Company.getInstance();
        if (company.getCompanyId() >= 0) {

            return true;
        } else {

            return false;
        }
    }

    /**
     * 进入登录.
     */
    private void gotoLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

}
