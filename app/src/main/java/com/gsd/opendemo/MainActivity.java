package com.gsd.opendemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by gsd on 2021/3/2.
 * Copyright © 2021 GSD. All rights reserved.
 */

public class MainActivity extends AppCompatActivity {

    private View mRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRoot = findViewById(R.id.cl_root);
        Button btnEnter = findViewById(R.id.btn_enter);
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bmp = getBitmapOfView(mRoot);
                EventBus.getDefault().postSticky(new BusEvent(bmp));
                Intent intent = new Intent(MainActivity.this, TargetActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }

    /**
     * 获取指定view的截图
     *
     * @param view
     * @return
     */
    public Bitmap getBitmapOfView(View view) {
        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmap));
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bitmap;
    }
}