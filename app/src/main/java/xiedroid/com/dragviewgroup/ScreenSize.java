package xiedroid.com.dragviewgroup;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.math.BigDecimal;

/**
 * 单位转换工具类
 *
 * @author xqx
 * @version 1.0
 * @date 2016-4-20.
 */
public class ScreenSize {

    private static final String TAG = ScreenSize.class.getName();

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
//        Loger.d( "Screen density is " + scale + ", dpValue is " + dpValue + ".");
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param context
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        Loger.d( "Screen width : " + wm.getDefaultDisplay().getWidth());
        return wm.getDefaultDisplay().getWidth();
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            return context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
            Rect frame = new Rect();
            ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            int statusBarHeight = frame.top;
            return statusBarHeight;
        }
    }

    /**
     * 获取默认软键盘高度，为屏幕高度乘以0.375取整
     *
     * @return
     */
    public static int getDefaultSoftInputHeight(Context context) {
        return (int) (getScreenHeight(context) * 0.325d);
    }



    public static float calculateMaxScale(Context context, Bitmap bitmap) {
        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();
        BigDecimal currScale = new BigDecimal(imageWidth).divide(new BigDecimal(getScreenWidth(context)), 10, BigDecimal.ROUND_FLOOR);
        BigDecimal currheight = new BigDecimal(imageHeight).divide(currScale, 10, BigDecimal.ROUND_FLOOR);
        BigDecimal maxScale = new BigDecimal(getScreenHeight(context)).divide(currheight, 10, BigDecimal.ROUND_FLOOR);
        return maxScale.floatValue();
    }

    /**
     * 获取虚拟键高度
     *
     * @param context
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
        int navigationBarHeight = 0;
        try {
            Resources rs = context.getResources();
            int id = rs.getIdentifier("navigation_bar_height", "dimen", "android");
            if (id > 0) {
                navigationBarHeight = rs.getDimensionPixelSize(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return navigationBarHeight;
    }

    /**
     * 获取屏幕密度
     * @param context  Activity实例，Activity获取窗体管理器后才能取得屏幕密度
     * @return  屏幕密度值
     */
    public static float getWindowDensity(Activity context){
//        取得屏幕密度
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.density;
    }
}
