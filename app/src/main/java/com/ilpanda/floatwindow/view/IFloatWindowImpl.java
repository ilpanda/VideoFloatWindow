package com.ilpanda.floatwindow.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;

import com.ilpanda.floatwindow.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by yhao on 2017/12/22.
 * https://github.com/yhaolpz
 */

public class IFloatWindowImpl extends IFloatWindow {


    private static final String TAG = "IFloatWindowImpl";

    private FloatWindow.Builder mBuilder;
    private FloatView mFloatView;
    private boolean isShow;
    private boolean once = true;
    private ValueAnimator mAnimator;
    private TimeInterpolator mDecelerateInterpolator;
    private float downX;
    private float downY;
    private float upX;
    private float upY;
    private boolean mClick = false;
    private int mSlop;

    private List<ViewStateListener> mViewStateListeners = new ArrayList<>();

    private IFloatWindowImpl() {

    }

    IFloatWindowImpl(FloatWindow.Builder builder) {
        mBuilder = builder;

        if (builder.mViewStateListener != null) {
            mViewStateListeners.add(builder.mViewStateListener);
        }

        mFloatView = new FloatPhone(builder.mApplicationContext);
        initTouchEvent();

        mFloatView.setSize(mBuilder.mWidth, mBuilder.mHeight);
        mFloatView.setGravity(mBuilder.gravity, mBuilder.xOffset, mBuilder.yOffset);
        mFloatView.setView(mBuilder.mView);
        mFloatView.init();
    }

    @Override
    public void show() {

        if (isShow) {
            return;
        }
        getView().setVisibility(View.VISIBLE);
        isShow = true;

        for (ViewStateListener listener : mViewStateListeners) {
            listener.onShow();
        }
    }

    @Override
    public void hide() {
        if (!isShow) {
            return;
        }
        getView().setVisibility(View.GONE);
        isShow = false;

        for (ViewStateListener listener : mViewStateListeners) {
            listener.onHide();
        }
    }

    @Override
    public boolean isShowing() {
        return isShow;
    }

    @Override
    void dismiss() {
        mFloatView.dismiss();
        isShow = false;
        for (ViewStateListener listener : mViewStateListeners) {
            listener.onDismiss();
        }
    }

    @Override
    void addViewStateListener(ViewStateListener listener) {
        mViewStateListeners.add(listener);
    }

    @Override
    void removeViewStateListener(ViewStateListener listener) {
        mViewStateListeners.remove(listener);
    }

    @Override
    public void updateX(int x) {
        mBuilder.xOffset = x;
        mFloatView.updateX(x);
    }

    @Override
    public void updateY(int y) {
        mBuilder.yOffset = y;
        mFloatView.updateY(y);
    }

    @Override
    public int getX() {
        return mFloatView.getX();
    }

    @Override
    public int getY() {
        return mFloatView.getY();
    }


    @Override
    public View getView() {
        mSlop = ViewConfiguration.get(mBuilder.mApplicationContext).getScaledTouchSlop();
        return mBuilder.mView;
    }

