# PageTransition
页面切换动画效果demo

我们要实现的效果是:页面跳转的时候,A页面类似开门的效果,打开过程中逐渐向两边拉开,显示出B页面,退回时显示A页面拉回,关闭B页面.

思路:因为在A页面效果拉开的时候B页面内容是在后边显示的,所以效果应该是在B页面里面实现,那么我们先将A页面的跳转前的页面截图传给B页面,然后在B页面中进行开门动画,在返回的时候进行闭门动画.

思路形成,开始动手coding.

### 先获取A页面的视图截图

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

### 传给B页面

考虑到传统的使用`Intent``bundle`传递数据大小的限制,我们直接使用`EventBus`传递数据,这里需要用粘性事件.

		EventBus.getDefault().postSticky(new BusEvent(bmp));
        

页面跳转

		Intent intent = new Intent(MainActivity.this, TargetActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);

### B页面接收处理

监听`eventbus`粘性事件

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onReceiveBitmapEvent(BusEvent event) {
        if (event != null) {
            mBitmap = event.getData();
            
        }
    }

对接收到的截图进行转换,转为左右2个图片

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

B页面布局xml
	
	<?xml version="1.0" encoding="utf-8"?>
	<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
	    xmlns:app="http://schemas.android.com/apk/res-auto"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent"
	    android:background="@color/purple_200">
	
	    <TextView
	        android:layout_width="wrap_content"
	        android:layout_height="wrap_content"
	        android:text="B activity content"
	        app:layout_constraintBottom_toBottomOf="parent"
	        app:layout_constraintLeft_toLeftOf="parent"
	        app:layout_constraintRight_toRightOf="parent"
	        app:layout_constraintTop_toTopOf="parent" />
	
	    <androidx.constraintlayout.widget.ConstraintLayout
	        android:layout_width="match_parent"
	        android:layout_height="match_parent">
	
	        <ImageView
	            android:id="@+id/iv_cover_left"
	            android:layout_width="0dp"
	            android:layout_height="match_parent"
	            app:layout_constraintBottom_toBottomOf="parent"
	            app:layout_constraintLeft_toLeftOf="parent"
	            app:layout_constraintRight_toLeftOf="@+id/iv_cover_right"
	            app:layout_constraintTop_toTopOf="parent" />
	
	        <ImageView
	            android:id="@+id/iv_cover_right"
	            android:layout_width="0dp"
	            android:layout_height="match_parent"
	            app:layout_constraintBottom_toBottomOf="parent"
	            app:layout_constraintLeft_toRightOf="@+id/iv_cover_left"
	            app:layout_constraintRight_toRightOf="parent"
	            app:layout_constraintTop_toTopOf="parent" />
	    </androidx.constraintlayout.widget.ConstraintLayout>
	
	</androidx.constraintlayout.widget.ConstraintLayout>


加载到遮罩图片布局上并执行开门动画

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

返回效果

    @Override
    public void onBackPressed() {
		//super.onBackPressed();
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

### 效果实现完成

通过这个思路可以实现很多切换动画效果.
