package com.dudu.aios.ui.utils.blur;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.CheckResult;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import rx.Observable;

/**
 * Created by Joker on 2015/11/27.
 */
public class RxBlurEffective {

  @CheckResult
  @NonNull
  public static Observable<Bitmap> bestBlur(@NonNull Context context, @DrawableRes Bitmap bitmap, int radius,
                                            float desaturateAmount) {
    return Observable.create(new BestBlurOnSubscribe(context, bitmap, radius, desaturateAmount));
  }
}
