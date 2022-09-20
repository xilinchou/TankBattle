package com.gamecentre.tankbattle.utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class CVTR {
    private static final DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
    private static final float metricsFloat = Resources.getSystem().getDisplayMetrics().density;

    public static int toPxl(int dp) {
        return (int)(metricsFloat*dp/160);
    }

    public static float toSp(int pxl) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, pxl, metrics);
    }

    public static float toDp(int pxl) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pxl, metrics);
    }
}
