package com.dudu.android.launcher.ui.view;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;

public class VideoView extends SurfaceView implements MediaPlayerControl {

	private static final String TAG = "videoui";

	private Context mContext;

	private Uri mUri;

	private int mDuration;

	private int mVideoWidth;

	private int mVideoHeight;

	private int mSurfaceWidth;

	private int mSurfaceHeight;

	private MediaPlayer mMediaPlayer = null;

	private boolean mIsPrepared;

	private SurfaceHolder mSurfaceHolder = null;

	private MediaPlayer.OnPreparedListener mOnPreparedListener;

	private MediaController mMediaController;

	private OnCompletionListener mOnCompletionListener;

	private int mCurrentBufferPercentage;

	private OnErrorListener mOnErrorListener;

	private boolean mStartWhenPrepared;

	private int mSeekWhenPrepared;

	public interface MySizeChangeListener {
		public void doMyThings();
	}

	private MySizeChangeListener mMyChangeLinstener;

	public VideoView(Context context) {
		super(context);
		mContext = context;
		initVideoView();
	}

	public VideoView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		mContext = context;
		initVideoView();
	}

	public VideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initVideoView();
	}

	public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
		mOnPreparedListener = l;
	}

	public void setOnCompletionListener(OnCompletionListener l) {
		mOnCompletionListener = l;
	}

	public void setMySizeChangeLinstener(MySizeChangeListener l) {
		mMyChangeLinstener = l;
	}

	public int getVideoWidth() {
		return mVideoWidth;
	}

	public int getVideoHeight() {
		return mVideoHeight;
	}

	public void setVideoScale(int width, int height) {
		LayoutParams lp = getLayoutParams();
		lp.height = height;
		lp.width = width;
		setLayoutParams(lp);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
		int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
		setMeasuredDimension(width, height);
	}

	public int resolveAdjustedSize(int desiredSize, int measureSpec) {
		int result = desiredSize;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		switch (specMode) {
		case MeasureSpec.UNSPECIFIED:
			result = desiredSize;
			break;
		case MeasureSpec.AT_MOST:
			result = Math.min(desiredSize, specSize);
			break;
		case MeasureSpec.EXACTLY:
			result = specSize;
			break;
		}

		return result;
	}

	public void setVideoPath(String path) {
		setVideoURI(Uri.parse(path));
	}

	public void setVideoURI(Uri uri) {
		mUri = uri;
		mStartWhenPrepared = false;
		mSeekWhenPrepared = 0;
		openVideo();
		requestLayout();
		invalidate();
	}

	private void openVideo() {
		if (mUri == null || mSurfaceHolder == null) {
			return;
		}

		Intent i = new Intent("com.android.music.musicservicecommand");
		i.putExtra("command", "pause");
		mContext.sendBroadcast(i);

		if (mMediaPlayer != null) {
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		try {
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setOnPreparedListener(mPreparedListener);
			mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
			mIsPrepared = false;

			mDuration = -1;
			mMediaPlayer.setOnCompletionListener(mCompletionListener);
			mMediaPlayer.setOnErrorListener(mErrorListener);
			mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
			mCurrentBufferPercentage = 0;
			mMediaPlayer.setDataSource(mContext, mUri);
			mMediaPlayer.setDisplay(mSurfaceHolder);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setScreenOnWhilePlaying(true);
			mMediaPlayer.prepareAsync();
			attachMediaController();
		} catch (IOException ex) {
			Log.w(TAG, "Unable to open content: " + mUri, ex);
			return;
		} catch (IllegalArgumentException ex) {
			Log.w(TAG, "Unable to open content: " + mUri, ex);
			return;
		}
	}

	public void stopPlayback() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	private void initVideoView() {
		mVideoWidth = 0;
		mVideoHeight = 0;
		getHolder().addCallback(mSHCallback);
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();
	}

	private void attachMediaController() {
		if (mMediaPlayer != null && mMediaController != null) {
			mMediaController.setMediaPlayer(this);
			View anchorView = this.getParent() instanceof View ? (View) this.getParent() : this;
			mMediaController.setAnchorView(anchorView);
			mMediaController.setEnabled(mIsPrepared);
		}
	}

	SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {

		public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
			mSurfaceWidth = w;
			mSurfaceHeight = h;
			if (mMediaPlayer != null && mIsPrepared && mVideoWidth == w && mVideoHeight == h) {
				if (mSeekWhenPrepared != 0) {
					mMediaPlayer.seekTo(mSeekWhenPrepared);
					mSeekWhenPrepared = 0;
				}
				mMediaPlayer.start();
				if (mMediaController != null) {
					mMediaController.show();
				}
			}
		}

		public void surfaceCreated(SurfaceHolder holder) {
			mSurfaceHolder = holder;
			openVideo();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			mSurfaceHolder = null;
			if (mMediaController != null)
				mMediaController.hide();
			if (mMediaPlayer != null) {
				mMediaPlayer.reset();
				mMediaPlayer.release();
				mMediaPlayer = null;
			}
		}
	};

	MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
		public void onPrepared(MediaPlayer mp) {
			mIsPrepared = true;
			if (mOnPreparedListener != null) {
				mOnPreparedListener.onPrepared(mMediaPlayer);
			}

			if (mMediaController != null) {
				mMediaController.setEnabled(true);
			}

			mVideoWidth = mp.getVideoWidth();
			mVideoHeight = mp.getVideoHeight();
			if (mVideoWidth != 0 && mVideoHeight != 0) {
				getHolder().setFixedSize(mVideoWidth, mVideoHeight);
				if (mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
					if (mSeekWhenPrepared != 0) {
						mMediaPlayer.seekTo(mSeekWhenPrepared);
						mSeekWhenPrepared = 0;
					}
					if (mStartWhenPrepared) {
						mMediaPlayer.start();
						mStartWhenPrepared = false;
						if (mMediaController != null) {
							mMediaController.show();
						}
					} else if (!isPlaying() && (mSeekWhenPrepared != 0 || getCurrentPosition() > 0)) {
						if (mMediaController != null) {
							mMediaController.show(0);
						}
					}
				}
			} else {
				if (mSeekWhenPrepared != 0) {
					mMediaPlayer.seekTo(mSeekWhenPrepared);
					mSeekWhenPrepared = 0;
				}
				if (mStartWhenPrepared) {
					mMediaPlayer.start();
					mStartWhenPrepared = false;
				}
			}
		}
	};

	MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
		public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
			mVideoWidth = mp.getVideoWidth();
			mVideoHeight = mp.getVideoHeight();

			if (mMyChangeLinstener != null) {
				mMyChangeLinstener.doMyThings();
			}

			if (mVideoWidth != 0 && mVideoHeight != 0) {
				getHolder().setFixedSize(mVideoWidth, mVideoHeight);
			}
		}
	};

	private OnErrorListener mErrorListener = new OnErrorListener() {
		public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
			Log.d(TAG, "Error: " + framework_err + "," + impl_err);
			if (mMediaController != null) {
				mMediaController.hide();
			}

			if (mOnErrorListener != null) {
				if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err)) {
					return true;
				}
			}

			return true;
		}
	};

	private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
		public void onBufferingUpdate(MediaPlayer mp, int percent) {
			mCurrentBufferPercentage = percent;
		}
	};

	private OnCompletionListener mCompletionListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mp) {
			if (mMediaController != null) {
				mMediaController.hide();
			}
			if (mOnCompletionListener != null) {
				mOnCompletionListener.onCompletion(mMediaPlayer);
			}
		}
	};

	@Override
	public void start() {
		if (mMediaPlayer != null && mIsPrepared) {
			mMediaPlayer.start();
			mStartWhenPrepared = false;
		} else {
			mStartWhenPrepared = true;
		}
	}

	@Override
	public void pause() {
		if (mMediaPlayer != null && mIsPrepared) {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.pause();
			}
		}

		mStartWhenPrepared = false;
	}

	@Override
	public int getDuration() {
		if (mMediaPlayer != null && mIsPrepared) {
			if (mDuration > 0) {
				return mDuration;
			}

			mDuration = mMediaPlayer.getDuration();
			return mDuration;
		}

		mDuration = -1;
		return mDuration;
	}

	@Override
	public int getBufferPercentage() {
		if (mMediaPlayer != null) {
			return mCurrentBufferPercentage;
		}
		return 0;
	}

	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return true;
	}

	@Override
	public boolean canSeekForward() {
		return true;
	}

	@Override
	public int getAudioSessionId() {
		return 0;
	}

	@Override
	public void seekTo(int msec) {
		if (mMediaPlayer != null && mIsPrepared) {
			mMediaPlayer.seekTo(msec);
		} else {
			mSeekWhenPrepared = msec;
		}
	}

	@Override
	public boolean isPlaying() {
		if (mMediaPlayer != null && mIsPrepared) {
			return mMediaPlayer.isPlaying();
		}

		return false;
	}

	@Override
	public int getCurrentPosition() {
		if (mMediaPlayer != null && mIsPrepared) {
			return mMediaPlayer.getCurrentPosition();
		}

		return 0;
	}

}
