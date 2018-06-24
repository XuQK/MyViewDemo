package com.github.xuqk.myviewdemo.like;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.github.xuqk.myviewdemo.R;
import com.github.xuqk.myviewdemo.Utils;

/**
 * ClassName: ScrollNumberView <br/>
 * PackageName: com.github.xuqk.myviewdemo.like <br/>
 * ProjectName: MyViewDemo <br/>
 * Create On: 6/18/18 10:24 PM <br/>
 * Site: http://www.zhenhao.com.cn <br/>
 *
 * @author: 徐乾琨 <br/>
 */

public class ScrollNumberView extends View {

    private static final String TAG = "ScrollNumberView";

    private final int duration = 150;

    private Context mContext;
    /**之前的数字*/
    private String mPreNumber;
    /**之前的数字中要移出去的部分*/
    private String mPreScrollNumber;
    /**现在的数字*/
    private String mCurrentNumber;
    /**现在的数字中要移进来的部分*/
    private String mCurrentScrollNumber;
    /**固定不动的部分*/
    private String mFixedNumber;
    private float mTextSize;
    private int mTextColor;
    private int mMaxCharCount;
    private float mProgress;
    private int mTimePassed;

    private int mHeight;
    private int mWidth;

    private Paint mFixTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mScrollTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int mFixedTextBaseLine;
    private int mFixedTextEndIndex;
    private int mFixedTextRight;
    private int mScrollTextBaseLine;

    private int mAddNumber;

    public ScrollNumberView(Context context) {
        super(context);
        mContext = context;
        init(null);
    }

