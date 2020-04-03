package com.ilpanda.floatwindow.view;

/**
 * Created by yhao on 2018/5/5
 * https://github.com/yhaolpz
 */
public interface ViewStateListener {

    /**
     * 显示时调用
     */
    void onShow();

    /**
     * 隐藏时调用
     */
    void onHide();


    void onDismiss();


    /**
     * 手指移动悬浮窗时调用。
     *
     * @param x
     * @param y
     */
    void onPositionUpdate(int x, int y);

    /**
     * 手指松开后，如果悬浮窗超出了边界，会有一个回到边界的动画。
     * 该方法在开始回到边界时调用。
     */
    void onMoveAnimStart();

    /**
     * 手指松开后，如果悬浮窗超出了边界，会有一个回到边界的动画。
     * 该方法在到达到边界时调用。
     */
    void onMoveAnimEnd();
}
