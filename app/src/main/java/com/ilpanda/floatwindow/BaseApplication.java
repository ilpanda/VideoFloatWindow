package com.ilpanda.floatwindow;

import android.app.Application;

import com.ilpanda.floatwindow.manager.LifeRecycleManager;
import com.ilpanda.floatwindow.view.FloatViewManager;

public class BaseApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        LifeRecycleManager.getInstance().init(this);
        LifeRecycleManager.getInstance().register(FloatViewManager.getInstance());
    }
}
