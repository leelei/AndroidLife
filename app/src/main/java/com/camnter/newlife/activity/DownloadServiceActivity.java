package com.camnter.newlife.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.camnter.newlife.R;
import com.camnter.newlife.service.DownloadService;
import com.camnter.newlife.service.IBinderView;
import com.camnter.newlife.utils.ImageUtil;

/**
 * Description：DownloadServiceActivity
 * Created by：CaMnter
 * Time：2015-11-16 14:58
 */
public class DownloadServiceActivity extends AppCompatActivity implements View.OnClickListener, IBinderView {

    private static final String OBJECT_IMAGE_URL = "http://img.blog.csdn.net/20150913233900119";

    private Button startBT;
    private ImageView imageIV;
    private DownloadService service;

    private ServiceConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_download_service);
        this.initViews();
        this.initData();
        this.initListeners();
    }

    private void initViews() {
        TextView imageTV = (TextView) this.findViewById(R.id.image_tv);
        imageTV.setText(OBJECT_IMAGE_URL);

        this.startBT = (Button) this.findViewById(R.id.start_service_bt);
        this.imageIV = (ImageView) this.findViewById(R.id.image_iv);
    }

    private void initData() {
        this.connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                DownloadService.DownloadServiceBinder binder = (DownloadService.DownloadServiceBinder) service;
                binder.iBinderView = DownloadServiceActivity.this;
                DownloadServiceActivity.this.service = binder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                DownloadServiceActivity.this.service = null;
            }
        };

        DownloadServiceActivity.this.bindService(
                new Intent(DownloadServiceActivity.this, DownloadService.class),
                DownloadServiceActivity.this.connection,
                Context.BIND_AUTO_CREATE
        );
    }

    private void initListeners() {
        this.startBT.setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_service_bt:
                this.service.startDownload(OBJECT_IMAGE_URL);
                break;
        }
    }

    /**
     * 开始下载
     */
    @Override
    public void downloadStart() {
        this.startBT.setEnabled(false);
    }

    /**
     * 下载成功
     *
     * @param imageFilePath
     */
    @Override
    public void downloadSuccess(String imageFilePath) {
        /**
         * 设置按钮可用，并隐藏Dialog
         */
        this.startBT.setEnabled(true);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        /**
         * ImageUtil.decodeScaleImage 解析图片
         */
        Bitmap bitmap = ImageUtil.decodeScaleImage(imageFilePath, screenWidth, screenHeight);
        DownloadServiceActivity.this.imageIV.setImageBitmap(bitmap);
    }

    /**
     * 下载失败
     */
    @Override
    public void downloadFailure() {
        this.startBT.setEnabled(true);
    }

}
