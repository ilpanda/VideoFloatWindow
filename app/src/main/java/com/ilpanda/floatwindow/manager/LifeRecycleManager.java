package com.ilpanda.floatwindow.manager;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class LifeRecycleManager implements Application.ActivityLifecycleCallbacks {


    private static final String TAG = "LifeStyleManager";

    private List<StateListener> mStateListeners = new ArrayList<>();

    private LifeRecycleManager() {

    }

    public void init(Context context) {
        ((Application) context.getApplicationContext()).registerActivityLifecycleCallbacks(this);
    }


    private static class HOLDER {
        static LifeRecycleManager INSTANCE = new LifeRecycleManager();
    }

    public static LifeRecycleManager getInstance() {
        return HOLDER.INSTANCE;
    }

    private int mCount = 0;

    private ArrayList<Activity> activities = new ArrayList<>();


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        activities.add(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        this.mCount++;
        if (this.mCount == 1) {
            for (StateListener stateListener : mStateListeners) {
                stateListener.onForeground();
            }
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        this.mCount--;
        if (this.mCount == 0) {
            for (StateListener stateListener : mStateListeners) {
                stateListener.onBackground();
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        activities.remove(activity);
    }

    public boolean isBackground() {
        return mCount == 0;
    }

    public boolean isForeground() {
        return mCount != 0;
    }

    public Activity getTopActivity() {
        if (activities.size() != 0) {
            return activities.get(activities.size() - 1);
        }
        return null;
    }

    public interface StateListener {
        void onBackground();

        void onForeground();
    }

    public void register(StateListener stateListener) {
        this.mStateListeners.add(stateListener);
    }

    public void unregister(StateListener stateListener) {
        this.mStateListeners.remove(stateListener);
    }


}
