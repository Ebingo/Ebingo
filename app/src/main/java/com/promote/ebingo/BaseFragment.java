package com.promote.ebingo;

import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.jch.lib.util.HttpUtil;

/**
 * Created by jch on 2014/8/27.
 */
public class BaseFragment extends Fragment {

    public boolean isNetworkConnected() {

        if (!HttpUtil.isNetworkConnected(getActivity().getApplicationContext())) {

            Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}