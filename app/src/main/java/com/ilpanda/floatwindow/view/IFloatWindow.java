package com.ilpanda.floatwindow.view;

import android.view.View;

/**
 * Created by yhao on 2017/12/22.
 * https://github.com/yhaolpz
 */

public abstract class IFloatWindow {
    public abstract void show();

    public abstract void hide();

    public abstract boolean isShowing();

    public abstract int getX();

    public abstract int getY();

    public abstract void updateX(int x);

    public abstract void updateY(int y);

    public abstract void scale();

    public abstract View getView();

    abstract void dismiss();

    abstract void addViewStateListener(ViewStateListener viewStateListener);

    abstract void removeViewStateListener(ViewStateListener viewStateListener);
}
