package com.github.xuqk.myviewdemo.flipborad;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.github.xuqk.myviewdemo.R;
import com.github.xuqk.myviewdemo.Utils;

/**
 * ClassName: FlipboardView <br/>
 * PackageName: com.github.xuqk.myviewdemo.flipborad <br/>
 * ProjectName: MyViewDemo <br/>
 * Create On: 6/25/18 7:47 PM <br/>
 * Site: http://www.zhenhao.com.cn <br/>
 *
 * @author: 徐乾琨 <br/>
 */

public class FlipboardView extends View {

    private static final String TAG = "FlipboardView";

    private Bitmap mBitmap;
    private int mSize;
    private int mLeft;
    private int mTop;
    private int mCenterX;
    private int mCenterY;
    private Paint mPaint;
    private Camera mCamera;
    private int mCameraZ;

    private ValueAnimator mAnimator0;
    private ValueAnimator mAnimator1;
    private ValueAnimator mAnimator2;

    /**动画旋转阶段旋转的角度，从0-270*/
    private int mRotation;
    /**动画静止阶段一半抬起的角度，从0-60*/
    private int mFirstAngle;
    private int mLastAngle;


    public FlipboardView(Context context) {
        super(context);
        init(context);
    }

    public FlipboardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FlipboardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.f);
        mSize = Utils.getScreenWidth(context) / 2;
        mLeft = mSize / 2;
        mTop = Utils.getScreenHeight(context) / 4;
        mCenterX = mLeft + mSize / 2;
        mCenterY = mTop + mSize / 2;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCamera = new Camera();
        float radio = ((float) mSize) / mBitmap.getWidth();
        Matrix matrix = new Matrix();
        matrix.preScale(radio, radio);
        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);

        mCameraZ = (int) (context.getResources().getDisplayMetrics().density * -6);

        initAnim();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 左半部分
        canvas.save();
        mCamera.save();
        canvas.translate(mCenterX, mCenterY);
        canvas.rotate(-mRotation);
        mCamera.setLocation(0, 0, mCameraZ);
        mCamera.rotateY(-mFirstAngle);
        mCamera.applyToCanvas(canvas);
        mCamera.restore();
        canvas.clipRect(0, - mSize, mSize, mSize);
        canvas.rotate(mRotation);
        canvas.translate(-mCenterX, -mCenterY);
        canvas.drawBitmap(mBitmap, mLeft, mTop, mPaint);
        canvas.restore();

        // 右半部分
        canvas.save();
        mCamera.save();
        canvas.translate(mCenterX, mCenterY);
        canvas.rotate(-mRotation);
        mCamera.setLocation(0, 0, mCameraZ);
        mCamera.rotateY(mLastAngle);
        mCamera.applyToCanvas(canvas);
        mCamera.restore();
        canvas.clipRect(-mSize, -mSize, 0, mSize);
        canvas.rotate(mRotation);
        canvas.translate(-mCenterX, -mCenterY);
        canvas.drawBitmap(mBitmap, mLeft, mTop, mPaint);
        canvas.restore();
        canvas.save();
    }

    private void initAnim() {
        mAnimator0 = ValueAnimator.ofInt(0, 60);
        mAnimator0.setDuration(600);
        mAnimator0.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator0.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mFirstAngle = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        mAnimator0.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mFirstAngle = 0;
                mRotation = 0;
                mLastAngle = 0;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimator1.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        mAnimator1 = ValueAnimator.ofInt(0, 270);
        mAnimator1.setDuration(1600)
                .setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRotation = (int) animation.getAnimatedValue();
                postInvalidate();
            }});
        mAnimator1.setStartDelay(150);
        mAnimator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimator2.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        mAnimator2 = ValueAnimator.ofInt(0, 60);
        mAnimator2.setDuration(600);
        mAnimator2.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLastAngle = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        mAnimator2.setStartDelay(150);
    }

    public void start() {
        mAnimator0.cancel();
        mAnimator1.cancel();
        mAnimator2.cancel();
        mAnimator0.start();
    }
}
