package com.ilpanda.floatwindow.view;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.LayoutRes;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by yhao on 2017/12/22.
 * https://github.com/yhaolpz
 */

public class FloatWindow {


    private static final String mDefaultTag = "default_float_window_tag";

    private static Map<String, IFloatWindow> mFloatWindowMap;

    private static Builder mBuilder = null;

    private FloatWindow() {

    }

    public static IFloatWindow get() {
        return get(mDefaultTag);
    }

    public static IFloatWindow get(@NonNull String tag) {
        return mFloatWindowMap == null ? null : mFloatWindowMap.get(tag);
    }


    @MainThread
    public static Builder with(@NonNull Context applicationContext) {
        return mBuilder = new Builder(applicationContext);
    }

    public static void destroy() {
        destroy(mDefaultTag);
    }

    public static void destroy(String tag) {
        if (mFloatWindowMap == null || !mFloatWindowMap.containsKey(tag)) {
            return;
        }
        mFloatWindowMap.get(tag).dismiss();
        mFloatWindowMap.remove(tag);
    }

    public static class Builder {
        Context mApplicationContext;
        View mView;
        private int mLayoutId;
        int mWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
        int mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        int gravity = Gravity.TOP | Gravity.START;
        int xOffset;
        int yOffset;
        boolean mShow = true;
        Class[] mActivities;
        int mSlideLeftMargin;
        int mSlideRightMargin;
        int mSlideTopMargin;
        int mSlideBottomMargin;
        long mDuration = 300;
        TimeInterpolator mInterpolator;
        private String mTag = mDefaultTag;
        ViewStateListener mViewStateListener;
        FloatViewClickListener mViewClickListener;

        private Builder() {

        }

        Builder(Context applicationContext) {
            mApplicationContext = applicationContext;
        }

        public Builder setView(@NonNull View view) {
            mView = view;
            return this;
        }

        public Builder setView(@LayoutRes int layoutId) {
            mLayoutId = layoutId;
            return this;
        }

        public Builder setWidth(int width) {
            mWidth = width;
            return this;
        }

        public Builder setHeight(int height) {
            mHeight = height;
            return this;
        }

        public Builder setWidth(int width, float ratio) {
            mWidth = (int) (width * ratio);
            return this;
        }


        public Builder setHeight(int height, float ratio) {
            mHeight = (int) (height * ratio);
            return this;
        }


        public Builder setX(int x) {
            xOffset = x;
            return this;
        }

        public Builder setY(int y) {
            yOffset = y;
            return this;
        }

        /**
         * 设置 Activity 过滤器，用于指定在哪些界面显示悬浮窗，默认全部界面都显示
         *
         * @param show       　过滤类型,子类类型也会生效
         * @param activities 　过滤界面
         */
        public Builder setFilter(boolean show, @NonNull Class... activities) {
            mShow = show;
            mActivities = activities;
            return this;
        }


        /**
         * @param slideLeftMargin
         * @param slideRightMargin
         * @param slideTopMargin
         * @param slideBottomMargin
         * @return
         */
        public Builder setMargin(
                int slideLeftMargin,
                int slideRightMargin,
                int slideTopMargin,
                int slideBottomMargin) {
            mSlideLeftMargin = slideLeftMargin;
            mSlideRightMargin = slideRightMargin;
            mSlideTopMargin = slideTopMargin;
            mSlideBottomMargin = slideBottomMargin;
            return this;
        }

        public Builder setViewClickListener(FloatViewClickListener mViewClickListener) {
            this.mViewClickListener = mViewClickListener;
            return this;
        }

        public Builder setMoveStyle(long duration, @Nullable TimeInterpolator interpolator) {
            mDuration = duration;
            mInterpolator = interpolator;
            return this;
        }

        public Builder setTag(@NonNull String tag) {
            mTag = tag;
            return this;
        }

        public Builder setViewStateListener(ViewStateListener listener) {
            mViewStateListener = listener;
            return this;
        }

        public void build() {
            if (mFloatWindowMap == null) {
                mFloatWindowMap = new HashMap<>();
            }
            if (mFloatWindowMap.containsKey(mTag)) {
                throw new IllegalArgumentException("FloatWindow of this tag has been added, Please set a new tag for the new FloatWindow");
            }
            if (mView == null && mLayoutId == 0) {
                throw new IllegalArgumentException("View has not been set!");
            }
            if (mView == null) {
                LayoutInflater inflate = (LayoutInflater) mApplicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mView = inflate.inflate(mLayoutId, null);
            }
            IFloatWindow floatWindowImpl = new IFloatWindowImpl(this);
            mFloatWindowMap.put(mTag, floatWindowImpl);
        }
    }

}