    private void startAnimator(final boolean hideOnAnimatorEnd) {
        if (mBuilder.mInterpolator == null) {
            if (mDecelerateInterpolator == null) {
                mDecelerateInterpolator = new DecelerateInterpolator();
            }
            mBuilder.mInterpolator = mDecelerateInterpolator;
        }
        mAnimator.setInterpolator(mBuilder.mInterpolator);
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimator.removeAllUpdateListeners();
                mAnimator.removeAllListeners();
                mAnimator = null;
                if (hideOnAnimatorEnd) {
                    FloatViewManager.getInstance().hide();
                    // 隐藏之后对悬浮窗进行复位，便于下次显示。
                    mFloatView.updateX(mBuilder.mSlideLeftMargin);
                }
                for (ViewStateListener listener : mViewStateListeners) {
                    listener.onMoveAnimEnd();
                }
            }
        });
        mAnimator.setDuration(mBuilder.mDuration).start();
        for (ViewStateListener listener : mViewStateListeners) {
            listener.onMoveAnimStart();
        }
    }

    private void cancelAnimator() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
    }

    private void moveBack(View view) {
        int startX = mFloatView.getX();

        int endX = 0;
        boolean hideOnAnimatorEnd = false;
        if (startX <= -(view.getWidth() / 2)) {
            hideOnAnimatorEnd = true;
            endX = -view.getWidth();
        } else if (startX < mBuilder.mSlideLeftMargin) {
            endX = mBuilder.mSlideLeftMargin;
        } else if (startX + view.getWidth() > ScreenUtil.getScreenWidth(mBuilder.mApplicationContext) - mBuilder.mSlideRightMargin) {
            endX = ScreenUtil.getScreenWidth(mBuilder.mApplicationContext) - mBuilder.mSlideRightMargin - view.getWidth();
        }
        if (endX != 0) {
            mAnimator = ObjectAnimator.ofInt(startX, endX);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int x = (int) animation.getAnimatedValue();
                    mFloatView.updateX(x);
                    for (ViewStateListener listener : mViewStateListeners) {
                        listener.onPositionUpdate(x, (int) upY);
                    }
                }
            });
            startAnimator(hideOnAnimatorEnd);
        }
    }


    private void initTouchEvent() {

        final MyGestureDetector gestureDetector = new MyGestureDetector(mBuilder.mApplicationContext, new GestureDetector.SimpleOnGestureListener() {

            private int mLastX;
            private int mLastY;

            @Override
            public boolean onDown(MotionEvent e) {
                mLastX = (int) e.getRawX();
                mLastY = (int) e.getRawY();
                cancelAnimator();
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (mBuilder.mViewClickListener != null) {
                    mBuilder.mViewClickListener.onDoubleTapFloatWindow();
                    scale();
                    return true;
                }
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                int rawX = (int) e2.getRawX();
                int rawY = (int) e2.getRawY();

                int changeX = rawX - mLastX;
                int changeY = rawY - mLastY;

                int newX = mFloatView.getX() + changeX;
                int newY = mFloatView.getY() + changeY;

                newY = Math.min(Math.max(newY, mBuilder.mSlideTopMargin), ScreenUtil.getScreenHeight(mBuilder.mApplicationContext) - mBuilder.mSlideBottomMargin - mBuilder.mView.getHeight());

                mFloatView.updateXY(newX, newY);

                for (ViewStateListener listener : mViewStateListeners) {
                    listener.onPositionUpdate(newX, newY);
                }

                mLastX = rawX;
                mLastY = rawY;
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (mBuilder.mViewClickListener != null) {
                    mBuilder.mViewClickListener.onSingleTapFloatWindow();
                    return true;
                }
                return super.onSingleTapConfirmed(e);
            }
        });

        getView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    gestureDetector.onActionUp(v, event);
                }
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    private class MyGestureDetector extends GestureDetector {
        public MyGestureDetector(Context context, OnGestureListener listener) {
            super(context, listener);
        }

        public MyGestureDetector(Context context, OnGestureListener listener, Handler handler) {
            super(context, listener, handler);
        }

        public MyGestureDetector(Context context, OnGestureListener listener, Handler handler, boolean unused) {
            super(context, listener, handler, unused);
        }

        public void onActionUp(View v, MotionEvent event) {
            upX = event.getRawX();
            upY = event.getRawY();
            mBuilder.mView.post(new Runnable() {
                @Override
                public void run() {
                    moveBack(mBuilder.mView);
                }
            });
        }
    }

    public enum ScaleType {
        MIN,
        MEDIUM,
        MAX
    }

    private ScaleType currentScaleType = ScaleType.MIN;


    public void scale() {

        ScaleType newScale = currentScaleType;
        int newWidth = 0;
        int newHeight = 0;

        int initWidth = mBuilder.mWidth;
        int initHeight = mBuilder.mHeight;
        if (currentScaleType == ScaleType.MIN) {
            newScale = ScaleType.MEDIUM;
            newWidth = (int) (initWidth * 1.37);
            newHeight = (int) (initHeight * 1.37);
        } else if (currentScaleType == ScaleType.MEDIUM) {
            newScale = ScaleType.MAX;
            newWidth = (int) (initWidth * 1.67);
            newHeight = (int) (initHeight * 1.67);
        } else if (currentScaleType == ScaleType.MAX) {
            newScale = ScaleType.MIN;
            newWidth = initWidth;
            newHeight = initHeight;
        }

        int maxWidth = ScreenUtil.getScreenWidth(mBuilder.mApplicationContext) - mBuilder.mSlideLeftMargin - mBuilder.mSlideRightMargin;

        newWidth = Math.min(maxWidth, newWidth);

        currentScaleType = newScale;
        mFloatView.updateSize(newWidth, newHeight);
    }

}
