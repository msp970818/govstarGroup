package com.kaituocn.govsafety.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class CircleView extends View {
    Paint bgPaint;
    RectF rect;
    Paint paint;
    int progress;
    int color_gray=0xFFDDEBF4;
    int color_blue=0xFF0087F3;
    int color_red=0xFFCF2013;

    public CircleView(Context context) {
        super(context, null);
        initPaint();
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }


    private void initPaint() {
        bgPaint=new Paint();
        bgPaint.setColor(color_gray);
        bgPaint.setAntiAlias(true);
        paint = new Paint();
        paint.setColor(color_blue);
        paint.setAntiAlias(true);
        progress=600;
    }

    public void setColor(int color) {
        paint.setColor(color);
    }

    public void setProgress(int progress) {
        this.progress = progress;
        if(progress<160){
            paint.setColor(color_red);
        }else{
            paint.setColor(color_blue);

        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (rect == null) {
            rect = new RectF(0, 0, getWidth(), getHeight());
        }
        canvas.drawCircle(getWidth()/2, getHeight()/2,getWidth()/2,bgPaint);
        canvas.drawArc(rect, 270, -progress*360/600, true, paint);
    }
}
