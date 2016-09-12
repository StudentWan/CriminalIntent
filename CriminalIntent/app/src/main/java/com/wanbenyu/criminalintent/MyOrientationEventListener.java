package com.wanbenyu.criminalintent;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.OrientationEventListener;

/**
 * Created by 本钰 on 2016/9/8.
 */
public class MyOrientationEventListener extends OrientationEventListener {

    private static final String TAG = "OrientationEvent";
    int mOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

    public MyOrientationEventListener(Context context, int rate) {
        super(context, rate);
    }

    @Override
    public void onOrientationChanged(int rotation) {
        //To do

        if (((rotation >= 0) && (rotation <= 45)) || (rotation > 315)) {
            mOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
        } else if ((rotation > 45) && (rotation <= 135)) {
            mOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        } else if ((rotation > 135) && (rotation <= 225)) {
            mOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        } else if ((rotation > 225) && (rotation <= 315)) {
            mOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
        }


    }

    public int getOrientation() {
        return mOrientation;
    }
}
