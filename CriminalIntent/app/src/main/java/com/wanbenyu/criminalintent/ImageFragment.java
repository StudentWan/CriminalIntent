package com.wanbenyu.criminalintent;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by 本钰 on 2016/9/6.
 */
public class ImageFragment extends DialogFragment {
    public static final String EXTRA_IMAGE_PATH =
            "com.wanbenyu.criminalintent.image_path";
    public static final String EXTRA_IMAGE_ORIENTATION =
            "com.wanbenyu.crinimalintent.image_orientation";

    private ImageView mImageView;

    public static ImageFragment newInstance (String imagePath, int orientation) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_IMAGE_PATH, imagePath);
        args.putSerializable(EXTRA_IMAGE_ORIENTATION, orientation);

        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup parent, Bundle savedInstanceState) {
        mImageView = new ImageView(getActivity());
        String path = (String)getArguments().getSerializable(EXTRA_IMAGE_PATH);
        int orientation = (int)getArguments().getSerializable(EXTRA_IMAGE_ORIENTATION);
        BitmapDrawable image = PictureUtils.getScaledDrawable(getActivity(), path, orientation);
        mImageView.setImageDrawable(image);

        return mImageView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PictureUtils.cleanImageView(mImageView);
    }
}
