package com.dudu.aios.ui.activity.video;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dudu.aios.ui.activity.video.adapter.PhotoListAdapter;
import com.dudu.aios.ui.base.BaseActivity;
import com.dudu.android.launcher.R;
import com.dudu.commonlib.utils.afinal.FinalBitmap;
import com.dudu.drivevideo.model.PhotoInfoEntity;
import com.dudu.drivevideo.storage.VideoFileManage;

import java.util.ArrayList;
import java.util.List;

public class PhotoListActivity extends BaseActivity {
    private ImageButton mBackButton, mDeleteButton, mUploadButton;

    private GridView mPhotoGridView;

    private LinearLayout photoEmptyContainer;

    private TextView tvSelect;

    private RelativeLayout deleteUploadContainer;

    private boolean isSelectClick = false;

    private PhotoListAdapter mPhotoListAdapter;

    private List<PhotoInfoEntity> photoInfoEntityList;

    private ArrayList<Integer> mChooseData = new ArrayList<>();

    private FinalBitmap finalBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        observableFactory.getCommonObservable(baseBinding).hasTitle.set(false);
        finalBitmap = new FinalBitmap(this);
        initFragment();
        initClickListener();
        initPhotoData();
    }

    @Override
    protected View getChildView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_photo_list,null);
    }

    private void initFragment() {
        mBackButton = (ImageButton) findViewById(R.id.button_back);
        tvSelect = (TextView) findViewById(R.id.tv_select);
        mDeleteButton = (ImageButton) findViewById(R.id.button_photo_delete);
        mUploadButton = (ImageButton) findViewById(R.id.button_photo_upload);
        mPhotoGridView = (GridView) findViewById(R.id.photo_gridView);
        deleteUploadContainer = (RelativeLayout) findViewById(R.id.delete_upload_container);
        photoEmptyContainer = (LinearLayout) findViewById(R.id.photo_empty_container);
    }

    private void initPhotoData() {
        photoInfoEntityList = new ArrayList<>();


        mPhotoListAdapter = new PhotoListAdapter(this, photoInfoEntityList, finalBitmap);
        mPhotoGridView.setAdapter(mPhotoListAdapter);
        new LoadPhotoTask().execute();
    }

    private void loadVideos() {
        List<PhotoInfoEntity> photoInfoEntityList = VideoFileManage.getInstance().generatePhotoInfoEntityList();
        if (photoInfoEntityList != null){
            this.photoInfoEntityList = photoInfoEntityList;
        }

    }

    private void initClickListener() {

        mPhotoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isSelectClick) {
                    mPhotoListAdapter.chooseState(position);
//                    mChooseData.add(photoInfoEntityList.get(position));
                } else {
                    //调到单个图片图片的页面

                }
            }
        });
    }



    public void onButtonBack(View view){
        if (photoInfoEntityList != null){
            photoInfoEntityList.clear();
        }
        finish();
    }


    private class LoadPhotoTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            loadVideos();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (photoInfoEntityList != null ) {
//                tvSelect.setVisibility(View.VISIBLE);
                if (photoInfoEntityList.size()>0){
                    photoEmptyContainer.setVisibility(View.GONE);
                }else {
                    photoEmptyContainer.setVisibility(View.VISIBLE);
                }

                mPhotoGridView.setVisibility(View.VISIBLE);

            } else {
//                tvSelect.setVisibility(View.GONE);
                photoEmptyContainer.setVisibility(View.VISIBLE);
                mPhotoGridView.setVisibility(View.GONE);
            }
            if (photoInfoEntityList != null){
                mPhotoListAdapter.setData(photoInfoEntityList);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (finalBitmap!=null) {
            finalBitmap.clearMemoryCache();
            finalBitmap.exitTasksEarly(true);
            finalBitmap = null;
        }
    }
}
