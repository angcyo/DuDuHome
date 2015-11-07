package com.dudu.android.launcher.ui.activity.video;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.bean.VideoEntity;
import com.dudu.android.launcher.db.DbHelper;
import com.dudu.android.launcher.ui.activity.base.BaseNoTitlebarAcitivity;
import com.dudu.android.launcher.ui.dialog.ConfirmCancelDialog;
import com.dudu.android.launcher.ui.dialog.ConfirmDialog;
import com.dudu.android.launcher.utils.cache.ImageCache;
import com.dudu.android.launcher.utils.cache.ThumbsFetcher;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoListActivity extends FragmentActivity {

	private static final String IMAGE_CACHE_DIR = "thumbs";

	private GridView mGridView;

	private VideoAdapter mAdapter;

	private List<VideoEntity> mVideoData;

	private DbHelper mDbHelper;

	private ThumbsFetcher mThumbsFetcher;

	private View mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.video_layout);

        mGridView = (GridView) findViewById(R.id.video_grid);
        mGridView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mEmptyView = findViewById(R.id.empty_view);

        initDatas();
    }

	public void initDatas() {
		ImageCache.ImageCacheParams cacheParams =
				new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.25f);

        cacheParams.diskCacheEnabled = false;

		mThumbsFetcher = new ThumbsFetcher(VideoListActivity.this);

        mThumbsFetcher.addImageCache(getSupportFragmentManager(), cacheParams);

		mDbHelper = DbHelper.getDbHelper(VideoListActivity.this);

		mVideoData = new ArrayList<>();

		mAdapter = new VideoAdapter(this, mVideoData);

		mGridView.setAdapter(mAdapter);

		new LoadVideoTask().execute();
	}

	private void loadVideos() {
		List<VideoEntity> videos = mDbHelper.getVideos();
		if (videos != null && !videos.isEmpty()) {
			mVideoData.addAll(videos);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mThumbsFetcher.closeCache();
	}

	public void onBackPressed(View v) {
		finish();
	}

	private class LoadVideoTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			loadVideos();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (!mVideoData.isEmpty()) {
				mGridView.setVisibility(View.VISIBLE);
				mEmptyView.setVisibility(View.GONE);
			} else {
				mEmptyView.setVisibility(View.VISIBLE);
				mGridView.setVisibility(View.GONE);
			}

			mAdapter.setData(mVideoData);
		}

	}

	private class VideoAdapter extends BaseAdapter {

		private Context context;

		private List<VideoEntity> data;

		public VideoAdapter(Context context, List<VideoEntity> data) {
			this.context = context;
			this.data = data;
		}

		public void setData(List<VideoEntity> data) {
			this.data = data;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		private void showDeleteDialog(final VideoEntity video) {
            ConfirmCancelDialog dialog = new ConfirmCancelDialog(context);
            dialog.setOnButtonClicked(new ConfirmCancelDialog.OnDialogButttonClickListener() {
                @Override
                public void onConfirmClick() {
                    data.remove(video);
					if (data.isEmpty()) {
						mEmptyView.setVisibility(View.VISIBLE);
						mGridView.setVisibility(View.GONE);
					}

                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            File file = video.getFile();
                            if (file != null && file.exists()) {
                                file.delete();
                            }

                            mDbHelper.deleteVideo(video
                                    .getName());
                        }
                    }).start();

                    notifyDataSetChanged();
                }

                @Override
                public void onCancelClick() {

                }
            });

			dialog.show();
		}

		private void showLockDialog() {
            ConfirmDialog dialog = new ConfirmDialog(context);
            dialog.setOnConfirmClickListener(new ConfirmDialog.OnConfirmClickListener() {
                @Override
                public void onConfirmClick() {

                }
            });

            dialog.show();
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.video_taxi_item, parent, false);
				holder.delete = (ImageButton) convertView
						.findViewById(R.id.delete_button);
				holder.date = (TextView) convertView
						.findViewById(R.id.date_text);
				holder.thumbnail = (ImageView) convertView
						.findViewById(R.id.thumbnail);
				holder.play = (ImageButton) convertView
						.findViewById(R.id.video_play);
				holder.checkBox = (CheckBox) convertView
						.findViewById(R.id.video_check_box);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final VideoEntity video = data.get(position);
			holder.date.setText(video.getName());
			holder.delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (video.getStatus() == 0) {
						showDeleteDialog(video);
					} else {
						showLockDialog();
					}
				}
			});

			holder.play.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(VideoListActivity.this,
							VideoPlayActivity.class);
					intent.setData(Uri.fromFile(video.getFile()));
					startActivity(intent);
				}
			});

			holder.checkBox.setOnCheckedChangeListener(null);
			holder.checkBox.setChecked(video.getStatus() == 1);
			holder.checkBox
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							if (isChecked) {
								video.setStatus(1);
								mDbHelper.updateVideoStatus(video.getName(), 1);
							} else {
								video.setStatus(0);
								mDbHelper.updateVideoStatus(video.getName(), 0);
							}
						}
					});

			mThumbsFetcher.loadImage(video.getFile().getAbsolutePath(),
					holder.thumbnail);
			return convertView;
		}

		class ViewHolder {
			ImageView thumbnail;
			ImageButton delete;
			ImageButton play;
			TextView date;
			CheckBox checkBox;
		}

	}

}
