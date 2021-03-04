package com.gsd.opendemo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by gsd on 2021/3/2.
 * Copyright © 2021 GSD. All rights reserved.
 */

public class TargetActivity extends AppCompatActivity {

    private Bitmap mBitmap;
    private ImageView mIvCoverLeft;
    private ImageView mIvCoverRight;
    private View mCover;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);
        init();
        EventBus.getDefault().register(this);
    }

    private void init() {
        mCover = findViewById(R.id.cl_cover);
        mIvCoverLeft = findViewById(R.id.iv_cover_left);
        mIvCoverRight = findViewById(R.id.iv_cover_right);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onReceiveBitmapEvent(BusEvent event) {
        if (event != null) {
            mBitmap = event.getData();
            doAnimation();
        }
    }

    /**
     * 将图片切分为左右2个
     *
     * @param bitmap
     * @return
     */
    private Bitmap[] splitBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int nw = width / 2;
        int nh = height;
        Bitmap[] bitmaps = new Bitmap[2];
        Bitmap left = Bitmap.createBitmap(bitmap, 0, 0, nw, nh, null, false);
        Bitmap right = Bitmap.createBitmap(bitmap, nw, 0, nw, nh, null, false);
        bitmaps[0] = left;
        bitmaps[1] = right;
        if (bitmap != null && !bitmap.equals(left) && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return bitmaps;
    }

    private void doAnimation() {
        Bitmap[] bitmaps = splitBitmap(mBitmap);
        Bitmap bitmapLeft = bitmaps[0];
        Bitmap bitmapRight = bitmaps[1];
        mIvCoverLeft.setImageBitmap(bitmapLeft);
        mIvCoverRight.setImageBitmap(bitmapRight);
        mIvCoverLeft.setVisibility(View.VISIBLE);
        mIvCoverRight.setVisibility(View.VISIBLE);
        mIvCoverLeft.animate()
                .setDuration(1000)
                .translationX(-bitmapLeft.getWidth())
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        mCover.setVisibility(View.GONE);
                    }
                })
                .start();
        mIvCoverRight.animate()
                .setDuration(1000)
                .translationX(bitmapRight.getWidth())
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        mCover.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        mIvCoverLeft.setVisibility(View.VISIBLE);
        mIvCoverRight.setVisibility(View.VISIBLE);
        mIvCoverLeft.animate()
                .setDuration(1000)
                .translationX(0)
                .start();
        mIvCoverRight.animate()
                .setDuration(1000)
                .translationX(0)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        overridePendingTransition(0, 0);
                    }
                })
                .start();
    }
}
