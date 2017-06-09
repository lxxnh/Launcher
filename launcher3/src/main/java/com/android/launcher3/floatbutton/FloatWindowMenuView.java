package com.android.launcher3.floatbutton;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.launcher3.R;

/**
 * Created by lxx on 6/5/17.
 */

public class FloatWindowMenuView extends LinearLayout {

    //记录大悬浮窗的宽度
    public static int viewWidth;

    //记录大悬浮窗的高度
    public static int viewHeight;

    private LinearLayout mCircleMenuLayout;
    private RelativeLayout mCloseView;

    public FloatWindowMenuView(final Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.float_window_menu, this);
        View view = findViewById(R.id.big_window_layout);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;

        mCircleMenuLayout = (LinearLayout) findViewById(R.id.id_menu_layout);
        mCircleMenuLayout.setFocusable(false);
        mCloseView = (RelativeLayout) findViewById(R.id.back);
        mCloseView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "test", Toast.LENGTH_LONG).show();
                FloatWindowManager.removeBigWindow(context);
                FloatWindowManager.createSmallWindow(context);
            }
        });

    }

}
