package com.ilpanda.floatwindow.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ilpanda.floatwindow.R;
import com.ilpanda.floatwindow.view.FloatViewManager;
import com.ilpanda.floatwindow.view.FloatWindowPermissionUtil;
import com.ilpanda.floatwindow.view.ViewStateListener;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewStateListener {


    private static final String TAG = "MainActivity";

    private TextView tvPermission;
    private TextView tvShow;
    private TextView tvDesktopShow;

    private static final int FLOAT_WINDOW_REQUEST_CODE = 1000;

    private boolean hasPermission;
    private boolean showOnDesktop = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floatwindow);

        initView();

        checkFloatWindowPermission();
    }

    private boolean checkFloatWindowPermission() {
        if (FloatWindowPermissionUtil.hasPermission(this)) {
            hasPermission = true;
            tvPermission.setText("已经获取悬浮窗权限");
            FloatViewManager.getInstance().init(this);
            FloatViewManager.getInstance().addViewStateListener(this);
            FloatViewManager.getInstance().show();
        } else {
            hasPermission = false;
            tvPermission.setText("点击获取悬浮窗权限");
        }
        return hasPermission;
    }


    private void initView() {
        tvPermission = findViewById(R.id.tv_permission);
        tvShow = findViewById(R.id.tv_show);
        tvDesktopShow = findViewById(R.id.tv_desktop_show);

        tvPermission.setOnClickListener(this);
        tvShow.setOnClickListener(this);
        tvDesktopShow.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FLOAT_WINDOW_REQUEST_CODE) {
            if (checkFloatWindowPermission()) {
                tvShow.setText("点击隐藏");
                FloatViewManager.getInstance().show();
            }
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_permission) {
            if (!hasPermission) {
                FloatWindowPermissionUtil.request(this, FLOAT_WINDOW_REQUEST_CODE);
            }
        } else if (id == R.id.tv_show) {
            if (FloatWindowPermissionUtil.hasPermissionOnActivityResult(this)) {
                if (FloatViewManager.getInstance().isShowing()) {
                    FloatViewManager.getInstance().hide();
                } else {
                    FloatViewManager.getInstance().show();
                }
            } else {
                Toast.makeText(this, "请先授予悬浮窗权限", Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.tv_desktop_show) {
            showOnDesktop = !showOnDesktop;
            FloatViewManager.getInstance().setShowOnDesktop(showOnDesktop);
            tvDesktopShow.setText(showOnDesktop ? "进入桌面显示悬浮窗" : "进入桌面隐藏悬浮窗");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FloatViewManager.getInstance().removeViewStateListener(this);
    }

    @Override
    public void onPositionUpdate(int x, int y) {
        Log.i(TAG, "x : " + x + " y : " + y);
    }

    @Override
    public void onShow() {
        tvShow.setText("点击隐藏");
        Log.i(TAG, "onShow");
    }

    @Override
    public void onHide() {
        tvShow.setText("点击显示");
        Log.i(TAG, "onHide");
    }

    @Override
    public void onDismiss() {

    }

    @Override
    public void onMoveAnimStart() {
        Log.i(TAG, "onMoveAnimStart");
    }

    @Override
    public void onMoveAnimEnd() {
        Log.i(TAG, "onMoveAnimEnd");
    }
}
