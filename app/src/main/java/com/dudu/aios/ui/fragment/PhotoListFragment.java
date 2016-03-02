package com.dudu.aios.ui.fragment;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dudu.aios.ui.activity.MainRecordActivity;
import com.dudu.aios.ui.fragment.base.BaseFragment;
import com.dudu.aios.ui.utils.contants.FragmentConstants;
import com.dudu.android.launcher.R;

import java.util.ArrayList;

public class PhotoListFragment extends BaseFragment implements View.OnClickListener {

    private ImageButton mBackButton, mDeleteButton, mUploadButton;

    private GridView mPhotoGridView;

    private LinearLayout photoEmptyContainer;

    private TextView tvSelect;

    private RelativeLayout deleteUploadContainer;

    private boolean isSelectClick = false;

    private PhotoAdapter mAdapter;

    private ArrayList<Integer> mPhotoData;

    private ArrayList<Integer> mChooseData = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_photo_list, null);
        initFragment(view);
        initClickListener();
        initPhotoData();
        return view;
    }

    private void initPhotoData() {
        mPhotoData = new ArrayList<>();
        mAdapter = new PhotoAdapter(getActivity(), mPhotoData);
        mPhotoGridView.setAdapter(mAdapter);
        new LoadPhotoTask().execute();
    }

    private void loadVideos() {

        for (int i = 0; i < 12; i++) {
            mPhotoData.add(i);
        }

    }

    private void initClickListener() {
        mBackButton.setOnClickListener(this);
        tvSelect.setOnClickListener(this);
        mDeleteButton.setOnClickListener(this);
        mUploadButton.setOnClickListener(this);
        mPhotoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isSelectClick) {
                    mAdapter.chooseState(position);
                    mChooseData.add(mPhotoData.get(position));
                } else {
                    //调到单个图片图片的页面
                    replaceFragment(FragmentConstants.FRAGMENT_PHOTO);
                }
            }
        });
    }

    public void replaceFragment(String name) {
        MainRecordActivity activity = (MainRecordActivity) getActivity();
        activity.replaceFragment(name);
    }

    private void initFragment(View view) {
        mBackButton = (ImageButton) view.findViewById(R.id.button_back);
        tvSelect = (TextView) view.findViewById(R.id.tv_select);
        mDeleteButton = (ImageButton) view.findViewById(R.id.button_photo_delete);
        mUploadButton = (ImageButton) view.findViewById(R.id.button_photo_upload);
        mPhotoGridView = (GridView) view.findViewById(R.id.photo_gridView);
        deleteUploadContainer = (RelativeLayout) view.findViewById(R.id.delete_upload_container);
        photoEmptyContainer = (LinearLayout) view.findViewById(R.id.photo_empty_container);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_back:
                replaceFragment(FragmentConstants.FRAGMENT_DRIVING_RECORD);
                break;
            case R.id.tv_select:
                actionSelect();
                break;
            case R.id.button_photo_delete:
                actionDelete();
                break;
            case R.id.button_photo_upload:
                actionUpload();
                break;
        }
    }

    private void actionUpload() {
        mAdapter.uploadPhoto();
    }

    private void actionDelete() {
        if (mChooseData.size() == 0) {
           //你还没有选择图片
            return;
        }
        for (int i = 0; i < mPhotoData.size(); i++) {
            for (int j = 0; j < mChooseData.size(); j++) {
                if (mPhotoData.get(i) == mChooseData.get(j)) {
                    mPhotoData.remove(i);
                }
            }
        }

        mAdapter.setData(mPhotoData);
        mChooseData.clear();
        if (mPhotoData.size() == 0) {
            mPhotoGridView.setVisibility(View.GONE);
            photoEmptyContainer.setVisibility(View.VISIBLE);
        }
    }

    private void actionSelect() {
        if (isSelectClick) {
            mAdapter.cancelChoose();
            tvSelect.setText(getResources().getString(R.string.action_choice));
            deleteUploadContainer.setVisibility(View.GONE);
            mChooseData.clear();
            isSelectClick = false;
        } else {
            tvSelect.setText(getResources().getString(R.string.cancel));
            deleteUploadContainer.setVisibility(View.VISIBLE);
            isSelectClick = true;
        }
    }

    class PhotoAdapter extends BaseAdapter {

        private Context context;

        private ArrayList<Integer> data;

        private LayoutInflater inflater;

        private boolean isChoose[];

        private boolean isDelete = true;

        private AnimationDrawable animationDrawable;

        public PhotoAdapter(Context context, ArrayList<Integer> data) {
            this.context = context;
            this.data = data;
            initChooseData();
            inflater = LayoutInflater.from(context);
        }

        public void setData(ArrayList<Integer> data) {
            this.data = (ArrayList<Integer>) data.clone();
            isDelete = true;
            initChooseData();
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.photo_grid_item, parent, false);
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

            return convertView;
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

        private void initChooseData() {
            isChoose = new boolean[data.size()];
            for (int i = 0; i < data.size(); i++) {
                isChoose[i] = false;
            }
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

    private class LoadPhotoTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            loadVideos();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mPhotoData != null) {
                tvSelect.setVisibility(View.VISIBLE);
                photoEmptyContainer.setVisibility(View.GONE);
                mPhotoGridView.setVisibility(View.VISIBLE);

            } else {
                tvSelect.setVisibility(View.GONE);
                photoEmptyContainer.setVisibility(View.VISIBLE);
                mPhotoGridView.setVisibility(View.GONE);
            }
            mAdapter.setData(mPhotoData);
        }
    }
}
