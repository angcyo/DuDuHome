package com.dudu.aios.ui.fragment.video.adapter;

import android.app.Fragment;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dudu.aios.ui.activity.MainRecordActivity;
import com.dudu.aios.ui.utils.contants.FragmentConstants;
import com.dudu.android.launcher.R;
import com.dudu.drivevideo.model.VideoEntity;

import java.util.ArrayList;

/**
 * Created by dengjun on 2016/2/18.
 * Description :
 */
public class VideoListViewAdapter extends RecyclerView.Adapter<VideoListViewAdapter.VideoInfoItemViewHolder> {
    private Context mContext;
    private Fragment fragment;
    private ArrayList<VideoEntity> videoEntityArrayList;


    public VideoListViewAdapter(Fragment fragment, ArrayList<VideoEntity> videoEntityArrayList) {
        this.mContext = fragment.getActivity();
        this.fragment = fragment;
        this.videoEntityArrayList = videoEntityArrayList;

        if (videoEntityArrayList == null){
            this.videoEntityArrayList = new ArrayList<VideoEntity>(4);
            this.videoEntityArrayList.add(new VideoEntity());
            this.videoEntityArrayList.add(new VideoEntity());
            this.videoEntityArrayList.add(new VideoEntity());
            this.videoEntityArrayList.add(new VideoEntity());
        }
    }



    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return videoEntityArrayList == null ? 0 : videoEntityArrayList.size();
    }

    @Override
    public VideoInfoItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View videoItemInfoView = LayoutInflater.from(mContext).inflate(R.layout.video_grid_item, parent, false);
        VideoInfoItemViewHolder videoInfoItemViewHolder = new VideoInfoItemViewHolder(videoItemInfoView);

        videoInfoItemViewHolder.btnPlay = (ImageButton) videoItemInfoView.findViewById(R.id.button_play_video);
        videoInfoItemViewHolder.btnDelete = (ImageButton) videoItemInfoView.findViewById(R.id.button_delete_video);
        videoInfoItemViewHolder.btnUpload = (ImageButton) videoItemInfoView.findViewById(R.id.button_upload_video);
        videoInfoItemViewHolder.btnCancel = (ImageButton) videoItemInfoView.findViewById(R.id.button_cancel_upload);
        videoInfoItemViewHolder.uploading = (LinearLayout) videoItemInfoView.findViewById(R.id.uploading_video_container);
        videoInfoItemViewHolder.upLoadSuccessful = (LinearLayout) videoItemInfoView.findViewById(R.id.upload_successful_video_container);
        videoInfoItemViewHolder.tvDate = (TextView) videoItemInfoView.findViewById(R.id.tv_video_date);
        videoInfoItemViewHolder.checkBox = (CheckBox) videoItemInfoView.findViewById(R.id.video_check_box);
        videoInfoItemViewHolder.imageUploading = (ImageView) videoItemInfoView.findViewById(R.id.image_uploading);


        videoInfoItemViewHolder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(FragmentConstants.FRAGMENT_VIDEO);
                //跳到播放的界面
            }
        });

        return videoInfoItemViewHolder;
    }




    public void onBindViewHolder(VideoInfoItemViewHolder holder, int position) {

    }

    public static  class VideoInfoItemViewHolder extends RecyclerView.ViewHolder{

        public VideoInfoItemViewHolder(View itemView) {
            super(itemView);
        }

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

    private void replaceFragment(String name) {
        MainRecordActivity activity = (MainRecordActivity) fragment.getActivity();
        activity.replaceFragment(name);
    }

}
