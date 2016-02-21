package com.dudu.commonlib.utils.afinal.core;

import android.graphics.drawable.Drawable;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class BitmapDisplayConfig {
	
	
	private int bitmapWidth;
	private int bitmapHeight;
	
	private Animation animation;
	
	private int animationType;
	
	private Drawable loadFailDrawable;
	private Drawable loadingDrawable;
	
	
	
	public BitmapDisplayConfig(){
		if(animation == null){
			animation = new AlphaAnimation(0.0f, 1.0f);
		}
	}
	
	public int getBitmapWidth() {
		return bitmapWidth;
	}

	public void setBitmapWidth(int bitmapWidth) {
		this.bitmapWidth = bitmapWidth;
	}

	public int getBitmapHeight() {
		return bitmapHeight;
	}

	public void setBitmapHeight(int bitmapHeight) {
		this.bitmapHeight = bitmapHeight;
	}

	public Animation getAnimation() {
		return animation;
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	public int getAnimationType() {
		return animationType;
	}

	public void setAnimationType(int animationType) {
		this.animationType = animationType;
	}

	public Drawable getLoadingDrawable(){
		return loadingDrawable;
	}
	
	public void setLoadingDrawable(Drawable loadingDrawable) {
		this.loadingDrawable = loadingDrawable;
	}
	
	public Drawable getLoadFailDrawable(){
		return loadFailDrawable;
	}
	
	public void setLoadfailDrawable(Drawable loadfailDrawable){
		this.loadFailDrawable = loadfailDrawable;
	}
	
	public class AnimationType{
		public static final int userDefined = 0;
		public static final int fadeIn = 1;
	}

}