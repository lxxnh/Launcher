package com.android.launcher3.theme;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.android.launcher3.R;

public class MagicTextView extends View {
    private Paint mTextPaint;//字体画笔
    private float mTextSize;//字体大小
    private String mText;//字体
    private Rect mTextBounds;//字体占用大小
    private int xText;//画字体的起点X
    private int yText;//画字体的起点y
    private int mAlpha;//定义透明度值

    public MagicTextView(Context context) {
        this(context, null);
    }

    public MagicTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MagicTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //从xml定义属性中拿到对应数值
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MagicTextView);
        int indexCount = ta.getIndexCount();
        for (int i = 0; i < indexCount; i++) {
            int index = ta.getIndex(i);
            switch (index) {
                case R.styleable.MagicTextView_text:
                    mText = ta.getString(index);
                    break;
                case R.styleable.MagicTextView_textSize:
                    mTextSize = ta.getDimension(index, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    break;
            }
        }
        Log.d("lxx", "mTextSize=" + mTextSize);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mTextSize);
        mTextBounds = new Rect();
        mTextPaint.getTextBounds(mText, 0, mText.length(), mTextBounds);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        xText = width / 2 - mTextBounds.width() / 2;
        yText = height / 2 + mTextBounds.height() / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        mTextPaint.setColor(0x3c3c3c);
        mTextPaint.setAlpha(255 - mAlpha);
        canvas.drawText(mText, xText, yText, mTextPaint);
        mTextPaint.setColor(0x9C1414);
        mTextPaint.setAlpha(mAlpha);
        canvas.drawText(mText, xText, yText, mTextPaint);


    }

    public void setAlpha(float alpha) {
        mAlpha = (int) Math.ceil(255 * alpha);
        invalidate();
    }

}