package com.dudu.commonlib.utils.afinal.display;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.dudu.commonlib.utils.afinal.core.BitmapDisplayConfig;


public class SimpleDisplayer implements Displayer{
	
	/**
	 * 图片加载完成 回调的函数
	 * @param imageView
	 * @param bitmap
	 * @param config
	 */
	public void loadCompletedisplay(View imageView,Bitmap bitmap,BitmapDisplayConfig config){
		switch (config.getAnimationType()) {
		case BitmapDisplayConfig.AnimationType.fadeIn:
			fadeInDisplay(imageView,bitmap);
			break;
		case BitmapDisplayConfig.AnimationType.userDefined:
			animationDisplay(imageView,bitmap,config.getAnimation());
			break;
		default:
			break;
		}
	}
	
	@SuppressWarnings("deprecation")
	private void fadeInDisplay(View imageView,Bitmap bitmap){
		final TransitionDrawable td =
                new TransitionDrawable(new Drawable[] {
                        new ColorDrawable(android.R.color.transparent),
                        new BitmapDrawable(imageView.getResources(), bitmap)
                });
        if(imageView instanceof ImageView){
			((ImageView)imageView).setImageDrawable(td);
		}else{
			imageView.setBackgroundDrawable(td);
		}
        td.startTransition(300);
	}
	
	
	@SuppressWarnings("deprecation")
	private void animationDisplay(View imageView,Bitmap bitmap,Animation animation){
		animation.setStartTime(AnimationUtils.currentAnimationTimeMillis());		
        if(imageView instanceof ImageView){
			((ImageView)imageView).setImageBitmap(bitmap);
		}else{
			imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
		}
        imageView.startAnimation(animation);
	}

	/**
	 * 图片加载失败回调的函数
	 * @param imageView
	 * @param drawable
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void loadFailDisplay(View imageView, Drawable drawable) {
		// TODO Auto-generated method stub
		if(imageView instanceof ImageView){
			((ImageView)imageView).setImageDrawable(drawable);
		}else{
			imageView.setBackgroundDrawable(drawable);
		}	
	}

}
