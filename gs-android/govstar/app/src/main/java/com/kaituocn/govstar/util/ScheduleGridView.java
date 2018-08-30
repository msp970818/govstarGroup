package com.kaituocn.govstar.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.kaituocn.govstar.R;

public class ScheduleGridView extends View {

    private Paint paint=new Paint();
    private float[] points;

    public ScheduleGridView(Context context) {
        super(context);
    }

    public ScheduleGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScheduleGridView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ScheduleGridView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(){
        paint.setColor(getResources().getColor(R.color.list_divider));
        paint.setAntiAlias(true);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (points == null) {
            paint.setStrokeWidth(Util.dp2px(getContext(),1));
            points=new float[24*4];
            int height=2000;
//            int height=canvas.getHeight();
            int step=Util.dp2px(getContext(),60);
            int index=0;
            for (int i = 0; i <24 ; i++) {
                    points[index++]=i*step;
                    points[index++]=0;
                    points[index++]=i*step;
                    points[index++]=height;
            }
//            System.out.println("============ w "+getWidth()+"   h "+getHeight());
        }
        canvas.drawLines(points,paint);
    }
}
