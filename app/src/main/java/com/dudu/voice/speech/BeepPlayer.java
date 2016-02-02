/*******************************************************************************
 * Copyright AISpeech Ltd. 2014
 * For more information contact us at http://aiworks.cn
 ******************************************************************************/
package com.dudu.voice.speech;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BeepPlayer {
	
	MediaPlayer mediaPlayer;
	OnCompletionTask mTask;
	boolean isCompleted = false;
	
	/**
	 * @param context
	 * @param resID 音频id
	 */
	public BeepPlayer(Context context, int resID) {
		mediaPlayer = MediaPlayer.create(context, resID);
	}
	
	public void setOnCompletionTask(OnCompletionTask task){
		mTask = task;
	}
	
	/**
	 * 播放Beep音
	 */
	public void playBeep() {
		isCompleted = false;
		mediaPlayer.setOnCompletionListener(mTask);
		mediaPlayer.start();
	}
	
	/**
	 * 停止播放
	 */
	public void stopBeep(){
		isCompleted = true;
		mediaPlayer.stop();
	}
	
	public boolean isCompleted(){
		return isCompleted;
	}
	
	public void release(){
		mediaPlayer.release();
	}
	
	/**
	 * 取消回调
	 */
	public void cancelCallback(){
		mediaPlayer.setOnCompletionListener(null);
	}
	
	public class OnCompletionTask implements OnCompletionListener{
		
		Handler mHandler;
		int mMessageWhat;
		
		public OnCompletionTask(Handler handler, int msgWhat) {
			mHandler = handler;
			mMessageWhat = msgWhat;
		}

		@Override
		public void onCompletion(MediaPlayer mp) {
			isCompleted = true;
			Message.obtain(mHandler, mMessageWhat).sendToTarget();
		}
		
	}
	
}
