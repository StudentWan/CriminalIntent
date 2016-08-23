package com.wanbenyu.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by 本钰 on 2016/8/8.
 */
public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment(){
        return new CrimeListFragment();
    }
}
