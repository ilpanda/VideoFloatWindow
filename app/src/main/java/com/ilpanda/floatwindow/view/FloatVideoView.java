package com.ilpanda.floatwindow.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ilpanda.floatwindow.R;


public class FloatVideoView extends RelativeLayout implements View.OnClickListener, FloatViewClickListener {

    private static final String TAG = "LiveFloatView";

    private Context mContext;

    private FloatViewListener mListener;

    private ImageView mIvMute;   // 是否静音图片
    private boolean mMute = false; // 是否静音

    public FloatVideoView(Context context) {
        super(context);
        init(context);
    }

    public FloatVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FloatVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    public void setListener(FloatViewListener mListener) {
        this.mListener = mListener;
    }


    private void init(Context context) {
        this.mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_float_palyer, this, true);
        ImageView ivClose = view.findViewById(R.id.iv_close);
        mIvMute = view.findViewById(R.id.iv_mute);
        ImageView ivFullScreen = view.findViewById(R.id.iv_fullscreen);

        ivClose.setOnClickListener(this);
        mIvMute.setOnClickListener(this);
        ivFullScreen.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (mListener == null) {
            return;
        }

        int id = v.getId();

        if (id == R.id.iv_close) {
            mListener.onClickClose();
        } else if (id == R.id.iv_mute) {
            mMute = !mMute;
            mIvMute.setImageResource(mMute ? R.drawable.img_mute : R.drawable.img_volume);
            mListener.onClickMute(mMute);
        } else if (id == R.id.iv_fullscreen) {
            mListener.onClickFullScreen();
        }
    }

    @Override
    public void onSingleTapFloatWindow() {
        if (mListener == null) {
            return;
        }
        mListener.onClickFloatWindow();
    }

    @Override
    public void onDoubleTapFloatWindow() {
        if (mListener == null) {
            return;
        }
        mListener.onDoubleClickFloatWindow();
    }

    public interface FloatViewListener {

        /**
         * 静音
         *
         * @param mute
         */
        void onClickMute(boolean mute);

        /**
         * 右上角的关闭
         */
        void onClickClose();

        /**
         * 全屏显示
         */
        void onClickFullScreen();

        /**
         * 单击悬浮窗
         */
        void onClickFloatWindow();

        /**
         * 双击悬浮窗
         */
        void onDoubleClickFloatWindow();

    }

}
