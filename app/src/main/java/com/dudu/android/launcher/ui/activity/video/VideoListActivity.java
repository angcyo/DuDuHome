package com.dudu.android.launcher.ui.activity.video;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.bean.VideoEntity;
import com.dudu.android.launcher.db.DbHelper;
import com.dudu.android.launcher.ui.activity.base.BaseNoTitlebarAcitivity;
import com.dudu.android.launcher.utils.cache.ThumbsFetcher;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoListActivity extends BaseNoTitlebarAcitivity {

	private GridView mGridView;

	private VideoAdapter mAdapter;

	private List<VideoEntity> mVideoData;

	private DbHelper mDbHelper;

	private ThumbsFetcher mThumbsFetcher;
	
	@Override
	public int initContentView() {
		return R.layout.video_layout;
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		mGridView = (GridView) findViewById(R.id.video_grid);
		mGridView.setOverScrollMode(View.OVER_SCROLL_NEVER);
	}

	@Override
	public void initListener() {

	}

	@Override
	public void initDatas() {
		mThumbsFetcher = new ThumbsFetcher(VideoListActivity.this);

		mDbHelper = DbHelper.getDbHelper(VideoListActivity.this);

		mVideoData = new ArrayList<VideoEntity>();

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
			new AlertDialog.Builder(context)
					.setTitle(R.string.alert_notification)
					.setMessage(R.string.alert_delete_video)
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									data.remove(video);
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
							})
					.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							}).show();
		}

		private void showLockDialog() {
			new AlertDialog.Builder(context)
					.setTitle(R.string.alert_notification)
					.setMessage(R.string.alert_lock_video)
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							}).show();
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.video_item, parent, false);
				holder.delete = (ImageButton) convertView
						.findViewById(R.id.delete_button);
				holder.weixin = (ImageButton) convertView
						.findViewById(R.id.weixin_button);
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
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

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

			holder.weixin.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {

				}
			});
			mThumbsFetcher.loadImage(video.getFile().getAbsolutePath(),
					holder.thumbnail);
			return convertView;
		}

		class ViewHolder {
			ImageView thumbnail;
			ImageButton delete;
			ImageButton weixin;
			ImageButton play;
			TextView date;
			CheckBox checkBox;
		}

	}

}
