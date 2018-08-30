package com.kaituocn.govstar.util;

import android.content.Context;
import android.util.AttributeSet;

import com.contrarywind.view.WheelView;

import java.lang.reflect.Field;

public class MyWheelView extends WheelView {


    public MyWheelView(Context context) {
        super(context);
        modifyItemsVisible();
    }

    public MyWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        modifyItemsVisible();
    }

    private void modifyItemsVisible(){
        Field privateField = null;
        try {
            privateField = WheelView.class.getDeclaredField("itemsVisible");
        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
        }
        privateField.setAccessible(true);
        try {
            privateField.setInt(this,7);
        } catch (IllegalAccessException e) {
//            e.printStackTrace();
        }
    }
}
