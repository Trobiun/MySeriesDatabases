package com.sakagames.myseriesdatabases;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by robin on 24/08/17.
 */

public class BitmapUtility {

    //convert from bitmap to byte array
    public static byte[] getBitmapAsByteArray(Bitmap bitmap, Context context) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Bitmap out = resize(bitmap,context);
        out.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static Bitmap resize(Bitmap bitmap, Context context) {
        if (bitmap == null) {
            return null;
        }
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        //Bitmap bitmapResult = new BitmapDrawable(context.getResources(),new ByteArrayInputStream(bitmaps));
        int maxHeight = 100;
        int maxWidth = 75;
        if (maxHeight > 0 && maxWidth > 0) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;
            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            bitmap = Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true);
            return bitmap;
        } else {
            return bitmap;
        }
    }
}
