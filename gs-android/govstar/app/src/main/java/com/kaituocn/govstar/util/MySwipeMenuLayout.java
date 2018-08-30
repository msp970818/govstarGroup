package com.kaituocn.govstar.util;

import android.content.Context;
import android.util.AttributeSet;

import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

public class MySwipeMenuLayout extends SwipeMenuLayout {
    OnSwipeMenuListener listener;
    public MySwipeMenuLayout(Context context) {
        super(context);
    }

    public MySwipeMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySwipeMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void smoothClose() {
        super.smoothClose();
        if (listener != null) {
            listener.onClose();
        }
    }


    public void setListener(OnSwipeMenuListener listener) {
        this.listener = listener;
    }

    public interface OnSwipeMenuListener{
            void onClose();
    }
}
