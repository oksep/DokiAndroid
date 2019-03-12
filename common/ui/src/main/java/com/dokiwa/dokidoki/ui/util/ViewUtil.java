package com.dokiwa.dokidoki.ui.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.*;

import java.util.Random;

public class ViewUtil {

    public static int getRootLeft(View myView) {
        if (myView == null) {
            return 0;
        } else if (myView.getParent() == myView.getRootView()) {
            return myView.getLeft();
        } else {
            return myView.getLeft() + getRootLeft((View) myView.getParent());
        }
    }

    public static int getRootTop(View myView) {
        if (myView == null) {
            return 0;
        } else if (myView.getParent() == myView.getRootView()) {
            return myView.getTop();
        } else {
            return myView.getTop() + getRootTop((View) myView.getParent());
        }
    }

    public static void adapterAboveNavigation(final View v) {
        if (v == null) {
            return;
        }

        if (!hasNavigationBar(v.getContext())) {
            return;
        }

        int navigationBarHeight = getNavigationBarHeight(v.getResources());
        if (navigationBarHeight > 0) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            layoutParams.bottomMargin = layoutParams.bottomMargin + navigationBarHeight;
            v.requestLayout();
        }
    }

    public static boolean hasNavigationBar(Context context) {
        boolean hasSoftwareKeys;

        if (context instanceof Activity) {

            Activity activity = (Activity) context;
            Display d = activity.getWindowManager().getDefaultDisplay();

            DisplayMetrics realDisplayMetrics = new DisplayMetrics();
            d.getRealMetrics(realDisplayMetrics);

            int realHeight = realDisplayMetrics.heightPixels;
            int realWidth = realDisplayMetrics.widthPixels;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            d.getMetrics(displayMetrics);

            int displayHeight = displayMetrics.heightPixels;
            int displayWidth = displayMetrics.widthPixels;

            hasSoftwareKeys = (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
        } else {
            // just consider that has the permanent back menu, so do not need navigation bar.
            boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
            boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            hasSoftwareKeys = !hasMenuKey && !hasBackKey;
        }
        return hasSoftwareKeys;
    }

    public static int getNavigationBarHeight(Resources res) {

        int navigationBarHeight = 0;
        int navigationBarHeightId =
                res.getIdentifier("navigation_bar_height", "dimen", "android");
        if (navigationBarHeightId > 0) {
            navigationBarHeight = res.getDimensionPixelSize(navigationBarHeightId);
        }

        return navigationBarHeight;

    }

    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        int statusBarHeightId = context.getResources()
                .getIdentifier("status_bar_height", "dimen", "android");
        if (statusBarHeightId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(statusBarHeightId);
        }
        return statusBarHeight;
    }

    public static void adapterBelowStatus(final View v) {
        if (v == null) {
            return;
        }

        int statusBarHeight = 0;
        int statusBarHeightId = v.getResources()
                .getIdentifier("status_bar_height", "dimen", "android");
        if (statusBarHeightId > 0) {
            statusBarHeight = v.getResources().getDimensionPixelSize(statusBarHeightId);
        }

        if (statusBarHeightId > 0) {
            ViewGroup.MarginLayoutParams layoutParams
                    = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            layoutParams.topMargin = layoutParams.topMargin + statusBarHeight;
            v.requestLayout();
        }
    }

    public static int[] getSize(final View view) {
        if (view == null) {
            return null;
        }

        int[] size = new int[2];

        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );

        size[0] = view.getMeasuredWidth();
        size[1] = view.getMeasuredHeight();


        return size;
    }

    public static int dp2px(Context context, float dpValue) {
        return (int) dp2pxExact(context, dpValue);
    }

    public static float dp2pxExact(Context context, float dpValue) {
        if (context == null) {
            // may be context destroyed by activity, but still invoke by fragment
            return 0;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return dpValue * scale + 0.5f;
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 处理Android bug,改背景 padding被置位
     */
    public static void setBackgroundResource(final View v, final int resid) {
        if (v == null || resid < 0) {
            return;
        }

        int left = v.getPaddingLeft();
        int right = v.getPaddingRight();
        int top = v.getPaddingTop();
        int bottom = v.getPaddingBottom();

        v.setBackgroundResource(resid);
        v.setPadding(left, top, right, bottom);
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static void hideParentChildExceptSelf(View exceptedView) {
        exceptedView.setVisibility(View.VISIBLE);
        ViewGroup parent = (ViewGroup) exceptedView.getParent();
        if (parent != null) {
            int count = parent.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = parent.getChildAt(i);
                if (child != exceptedView) {
                    child.setVisibility(View.GONE);
                }
            }
        }
    }

    public static void visibleParentChildExceptSelf(View exceptedView) {
        exceptedView.setVisibility(View.GONE);
        ViewGroup parent = (ViewGroup) exceptedView.getParent();
        if (parent != null) {
            int count = parent.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = parent.getChildAt(i);
                if (child != exceptedView) {
                    child.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public static int randomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }
}
