package com.lougw.calculate.app;

import android.app.Application;

import com.lougw.calculate.utils.UIUtils;

/**
 * Created by lougw on 18-3-16.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        UIUtils.init(this);
    }


}
