package com.ilpanda.floatwindow.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.ilpanda.floatwindow.manager.LifeRecycleManager;
import com.ilpanda.floatwindow.ui.VideoPlayActivity;

public class FloatViewManager implements FloatVideoView.FloatViewListener, LifeRecycleManager.StateListener {

    private static final String TAG = "FloatViewManager";

    private static class Holder {
        private static FloatViewManager INSTANCE = new FloatViewManager();
    }

    private FloatViewManager() {

    }

    public static FloatViewManager getInstance() {
        return Holder.INSTANCE;
    }

    private boolean mInit = false;


    private boolean showOnDesktop = true;

    public void setShowOnDesktop(boolean showOnDesktop) {
        this.showOnDesktop = showOnDesktop;
    }

    public void init(Context context) {

        if ((mInit)) {
            return;
        }

        mInit = true;

        Context applicationContext = context.getApplicationContext();
        FloatVideoView floatView = new FloatVideoView(applicationContext);
        floatView.setVisibility(View.GONE); // 默认隐藏悬浮窗
        floatView.setListener(this);

        // 因为该悬浮窗主要用于播放横屏视频,设置默认宽度为屏幕宽度的 60%
        // 将比例调整为 宽:高 = 16:9
        int width = (int) (ScreenUtil.getScreenWidth(applicationContext) * 0.6);
        int height = width * 9 / 16;

        FloatWindow.with(applicationContext)
                .view(floatView)
                .width(width)   // 悬浮窗宽度
                .height(height) // 悬浮窗高度
                .tag(TAG)
                .viewClickListener(floatView) // 为 floatView 设置了 GestureListener,监听触摸事件,处理单击和双击事件.
                .x(ScreenUtil.dipToPx(applicationContext, 6)) // 悬浮窗起始位置 x 方向偏移。
                .y(0) // 悬浮窗起始位置 y 方向偏移，0 是从状态栏之下开始。
                .margin(ScreenUtil.dipToPx(applicationContext, 6),
                        ScreenUtil.dipToPx(applicationContext, 6),
                        0,
                        ScreenUtil.dipToPx(applicationContext, 44))
                .build();
    }

    /**
     * 显示悬浮窗
     */
    public void show() {
        IFloatWindow iFloatWindow = FloatWindow.get(TAG);
        if (iFloatWindow == null) return;
        iFloatWindow.show();
    }

    /**
     * 当前是否显示悬浮窗
     *
     * @return
     */
    public boolean isShowing() {
        IFloatWindow iFloatWindow = FloatWindow.get(TAG);
        if (iFloatWindow == null) return false;
        return iFloatWindow.isShowing();
    }


    /**
     * 隐藏悬浮窗,一般用于以下情况:
     * 用户主动关闭
     * 用户切换到桌面后
     * 用户进入到视频直播页面(因为已经有视频在播放了,不需要悬浮窗)
     */
    public void hide() {
        IFloatWindow iFloatWindow = FloatWindow.get(TAG);
        if (iFloatWindow == null) return;
        iFloatWindow.hide();
    }


    /**
     * 点击开启/关闭静音.
     *
     * @param mute
     */
    @Override
    public void onClickMute(boolean mute) {
        Activity topActivity = LifeRecycleManager.getInstance().getTopActivity();
        if (topActivity != null) {
            Toast.makeText(topActivity, mute ? "开启静音" : "关闭静音", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 点击右上角的关闭
     */
    @Override
    public void onClickClose() {
        Activity topActivity = LifeRecycleManager.getInstance().getTopActivity();
        if (topActivity != null) {
            Toast.makeText(topActivity, "单击关闭", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 点击全屏
     */
    @Override
    public void onClickFullScreen() {
        Activity topActivity = LifeRecycleManager.getInstance().getTopActivity();
        if (topActivity != null) {
            Toast.makeText(topActivity, "点击全屏", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 单击悬浮窗
     */
    @Override
    public void onClickFloatWindow() {
        Activity topActivity = LifeRecycleManager.getInstance().getTopActivity();
        if (topActivity != null) {
            Intent intent = new Intent(topActivity, VideoPlayActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            topActivity.startActivity(intent);
        }
    }

    /**
     * 双击悬浮窗
     */
    @Override
    public void onDoubleClickFloatWindow() {
        Activity topActivity = LifeRecycleManager.getInstance().getTopActivity();
        IFloatWindow iFloatWindow = FloatWindow.get(TAG);
        if (iFloatWindow == null) return;

        if (topActivity != null) {
            Toast.makeText(topActivity, "双击屏幕", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 用户点击 Home 键进入后台
     */
    @Override
    public void onBackground() {
        if (!showOnDesktop) {
            hide();
        }
    }

    /**
     * 用户切换到前台
     */
    @Override
    public void onForeground() {
        if (!showOnDesktop) {
            show();
        }
    }

    public void addViewStateListener(ViewStateListener listener) {
        IFloatWindow iFloatWindow = FloatWindow.get(TAG);
        if (iFloatWindow == null) return;

        iFloatWindow.addViewStateListener(listener);
    }

    public void removeViewStateListener(ViewStateListener listener) {
        IFloatWindow iFloatWindow = FloatWindow.get(TAG);
        if (iFloatWindow == null) return;
        iFloatWindow.removeViewStateListener(listener);
    }

}
