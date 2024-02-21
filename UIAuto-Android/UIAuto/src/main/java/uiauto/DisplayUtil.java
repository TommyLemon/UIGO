/*Copyright ©2020 TommyLemon(https://github.com/TommyLemon/UIAuto)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/

package uiauto;

import android.app.Service;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;


public class DisplayUtil {
    private static final String TAG = "DisplayUtil";

    public static int px2dp(Context context, float pxVal) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (pxVal / density + 0.5f);
    }

    public static int dp2px(Context context, float dpVal) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpVal * density + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取屏幕宽度（显示宽度，横屏的时候可能会小于物理像素值）
     */
    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getRealMetrics(metrics);
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            return metrics.widthPixels;
        }

        return metrics.widthPixels - getNavigationBarCurrentHeight(context);
    }

    /**
     * 获取屏幕高度（物理像素值的高度）
     */
    public static int getScreenHeight(Context context) {
        return getScreenSize(context).y;
    }

    /**
     * 获取屏幕宽高
     */
    public static Point getScreenSize(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();

        Point p = new Point();
        display.getRealSize(p);
        return p;
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static int statusBarHeight(View view) {
        return getStatusBarHeight(view.getContext().getApplicationContext());
    }

    /**
     * 获取导航栏真实的高度（可能未显示）
     */
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    /**
     * 获取导航栏当前的高度
     */
    public static int getNavigationBarCurrentHeight(Context context) {
        return hasNavigationBar(context) ? getNavigationBarHeight(context) : 0;
    }

    /**
     * 判断虚拟导航栏是否显示
     *
     * @param context 上下文对象
     * @return true(显示虚拟导航栏)，false(不显示或不支持虚拟导航栏)
     */
    public static boolean hasNavigationBar(Context context) {
        if (getNavigationBarHeight(context) == 0
            || (RomUtil.checkIsHuaweiRom() && isHuaWeiHideNav(context))
            || (RomUtil.checkIsMiuiRom() && isMiuiFullScreen(context))
            || (RomUtil.checkIsVivoRom() && isVivoFullScreen(context))
        ) {
            return false;
        }

        return isHasNavigationBar(context);
    }

    /**
     * 不包含导航栏的有效高度（没有导航栏，或者已去除导航栏的高度）
     */
    public static int rejectedNavHeight(Context context) {
        Point point = getScreenSize(context);
        if (point.x > point.y) {
            return point.y;
        }
        return point.y - getNavigationBarCurrentHeight(context);
    }

    /**
     * 华为手机是否隐藏了虚拟导航栏
     * @return true 表示隐藏了，false 表示未隐藏
     */
    public static boolean isHuaWeiHideNav(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return Settings.System.getInt(context.getContentResolver(), "navigationbar_is_min", 0) != 0;
        }
        return Settings.Global.getInt(context.getContentResolver(), "navigationbar_is_min", 0) != 0;
    }

    /**
     * 小米手机是否开启手势操作
     * @return false 表示使用的是虚拟导航键(NavigationBar)， true 表示使用的是手势， 默认是false
     */
    public static boolean isMiuiFullScreen(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "force_fsg_nav_bar", 0) != 0;
    }

    /**
     * Vivo手机是否开启手势操作
     * @return false 表示使用的是虚拟导航键(NavigationBar)， true 表示使用的是手势， 默认是false
     */
    public static boolean isVivoFullScreen(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "navigation_gesture_on", 0) != 0;
    }


    /**
     * 其他手机根据屏幕真实高度与显示高度是否相同来判断
     */
    public static boolean isHasNavigationBar(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(realDisplayMetrics);
        }
        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);
        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        // 部分无良厂商的手势操作，显示高度 + 导航栏高度，竟然大于物理高度，对于这种情况，直接默认未启用导航栏
        if (displayHeight + getNavigationBarHeight(context) > realHeight) {
            return false;
        }

        return realWidth - displayWidth > 0 || realHeight - displayHeight > 0;
    }
}
