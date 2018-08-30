package com.kaituocn.govstar.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

import java.util.HashSet;

public class SyncAllHorizontalScrollView extends HorizontalScrollView {

    private static HashSet<View> observerList = new HashSet<>();
    private static boolean touchDown;

    public SyncAllHorizontalScrollView(Context context) {
        super(context);
    }

    public SyncAllHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        addObserver(this);
    }

    public SyncAllHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addObserver(this);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        for (View view : observerList) {
            if (view!=this) {
                view.scrollTo(l, t);
            }

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction()== MotionEvent.ACTION_DOWN) {
            touchDown=true;
        }else if(ev.getAction()== MotionEvent.ACTION_UP||ev.getAction()== MotionEvent.ACTION_CANCEL||ev.getAction()== MotionEvent.ACTION_OUTSIDE){
            touchDown=false;
        }

        return super.onTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
//        if (touchDown) {
//            for (View view : observerList){
//                view.scrollTo(view.getScrollX(),0);
//            }
//        }else{
//            super.computeScroll();
//        }
        super.computeScroll();
    }

    private static void addObserver(View view){
        observerList.add(view);
    }
}