    public ScrollNumberView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    public ScrollNumberView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.ScrollNumberView);
        mTextSize = array.getDimension(R.styleable.ScrollNumberView_textSize, Utils.dpToPx(mContext, 16));
        mTextColor = array.getColor(R.styleable.ScrollNumberView_textColor, Color.parseColor("#FFA7A7A7"));
        mCurrentNumber = array.getString(R.styleable.ScrollNumberView_number);
        if (mCurrentNumber == null || mCurrentNumber.isEmpty()) {
            mCurrentNumber = "0";
        }
        mFixedNumber = mCurrentNumber;
        mMaxCharCount = array.getInt(R.styleable.ScrollNumberView_maxCount, 4);
        array.recycle();

        mProgress = 1;

        initTextPaint();
        computeViewSize();

    }

    /**
     * 初始化文字风格
     */
    private void initTextPaint() {
        mFixTextPaint.setColor(mTextColor);
        mFixTextPaint.setTextSize(mTextSize);
        mScrollTextPaint.setTextSize(mTextSize);
        mScrollTextPaint.setColor(mTextColor);
    }

    /**
     * 通过文字计算view的尺寸，高度是文字高度x3，宽度是mMaxCharCount*9的字符串宽度
     */
    private void computeViewSize() {
        StringBuilder maxString = new StringBuilder();
        for (int i = 0; i < mMaxCharCount; i++) {
            maxString.append("9");
        }

        mWidth = (int) mFixTextPaint.measureText(maxString.toString());
        mHeight = (int) (mFixTextPaint.getFontSpacing() * 3);
        // 设置数字baseline为中间那行
        mFixedTextBaseLine = (int) (mFixTextPaint.getFontMetrics().leading - mFixTextPaint.getFontMetrics().top + mFixTextPaint.getFontSpacing());
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
        drawFixedNumber(canvas);
        if (mAddNumber != 0) {
            drawOldNumber(canvas);
            drawNewNumber(canvas);
        }
    }

    /**绘制固定数字，包括初次初始化及数字变化过程中的前半部分数字*/
    private void drawFixedNumber(Canvas canvas) {
        canvas.save();
        canvas.drawText(mFixedNumber, 0, mFixedTextBaseLine, mFixTextPaint);
        canvas.restore();
    }

    private void drawNewNumber(Canvas canvas) {
        // 入场数字的动作，数字透明度从0-1变化
        float moveToBaseLine;
        // 有时会会出现progress>1的情况，此时progress一律设置为1
        if (mProgress > 1) {
            mProgress = 1;
        }
        mScrollTextPaint.setAlpha((int) (255 * mProgress));
        if (mAddNumber > 0) {
            // 如果数字增加，入场数字要往上移，其baseline是从最底下到中间，透明度增加
            moveToBaseLine = mFixedTextBaseLine + mScrollTextPaint.getFontSpacing() * (1 - mProgress);
        } else {
            // 如果数字减小，入场数字要从上往下移，其baseline是从最顶上那个到中间，透明度增加
            moveToBaseLine = mFixedTextBaseLine - mScrollTextPaint.getFontSpacing() * (1 - mProgress);
        }
        canvas.save();
        canvas.drawText(mCurrentScrollNumber, mFixedTextRight, moveToBaseLine, mScrollTextPaint);
        canvas.restore();
    }

    private void drawOldNumber(Canvas canvas) {
        // 出场数字的动作，透明度持续变小直至消失
        float moveToBaseLine;
        // 有时会会出现progress>1的情况，此时progress一律设置为1
        if (mProgress > 1) {
            mProgress = 1;
        }
        mScrollTextPaint.setAlpha((int) (255 * (1 - mProgress)));
        if (mAddNumber > 0) {
            // 如果数字增加，出场数字要往上移，其baseline是从中间到最上层
            moveToBaseLine = mFixedTextBaseLine - mScrollTextPaint.getFontSpacing() * mProgress;
        } else {
            // 如果数字减小，出场数字要往下移，其baseline是中间到最下层
            moveToBaseLine = mFixedTextBaseLine + mScrollTextPaint.getFontSpacing() * mProgress;
        }
        canvas.save();
        canvas.drawText(mPreScrollNumber, mFixedTextRight, moveToBaseLine, mScrollTextPaint);
        canvas.restore();
    }

    /**
     * 计算出最后一个无变化的数字的index
     */
    public void computeFixChar(int changeNumber) {
        if (changeNumber == 0) {
            return;
        }
        mAddNumber = changeNumber;
        int preNumber = Integer.parseInt(mCurrentNumber);
        int currentNumber = preNumber + changeNumber;

        mPreNumber = mCurrentNumber;
        mCurrentNumber = String.valueOf(currentNumber);

        char[] pre = mPreNumber.toCharArray();
        char[] after = mCurrentNumber.toCharArray();
        int maxLength = changeNumber > 0 ? after.length : pre.length;
        for (int i = 0; i < maxLength; i++) {
            if (pre[i] != after[i]) {
                mFixedTextEndIndex = i - 1;
                break;
            }
        }

        if (mFixedTextEndIndex < 0) {
            mFixedTextRight = 0;
            mFixedNumber = "";
            mPreScrollNumber = mPreNumber;
            mCurrentScrollNumber = mCurrentNumber;
        } else {
            mFixedNumber = mCurrentNumber.substring(0, mFixedTextEndIndex + 1);
            mFixedTextRight = (int) mFixTextPaint.measureText(mFixedNumber);
            mPreScrollNumber = mPreNumber.substring(mFixedTextEndIndex + 1, mPreNumber.length());
            mCurrentScrollNumber = mCurrentNumber.substring(mFixedTextEndIndex + 1, mCurrentNumber.length());
        }
    }

    public void changeNumber(int changeNumber) {
        if (mTimePassed < duration) {
            mTimePassed = duration;
            postInvalidate();
        }
        computeFixChar(changeNumber);
        mProgress = 0;
        mTimePassed = 0;
        playAnimate();
    }

    public void playAnimate() {
        mTimePassed += 10;
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgress = (float) mTimePassed / (float) duration;
                if (mProgress <= 1) {
                    postInvalidate();
                    playAnimate();
                }
            }
        }, 10);
    }

    public String getCurrentNumber() {
        return mCurrentNumber == null ? "0" : mCurrentNumber;
    }

    public void setCurrentNumber(@NonNull String currentNumber, boolean animate) {
        if (currentNumber.isEmpty()) {
            currentNumber = "0";
        }
        if (mTimePassed < duration) {
            mTimePassed = duration;
            postInvalidate();
        }
        if (animate) {
            changeNumber(Integer.parseInt(currentNumber) - Integer.parseInt(mCurrentNumber));
        } else {
            mCurrentNumber = currentNumber;
            postInvalidate();
        }
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float textSize) {
        mTextSize = textSize;
        postInvalidate();
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
        postInvalidate();
    }

    public int getMaxCharCount() {
        return mMaxCharCount;
    }

    public void setMaxCharCount(int maxCharCount) {
        mMaxCharCount = maxCharCount;
    }

    public float getProgress() {
        return mProgress;
    }

    public void setProgress(float progress) {
        mProgress = progress;
        postInvalidate();
    }
}
