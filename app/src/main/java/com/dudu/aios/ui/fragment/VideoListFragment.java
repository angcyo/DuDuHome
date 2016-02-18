package com.dudu.aios.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dudu.aios.ui.activity.MainRecordActivity;
import com.dudu.aios.ui.fragment.video.adapter.VideoListViewAdapter;
import com.dudu.aios.ui.fragment.video.view.CusomSwipeView;
import com.dudu.aios.ui.utils.contants.FragmentConstants;
import com.dudu.android.launcher.R;

import com.dudu.android.launcher.db.DbHelper;
import com.dudu.android.launcher.model.VideoEntity;

import java.util.ArrayList;


public class VideoListFragment extends Fragment implements View.OnClickListener {


    private CusomSwipeView videoListView;
    private VideoListViewAdapter videoListViewAdapter;

    private LinearLayout emptyView;

    private ImageButton mPreVideoButton, mPostVideoButton, mBackButton;

    private TextView mPreVideoTextChinese, mPostVideoTextChinese, mPreVideoTextEnglish, mPostVideoTextEnglish;


    private ArrayList<VideoEntity> mVideoData;

    private DbHelper mDbHelper;

    private int mPerPageItemNum = 6;

    private int mCurrentPage = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_video_list, null);
        initFragmentView(view);
        initClickListener();
        initVideoData();
        return view;
    }

    private void initVideoData() {
        mVideoData = new ArrayList<>();

        videoListViewAdapter = new VideoListViewAdapter(this, null);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        videoListView.setLayoutManager(linearLayoutManager);
        videoListView.setAdapter(videoListViewAdapter);

        new LoadVideoTask().execute();
    }

    private void initClickListener() {
//        mPreVideoContainer.setOnClickListener(this);
//        mPostVideoContainer.setOnClickListener(this);
        mBackButton.setOnClickListener(this);
    }

    private void initFragmentView(View view) {
//        mVideoGridView = (GridView) view.findViewById(R.id.videoGridView);
//        mPreVideoContainer = (LinearLayout) view.findViewById(R.id.pre_video_container);
        mPreVideoButton = (ImageButton) view.findViewById(R.id.button_pre_video);
        mPreVideoTextChinese = (TextView) view.findViewById(R.id.pre_video_text_chinese);
        mPreVideoTextEnglish = (TextView) view.findViewById(R.id.pre_video_text_english);
//        mPostVideoContainer = (LinearLayout) view.findViewById(R.id.post_video_container);
        mPostVideoButton = (ImageButton) view.findViewById(R.id.button_post_video);
        mPostVideoTextChinese = (TextView) view.findViewById(R.id.post_video_text_chinese);
        mPostVideoTextEnglish = (TextView) view.findViewById(R.id.post_video_text_english);
        emptyView = (LinearLayout) view.findViewById(R.id.video_empty_container);
        mBackButton = (ImageButton) view.findViewById(R.id.button_back);

        videoListView = (CusomSwipeView)view.findViewById(R.id.video_list_view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

           /* case R.id.pre_video_container:
                mPreVideoButton.setBackgroundResource(R.drawable.prepositive_video_checked);
                mPreVideoTextChinese.setTextColor(getResources().getColor(R.color.white));
                mPreVideoTextEnglish.setTextColor(getResources().getColor(R.color.white));
                mPostVideoButton.setBackgroundResource(R.drawable.postposition_video_unchecked);
                mPostVideoTextChinese.setTextColor(getResources().getColor(R.color.unchecked_textColor));
                mPostVideoTextEnglish.setTextColor(getResources().getColor(R.color.unchecked_textColor));
                break;

            case R.id.post_video_container:
                mPostVideoButton.setBackgroundResource(R.drawable.postposition_video_checked);
                mPostVideoTextChinese.setTextColor(getResources().getColor(R.color.white));
                mPostVideoTextEnglish.setTextColor(getResources().getColor(R.color.white));
                mPreVideoButton.setBackgroundResource(R.drawable.prepositive_video_unchecked);
                mPreVideoTextChinese.setTextColor(getResources().getColor(R.color.unchecked_textColor));
                mPreVideoTextEnglish.setTextColor(getResources().getColor(R.color.unchecked_textColor));
                break;*/

            case R.id.button_back:
                replaceFragment(FragmentConstants.FRAGMENT_DRIVING_RECORD);
                break;
        }
    }

    private void replaceFragment(String name) {
        MainRecordActivity activity = (MainRecordActivity) getActivity();
        activity.replaceFragment(name);
    }


    private class VideoAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        private Context context;

        private ArrayList<VideoEntity> data;


        private AnimationDrawable animationDrawable;

        public VideoAdapter(Context context, ArrayList<VideoEntity> data) {
            this.context = context;
            this.data = data;
            inflater = LayoutInflater.from(context);

        }

        public void setData(ArrayList<VideoEntity> data) {
            this.data = (ArrayList<VideoEntity>) data.clone();
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.video_grid_item, parent, false);
                holder = new ViewHolder();
                holder.btnPlay = (ImageButton) convertView.findViewById(R.id.button_play_video);
                holder.btnDelete = (ImageButton) convertView.findViewById(R.id.button_delete_video);
                holder.btnUpload = (ImageButton) convertView.findViewById(R.id.button_upload_video);
                holder.btnCancel = (ImageButton) convertView.findViewById(R.id.button_cancel_upload);
                holder.uploading = (LinearLayout) convertView.findViewById(R.id.uploading_video_container);
                holder.upLoadSuccessful = (LinearLayout) convertView.findViewById(R.id.upload_successful_video_container);
                holder.tvDate = (TextView) convertView.findViewById(R.id.tv_video_date);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.video_check_box);
                holder.imageUploading = (ImageView) convertView.findViewById(R.id.image_uploading);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final VideoEntity entity = data.get(position);
            holder.tvDate.setText(entity.getCreateTime());
            holder.btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    replaceFragment(FragmentConstants.FRAGMENT_VIDEO);
                    //跳到播放的界面
                }
            });
            holder.btnUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.uploading.setVisibility(View.VISIBLE);
                    holder.btnUpload.setVisibility(View.GONE);
                    holder.imageUploading.setImageResource(R.drawable.uplaod_video_arrows);
                    animationDrawable = (AnimationDrawable) holder.imageUploading.getDrawable();
                    animationDrawable.start();
                }
            });
            holder.btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.uploading.setVisibility(View.GONE);
                    holder.btnUpload.setVisibility(View.VISIBLE);
                    if (animationDrawable != null) {
                        animationDrawable.stop();
                    }
                }
            });

            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            return convertView;
        }

        class ViewHolder {
            ImageButton btnPlay;
            ImageButton btnDelete;
            ImageButton btnUpload;
            ImageButton btnCancel;
            LinearLayout uploading;
            LinearLayout upLoadSuccessful;
            TextView tvDate;
            CheckBox checkBox;
            ImageView imageUploading;
        }
    }

    private class LoadVideoTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            loadVideos();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mVideoData != null) {
                emptyView.setVisibility(View.GONE);

            } else {
                emptyView.setVisibility(View.VISIBLE);

            }
        }
    }

    private void loadVideos() {
       /* List<VideoEntity> videos = mDbHelper.getVideos(mCurrentPage * mPerPageItemNum,
                mPerPageItemNum);
        if (videos != null && !videos.isEmpty()) {
            mVideoData.addAll(videos);
        }*/
        mVideoData = getVideoData();

    }

    public ArrayList<VideoEntity> getVideoData() {
        ArrayList<VideoEntity> list = new ArrayList();
        for (int i = 0; i < 4; i++) {
            VideoEntity entity = new VideoEntity();
            entity.setCreateTime("20170106/13:00-14:0" + i);
            list.add(entity);
        }
        return list;
    }
}
