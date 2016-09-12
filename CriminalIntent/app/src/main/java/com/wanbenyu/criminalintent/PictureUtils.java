package com.wanbenyu.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

/**
 * Created by 本钰 on 2016/9/6.
 */
public class PictureUtils {
    /**
     * Get a BitmapDrawable from a local file that is scaled down
     * to fit the current window size.
     */

    private static final String TAG = "PictureUtils";

    @SuppressWarnings("deprecation")
    public static BitmapDrawable getScaledDrawable(Activity a, String path, int orientation) {
        Display display = a.getWindowManager().getDefaultDisplay();
        float destWidth = display.getWidth();
        float destHeight = display.getHeight();

        //Read in the dimensions of the image on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        int inSampleSize = 1;
        if(srcHeight > destHeight || srcWidth > destWidth) {
            if(srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / destHeight);
            } else {
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        //Log.d(TAG,String.valueOf(orientation));
        /**Matrix matrix = new Matrix();
        matrix.preScale(1,1);
        matrix.postRotate(90);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);*/
        Bitmap resizedBitmap = null;
        Matrix matrix = new Matrix();
        if(orientation == 0) {
            matrix.preScale(1,1);
            matrix.postRotate(0);
            resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } else if (orientation == 8) {
            matrix.preScale(1,1);
            matrix.postRotate(180);
            resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } else if (orientation == 1) {
            matrix.preScale(1,1);
            matrix.postRotate(90);
            resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } else {
            matrix.preScale(1,1);
            matrix.postRotate(270);
            resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        return new BitmapDrawable(a.getResources(), resizedBitmap);

    }

    public static void cleanImageView(ImageView imageView) {
        if(!(imageView.getDrawable() instanceof BitmapDrawable))
            return;

        //Clean up the view's image for the sake of memory
        BitmapDrawable b = (BitmapDrawable)imageView.getDrawable();
        b.getBitmap().recycle();
        imageView.setImageDrawable(null);
    }

}
