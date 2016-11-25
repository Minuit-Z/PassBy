package utils;

import android.app.Application;
import android.os.Build;

import com.baidu.mapapi.SDKInitializer;

import cn.bmob.v3.Bmob;

/**
 * Created by 德帅 on 2016/8/27.
 * action:
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        SDKInitializer.initialize(MyApplication.this);

        //第一：默认初始化
        Bmob.initialize(MyApplication.this, "2d73eaaef57c8a716217a4759728beed");
//      SDKInitializer.initialize(MyApplication.this);

    }
}
