package com.promote.ebingo.InformationActivity;

import android.support.v4.app.Fragment;

/**
 * Created by ACER on 2014/9/18.
 */
public class InterpriseBaseFragment extends Fragment {
    /**
     * 企业id. *
     */
    protected int interprsetId = -1;

    public int getInterprsetId() {
        return interprsetId;
    }

    protected void setInterprsetId(int interprsetId) {
        this.interprsetId = interprsetId;
    }


}