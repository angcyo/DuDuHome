package com.dudu.commonlib.utils.image;

import android.graphics.drawable.Drawable;

import com.dudu.commonlib.CommonLib;

/**
 * Created by dengjun on 2016/2/21.
 * Description :
 */
public class ImageUtils {
    public static Drawable getDrawble(int id){
        return CommonLib.getInstance().getContext().getResources().getDrawable(id);
    }
}
