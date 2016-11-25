package utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 德帅 on 2016/7/27.
 * action:
 */
public class SpUtil {
    /**
     * 向sp中添加boolean数据
     * @time 2016/7/27 17:09
     * @author ziye
     */
    public static void setBoolean(String key, Boolean value, Context context) {

        SharedPreferences sp = context.getSharedPreferences(Constants.CONFIG_FILE, Context.MODE_PRIVATE);
        sp.edit().putBoolean(Constants.ISSETUP, value).commit();//提交保存
    }

    /**
     * 从sp中获取boolean数据
     *@time 2016/7/27 17:17
     *@author ziye
     */
    public static boolean getBoolean(Context context,String key,boolean defaultValue){
        SharedPreferences sp = context.getSharedPreferences(Constants.CONFIG_FILE, Context.MODE_PRIVATE);
        return sp.getBoolean(key,defaultValue);
    }

    /**
     *想sp中写入username信息
     *@time 2016/8/17 16:17
     *@author ziye
     */
    public static void setUserNamefromSP( String name, Context context) {

        SharedPreferences sp = context.getSharedPreferences("user info", Context.MODE_PRIVATE);
        sp.edit().putString("UserName", name).commit();//提交保存
    }

    /**
     * 从sps中获取username信息
     *@time 2016/8/17 16:18
     *@author ziye
     */
    public static String getUserNamefromSP(Context context,String defaultValue){
        SharedPreferences sp = context.getSharedPreferences("user info", Context.MODE_PRIVATE);
        return sp.getString("UserName",defaultValue);
    }

    /**
     * 获取主题信息
     *@time 2016/9/7 18:58
     *@author ziye
     * code:1 默认;2 武乡;3 太原
     */
    public static int getStyleCode(Context context){
        SharedPreferences sp=context.getSharedPreferences("style code",Context.MODE_PRIVATE);
        return sp.getInt("style",1);
    }

    public static void setStyleCode(Context context,int code){
        SharedPreferences sp=context.getSharedPreferences("style code",Context.MODE_PRIVATE);
        sp.edit().putInt("style",code).commit();
    }
}
