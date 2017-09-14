package com.yxc.demo.dynawave.utils;

import android.content.Context;

/**
 * Created by yuexingchuan on 17/9/14.
 */

public class UIUtils {

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
