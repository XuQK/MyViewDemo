package com.github.xuqk.myviewdemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * ClassName: MyViewPager <br/>
 * PackageName: com.github.xuqk.myviewdemo <br/>
 * ProjectName: MyViewDemo <br/>
 * Create On: 6/21/18 10:55 PM <br/>
 * Site: http://www.zhenhao.com.cn <br/>
 *
 * @author: 徐乾琨 <br/>
 */

public class MyViewPager extends ViewPager {
    public MyViewPager(@NonNull Context context) {
        super(context);
    }

    public MyViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        int x =
        return ev.getAction() != MotionEvent.ACTION_DOWN;
    }
}
