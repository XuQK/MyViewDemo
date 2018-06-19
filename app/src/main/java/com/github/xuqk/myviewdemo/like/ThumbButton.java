package com.github.xuqk.myviewdemo.like;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.github.xuqk.myviewdemo.R;

/**
 * ClassName: LikeButton <br/>
 * PackageName: com.github.xuqk.myviewdemo.like <br/>
 * ProjectName: MyViewDemo <br/>
 * Create On: 6/18/18 3:32 PM <br/>
 * Site: http://www.zhenhao.com.cn <br/>
 *
 * @author: 徐乾琨 <br/>
 */

public class ThumbButton extends View {

    private static final String TAG = "ThumbButton";

    private final int duration = 300;

    private Context mContext;
    private Bitmap mThumbLikeBitmap;
    private Bitmap mThumbUnLikeBitmap;
    private Bitmap mThumbShineBitmap;
    private int mThumbWidth;
    private int mThumbHeight;
    private int mViewHeight;
    private int mViewWidth;
    private int mCircleRadius;
    private float mProgress;
    private int mTimePassed;

    private Paint mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mShiningPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private boolean mLiked;

    public ThumbButton(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public ThumbButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public ThumbButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        mThumbUnLikeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_messages_like_unselected);
        mThumbLikeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_messages_like_selected);
        mThumbShineBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_messages_like_selected_shining);
        mThumbHeight = mThumbLikeBitmap.getHeight();
        mThumbWidth = mThumbLikeBitmap.getWidth();
        // view宽度就是大拇指宽度
        mViewWidth = mThumbLikeBitmap.getWidth();
        // view高度是大拇指高度加上闪光高度
        mViewHeight = mThumbLikeBitmap.getHeight() + mThumbShineBitmap.getHeight();

        // 圆圈直径设置为大拇指宽度的1.5倍最合适
        mCircleRadius = (int) (mViewWidth * 1.5) / 2;
        mTimePassed = (int) (mProgress * duration);
        mCirclePaint.setColor(ContextCompat.getColor(mContext, R.color.red));
        mCirclePaint.setStyle(Paint.Style.STROKE);

        // 完成度默认是1，用于View初始化时的绘制，调用动画时手动设置成0
        mProgress = 1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mCircleRadius * 2, mViewHeight);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mCircleRadius * 2, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, mViewHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mLiked) {
            drawLikeCircle(canvas);
            drawLikeThumb(canvas);
            drawLikeShining(canvas);
        } else {
            drawUnLikeThumb(canvas);
            drawUnLikeShining(canvas);
        }
    }

    private void drawLikeCircle(Canvas canvas) {
        // 动画进程到0.9时圆圈消失，进程到0.2时，圆圈动画开始执行
        // 圆圈线条宽度，根据动画完成度变宽
        float radius;
        if (mProgress < 0.3) {
            return;
        } else if (mProgress >= 0.3 && mProgress < 0.9) {
            mCirclePaint.setStrokeWidth(4 * mProgress);
            radius = (float) ((mCircleRadius - 2)) * mProgress;
            // 圆圈透明度变化从完成度0.5开始，由1变为0.2
            if (mProgress > 0.5) {
                float circleAlpha = (0.9f - mProgress) / 0.5f;
                mCirclePaint.setAlpha((int) (circleAlpha * 255));
            }
        } else {
            return;
        }

        canvas.save();
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, mCirclePaint);
        canvas.restore();
    }

    private void drawLikeThumb(Canvas canvas) {

        float centerX = getWidth() / 2;
        float centerY = getHeight() / 2;
        float scale;
        float left = (getWidth() - mThumbWidth) / 2;
        float top = (getHeight() - mThumbHeight) / 2;
        Bitmap bitmap;
        if (mProgress < 0.3) {
            // 第一阶段，灰色大拇指变小到0.8倍
            // 完成度从0-0.3，对应灰色拇指放大倍数从1-0.8，
            scale = 1f - 2f / 3f * mProgress;
            bitmap = mThumbUnLikeBitmap;
        } else if (mProgress >= 0.3 && mProgress < 0.6) {
            // 完成度从0.3-0.6，对应灰色拇指放大倍数从0.8-1.2
            scale = 4f / 3f * mProgress + 0.4f;
            bitmap = mThumbLikeBitmap;
        } else if (mProgress >= 0.6 && mProgress < 0.8) {
            // 完成度从0.6-0.8，对应灰色拇指放大倍数从1.2-0.9
            scale = 2.1f - 1.5f * mProgress;
            bitmap = mThumbLikeBitmap;
        } else {
            // 完成度从0.8-1，对应灰色拇指放大倍数从0.9-1
            scale = 0.5f * mProgress + 0.5f;
            bitmap = mThumbLikeBitmap;
        }
        canvas.save();
        canvas.scale(scale, scale, centerX, centerY);
        canvas.drawBitmap(bitmap, left, top, null);
        canvas.restore();
    }

    private void drawLikeShining(Canvas canvas) {
        // 三个点在进度0.5的时候出现，0.8的时候放到最大
        float scale;
        float centerX = getWidth() / 2;
        float centerY = mThumbShineBitmap.getHeight() / 2;
        float left = (getWidth() - mThumbShineBitmap.getWidth()) / 2;

        if (mProgress < 0.5) {
            return;
        } else if (mProgress >= 0.5 && mProgress < 0.8) {
            // 完成度从0.5-0.8，闪光倍数从0.1-1
            scale = 3f * mProgress - 1.4f;
        } else {
            scale = 1;
        }
        canvas.save();
        canvas.scale(scale, scale, centerX, centerY);
        canvas.drawBitmap(mThumbShineBitmap, left, 0, null);
        canvas.restore();
    }

    private void drawUnLikeThumb(Canvas canvas) {

        float centerX = getWidth() / 2;
        float centerY = getHeight() / 2;
        float scale;
        float left = (getWidth() - mThumbWidth) / 2;
        float top = (getHeight() - mThumbHeight) / 2;
        Bitmap bitmap;
        if (mProgress < 0.5) {
            // 第一阶段倍数1-0.8，红色拇指
            scale = 1f - 0.4f * mProgress;
            bitmap = mThumbLikeBitmap;
        } else {
            // 第二阶段倍数0.8-1，灰色拇指
            scale = 0.4f * mProgress + 0.6f;
            bitmap = mThumbUnLikeBitmap;
        }

        canvas.save();
        canvas.scale(scale, scale, centerX, centerY);
        canvas.drawBitmap(bitmap, left, top, null);
        canvas.restore();
    }

    private void drawUnLikeShining(Canvas canvas) {
        // 三个点在进度0.5的时候出现，0.8的时候放到最大
        float left = (getWidth() - mThumbShineBitmap.getWidth()) / 2;

        if (mProgress < 0.5) {
            mShiningPaint.setAlpha(255);
        } else if (mProgress < 1) {
            // 完成度从0.5-1，透明度从1-0
            float alpha = 2f - 2f * mProgress;
            mShiningPaint.setAlpha((int) (alpha * 255));
        } else {
            return;
        }
        canvas.save();
        canvas.drawBitmap(mThumbShineBitmap, left, 0, mShiningPaint);
        canvas.restore();
    }

    public void playAnimator() {
        mTimePassed += 10;
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgress = (float) mTimePassed / (float) duration;
                if (mProgress <= 1) {
                    postInvalidate();
                    playAnimator();
                }
            }
        }, 10);
    }

    public void startAnimator() {
        mProgress = 0;
        mTimePassed = 0;
        mCirclePaint.setAlpha(255);
        mShiningPaint.setAlpha(255);
        playAnimator();
    }

    public boolean isLiked() {
        return mLiked;
    }

    public void setLiked(boolean liked, boolean animate) {
        if (liked != mLiked) {
            if (mTimePassed < duration) {
                mTimePassed = duration;
                postInvalidate();
            }
            mLiked = liked;
            if (animate) {
                startAnimator();
            } else {
                postInvalidate();
            }
        }
    }

    public float getProgress() {
        return mProgress;
    }

    public void setProgress(float progress) {
        mProgress = progress;
        if (mTimePassed < duration) {
            mTimePassed = duration;
        }
        postInvalidate();
    }
}