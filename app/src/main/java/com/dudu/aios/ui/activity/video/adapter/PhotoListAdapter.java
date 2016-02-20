package com.dudu.aios.ui.activity.video.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dudu.aios.ui.activity.video.PhotoShowActivity;
import com.dudu.android.launcher.R;

import java.util.ArrayList;

/**
 * Created by dengjun on 2016/2/20.
 * Description :
 */
public class PhotoListAdapter extends BaseAdapter {
    private Context context;

    private ArrayList<Integer> phontoList;

    private boolean isChoose[];
    private boolean isDelete = true;

    private AnimationDrawable animationDrawable;

    public PhotoListAdapter(Context context, ArrayList<Integer> phontoList) {
        this.context = context;
        this.phontoList = phontoList;
        initChooseData();
    }

    @Override
    public int getCount() {
        return phontoList.size();
    }

    @Override
    public Object getItem(int position) {
        return phontoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.photo_grid_item, parent, false);
            holder.photoChooseBg = (ImageView) convertView.findViewById(R.id.photo_choose_bg);
            holder.photo = (ImageView) convertView.findViewById(R.id.photo);
            holder.uploading = (LinearLayout) convertView.findViewById(R.id.uploading_container);
            holder.btnCancel = (ImageButton) convertView.findViewById(R.id.button_cancel_upload);
            holder.uploadSuccessful = (LinearLayout) convertView.findViewById(R.id.upload_successful_container);
            holder.uploadingIcon = (ImageView) convertView.findViewById(R.id.image_uploading);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (isDelete) {
            //删除
            if (isChoose[position] == true) {
                holder.photoChooseBg.setVisibility(View.VISIBLE);
            } else {
                holder.photoChooseBg.setVisibility(View.GONE);
            }
        } else {
            //上传
            if (isChoose[position] == true) {
                holder.photoChooseBg.setVisibility(View.GONE);
                holder.uploading.setVisibility(View.VISIBLE);
                holder.uploadingIcon.setImageResource(R.drawable.uplaod_video_arrows);
                animationDrawable = (AnimationDrawable) holder.uploadingIcon.getDrawable();
                animationDrawable.start();
            }
        }

        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.uploading.setVisibility(View.GONE);
                if (animationDrawable != null) {
                    animationDrawable.stop();
                }
            }
        });

        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, PhotoShowActivity.class));
            }
        });

        return convertView;
    }

    public void setData(ArrayList<Integer> phontoList) {
        this.phontoList = (ArrayList<Integer>) phontoList.clone();
        isDelete = true;
        initChooseData();
        notifyDataSetChanged();
    }

    private void initChooseData() {
        isChoose = new boolean[phontoList.size()];
        for (int i = 0; i < phontoList.size(); i++) {
            isChoose[i] = false;
        }
    }

    public void chooseState(int post) {
        isChoose[post] = isChoose[post] == true ? false : true;
        this.notifyDataSetChanged();
    }

    public void cancelChoose() {
        initChooseData();
        notifyDataSetChanged();
    }

    public void uploadPhoto() {
        isDelete = false;
        notifyDataSetChanged();
    }

    class ViewHolder {
        ImageView photoChooseBg;
        ImageView photo;
        LinearLayout uploading;
        LinearLayout uploadSuccessful;
        ImageButton btnCancel;
        ImageView uploadingIcon;
    }
}
