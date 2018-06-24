package com.github.xuqk.myviewdemo.ruler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.OverScroller;
import android.widget.ScrollView;
import android.widget.Scroller;

import com.github.xuqk.myviewdemo.R;
import com.github.xuqk.myviewdemo.Utils;

/**
 * ClassName: RulerView <br/>
 * PackageName: com.github.xuqk.myviewdemo.ruler <br/>
 * ProjectName: MyViewDemo <br/>
 * Create On: 6/19/18 9:09 PM <br/>
 * Site: http://www.zhenhao.com.cn <br/>
 *
 * @author: 徐乾琨 <br/>
 *
 * 规定尺子主刻度宽2dp，长16dp；次刻度宽1dp，长8dp；尺子高度为2倍主刻度长，即32dp
 */

public class RulerView extends View {

    private static final String TAG = "RulerView";

    private int mRulerHeight = 64;
    private int mRulerBigScaleWidth = 2;
    private int mRulerSmallScaleWidth = 1;
    private int mRulerBigScaleLength = 32;
    private int mRulerSmallScaleLength = 16;
    private int mRulerScaleGap = 8;

    private Context mContext;
    private int mWidth;
    private int mHeight;

    private int mLastX;
    private int mLastY;
    private int mPreviousScrollX;
    private int mScreenWidth;
    /**是否需要调整结果尺寸精度*/
    private boolean mAdapterScale;

    /**尺子的上下限，整数*/
    private int mUpperLimit;
    private int mLowerLimit;
    private int mDefaultScaleText;

    /**尺子背景*/
    private int mRulerBackground;
    /**尺子刻度颜色*/
    private int mRulerScaleColor;
    /**尺子刻度文字颜色*/
    private int mRulerScaleTextColor;
    /**尺子刻度文字大小*/
    private int mRulerScaleTextSize;
    /**当前刻度文字颜色*/
    private int mCurrentScaleTextColor;
    /**当前刻度文字尺寸*/
    private int mCurrentScaleTextSize;

    /**当前刻度数值*/
    private float mCurrentScale;
    private int mGapTextToRuler;

    private Paint mCurrentScaleTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mScaleTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mScalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path mIndicatorPath;

    private OverScroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mMinimumFlingVelocity;
    private int mMaximumFlingVelocity;

    public RulerView(Context context) {
        super(context);
        mContext = context;
        init(null);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mScreenWidth = Utils.getScreenWidth(mContext);
        mRulerHeight = Utils.dpToPxInt(mContext,mRulerHeight);
        mRulerBigScaleWidth =Utils.dpToPxInt(mContext, mRulerBigScaleWidth);
        mRulerSmallScaleWidth =Utils.dpToPxInt(mContext,mRulerSmallScaleWidth);
        mRulerBigScaleLength = Utils.dpToPxInt(mContext,mRulerBigScaleLength);
        mRulerSmallScaleLength = Utils.dpToPxInt(mContext,mRulerSmallScaleLength);
        mRulerScaleGap =Utils.dpToPxInt(mContext, mRulerScaleGap);

        TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.RulerView);
        mUpperLimit = array.getInt(R.styleable.RulerView_upper_limit, 200);
        mLowerLimit = array.getInt(R.styleable.RulerView_lower_limit, 40);
        mDefaultScaleText = array.getInt(R.styleable.RulerView_default_mark, 60);

        mRulerBackground = array.getColor(R.styleable.RulerView_ruler_background_color, Color.WHITE);
        mRulerScaleColor = array.getColor(R.styleable.RulerView_scale_color, Color.BLACK);
        mRulerScaleTextColor = array.getColor(R.styleable.RulerView_scale_text_color, Color.parseColor("#FFFF0000"));
        mRulerScaleTextSize = (int) array.getDimension(R.styleable.RulerView_scale_text_size, Utils.dpToPx(mContext, 16));
        mCurrentScaleTextColor = array.getColor(R.styleable.RulerView_current_scale_text_color, Color.parseColor("#FFFF0000"));
        mCurrentScaleTextSize = (int) array.getDimension(R.styleable.RulerView_current_scale_text_size, Utils.dpToPx(mContext, 20));
        mGapTextToRuler = (int) array.getDimension(R.styleable.RulerView_gap_text_to_ruler, Utils.dpToPx(mContext, 12));
        array.recycle();

        mCurrentScaleTextPaint.setColor(mCurrentScaleTextColor);
        mCurrentScaleTextPaint.setTextSize(mCurrentScaleTextSize);
        mScaleTextPaint.setColor(mRulerScaleTextColor);
        mScaleTextPaint.setTextSize(mRulerScaleTextSize);
        mScalePaint.setColor(mRulerScaleColor);
        mScalePaint.setStyle(Paint.Style.FILL);
        mIndicatorPath = new Path();

