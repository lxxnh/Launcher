package com.android.launcher3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zhongwenguang on 2/8/17.
 */

public class PrivateDropTarget extends ButtonDropTarget {

    public PrivateDropTarget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PrivateDropTarget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mHoverColor = getResources().getColor(R.color.info_target_hover_tint);
        //TODO 需要替换图片
        setDrawable(R.drawable.ic_info_launcher);
    }
    @Override
    protected boolean supportsDrop(DragSource source, Object info) {
        return true;
    }

    @Override
    void completeDrop(DragObject d) {
        showConfirmDialog();
    }

    public void showConfirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mLauncher);
        builder.setTitle(R.string.private_confirm_title);
        builder.setMessage(R.string.private_confirm_msg);
        builder.setPositiveButton(R.string.private_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //TODO 设置隐私文件
                Intent intent = new Intent(mLauncher,PasswordActivity.class);
                mLauncher.startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.private_confirm_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }
}
