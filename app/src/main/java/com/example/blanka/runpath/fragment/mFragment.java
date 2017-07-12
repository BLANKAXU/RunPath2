package com.example.blanka.runpath.fragment;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by BLANKA on 2017/4/24 0024.
 */

public class mFragment extends Fragment {
    protected Activity mActivity;
    /**
     *  保证Fragment即使在onDetach后，仍持有Activity的引用（有引起内存泄露的风险，但是相比空指针闪退，这种做法“安全”些）
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity)context;
    }
}