        mWidth = mScreenWidth + mRulerScaleGap * (mUpperLimit - mLowerLimit);
        mHeight = (int) (mCurrentScaleTextPaint.getFontSpacing() + mGapTextToRuler + mRulerHeight);

        mScroller = new OverScroller(mContext);
        final ViewConfiguration configuration = ViewConfiguration.get(mContext);
        mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mWidth, mHeight);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mWidth, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, mHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawRulerBackground(canvas);
        drawRulerScale(canvas);
        drawIndicator(canvas);
        drawCurrentScaleText(canvas);
    }

    private void drawRulerBackground(Canvas canvas) {
        canvas.save();
        canvas.clipRect(0, mHeight - mRulerHeight, mWidth, mHeight);
        canvas.drawColor(mRulerBackground);
        canvas.restore();
    }

    private void drawRulerScale(Canvas canvas) {
        int currentScaleX = mScreenWidth / 2;
        int rulerTop = mHeight - mRulerHeight;

        canvas.save();
        for (int i = 0; i <= mUpperLimit - mLowerLimit ; i++) {
            if (i % 10 == 0) {
                // 画主刻度
                canvas.drawRect((float) (currentScaleX - mRulerBigScaleWidth / 2), (float) rulerTop, (float) (currentScaleX + mRulerBigScaleWidth / 2), (float) (rulerTop + mRulerBigScaleLength), mScalePaint);
                // 画主刻度读数
                String currentScaleText = String.valueOf(mLowerLimit + i);
                float currentScaleTextWidth = mScaleTextPaint.measureText(currentScaleText);
                float currentScaleTextHeight = mScaleTextPaint.getFontSpacing();
                canvas.drawText(currentScaleText, currentScaleX - currentScaleTextWidth / 2, currentScaleTextHeight + rulerTop + mRulerBigScaleLength, mScaleTextPaint);
            } else {
                // 画副刻度
                canvas.drawRect((float) (currentScaleX - mRulerSmallScaleWidth / 2), (float) rulerTop, (float) (currentScaleX + mRulerSmallScaleWidth / 2), (float) (rulerTop + mRulerSmallScaleLength), mScalePaint);
            }
            currentScaleX += mRulerScaleGap;
        }
        canvas.restore();
    }

    private void drawIndicator(Canvas canvas) {
        // 三角形指示器顶边终点的坐标，其顶边长为两倍尺子刻度，顶边到定点距离为1/2副刻度长度
        int indicatorX = getScrollX() + mScreenWidth / 2;
        int indicatorY = mHeight - mRulerHeight;

        mIndicatorPath.reset();
        mIndicatorPath.moveTo(indicatorX - mRulerScaleGap, indicatorY);
        mIndicatorPath.lineTo(indicatorX + mRulerScaleGap, indicatorY);
        mIndicatorPath.lineTo(indicatorX, indicatorY + mRulerSmallScaleLength / 2);

        canvas.drawPath(mIndicatorPath, mScalePaint);
    }

    private void drawCurrentScaleText(Canvas canvas) {
        canvas.save();
        float currentScaleTextWidth = mCurrentScaleTextPaint.measureText(String.valueOf(mCurrentScale));
        canvas.drawText(String.valueOf(mCurrentScale), getScrollX() + mScreenWidth / 2 - currentScaleTextWidth / 2, mHeight - mRulerHeight - mGapTextToRuler, mCurrentScaleTextPaint);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        int scrollX = getScrollX();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = mLastX - x;

                scrollBy(deltaX, 0);


                break;
            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                final int pointerId = event.getPointerId(0);
                velocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
                final float velocityX = velocityTracker.getXVelocity(pointerId);
                if (Math.abs(velocityX) > mMinimumFlingVelocity) {
                    mScroller.fling(scrollX, 0, (int) -velocityX, 0, 0, Math.max(0, mWidth - mScreenWidth), 0, 0);
                    invalidate();
                }
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }

                break;
            default:
        }

        mAdapterScale = true;
        mLastY = y;
        mLastX = x;
        return true;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);

            // 检测最后一次滑动
            if (!mScroller.computeScrollOffset() && mAdapterScale) {
                mAdapterScale = false;
                mScroller.startScroll(getScrollX(), 0, (int) (mCurrentScale - mLowerLimit) * mRulerScaleGap - getScrollX(), 0);
            }
            postInvalidate();
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        if (x < 0) {
            x = 0;
        }
        int endX = mWidth - mScreenWidth;
        if (x > endX) {
            x = endX;
        }
        if (x != getScrollX()) {
            super.scrollTo(x, 0);
        }

        mCurrentScale = Math.round(x / (float) mRulerScaleGap) + mLowerLimit;
    }
}
