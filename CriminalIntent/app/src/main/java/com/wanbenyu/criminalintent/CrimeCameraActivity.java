package com.wanbenyu.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by 本钰 on 2016/8/29.
 */
public class CrimeCameraActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeCameraFragment();
    }
}
