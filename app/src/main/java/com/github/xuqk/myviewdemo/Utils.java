package com.github.xuqk.myviewdemo;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * ClassName: Utils <br/>
 * PackageName: com.github.xuqk.myviewdemo.like <br/>
 * ProjectName: MyViewDemo <br/>
 * Create On: 6/18/18 10:54 PM <br/>
 * Site: http://www.zhenhao.com.cn <br/>
 *
 * @author: 徐乾琨 <br/>
 */

public class Utils {

    public static float dpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static float pxToDp(Context context, float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static int dpToPxInt(Context context, float dp) {
        return (int) (dpToPx(context, dp) + 0.5f);
    }

    public static int pxToDpCeilInt(Context context, float px) {
        return (int) (pxToDp(context, px) + 0.5f);
    }

    /**
     * 获取屏幕宽度
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }
}
