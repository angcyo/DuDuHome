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

import com.dudu.drivevideo.model.VideoEntity;
import com.dudu.drivevideo.storage.VideoFileManage;

import java.util.ArrayList;
import java.util.List;


public class VideoListFragment extends Fragment implements View.OnClickListener {


    private CusomSwipeView videoListView;
    private VideoListViewAdapter videoListViewAdapter;

    private LinearLayout emptyView;

    private ImageButton mPreVideoButton, mPostVideoButton, mBackButton;

    private TextView mPreVideoTextChinese, mPostVideoTextChinese, mPreVideoTextEnglish, mPostVideoTextEnglish;


    private ArrayList<VideoEntity> mVideoData;


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
        loadVideos();

        videoListViewAdapter = new VideoListViewAdapter(this, mVideoData);
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



            case R.id.button_back:
                replaceFragment(FragmentConstants.FRAGMENT_DRIVING_RECORD);
                break;
        }
    }

    private void replaceFragment(String name) {
        MainRecordActivity activity = (MainRecordActivity) getActivity();
        activity.replaceFragment(name);
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

        List<VideoEntity> videos = VideoFileManage.getInstance().getDbHelper().getAllVideosList();
        if (videos != null && !videos.isEmpty()) {
            mVideoData.addAll(videos);
        }
    }

}
