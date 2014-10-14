package com.promote.ebingo;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.jch.d2code.CaptureActivity;
import com.jch.lib.util.HttpUtil;
import com.promote.ebingo.InformationActivity.CodeScanOnlineActivity;

/**
 * Created by jch on 2014/8/27.
 */
public class BaseFragment extends Fragment {
    /**
     * 用于跳转到开始扫描用来回传值的resultCode
     */
    public static final int TO_SCAN = 99;

    public boolean isNetworkConnected() {

        if (!HttpUtil.isNetworkConnected(getActivity().getApplicationContext())) {

            Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    protected void scan2Code() {

        Intent intent = new Intent(getActivity(),
                CaptureActivity.class);
        getActivity().startActivityForResult(intent, TO_SCAN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case TO_SCAN:
                if (resultCode == getActivity().RESULT_OK) {
                    String scanStr = data.getStringExtra("RESULT");
//                  //TODO 判断扫描结果的类型.
                    Intent intent = new Intent(getActivity(), CodeScanOnlineActivity.class);
                    intent.putExtra(CodeScanOnlineActivity.URLSTR, scanStr);
                    startActivity(intent);
                } else if (resultCode == getActivity().RESULT_CANCELED) {
                    Toast.makeText(getActivity(), "扫描取消",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "扫描异常",
                            Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }
}