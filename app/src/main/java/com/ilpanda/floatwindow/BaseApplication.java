package com.ilpanda.floatwindow;

import android.app.Application;

import com.ilpanda.floatwindow.manager.FloatViewManager;
import com.ilpanda.floatwindow.manager.LifeRecycleManager;

public class BaseApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        LifeRecycleManager.getInstance().init(this);
        LifeRecycleManager.getInstance().register(FloatViewManager.getInstance());
    }
}
