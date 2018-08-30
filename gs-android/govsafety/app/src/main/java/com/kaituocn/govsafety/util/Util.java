package com.kaituocn.govsafety.util;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class Util {
    public static void ShowToast(Context c, String s) {
        Toast t = Toast.makeText(c, s, Toast.LENGTH_SHORT);
        t.setText(s);
        t.setGravity(Gravity.CENTER, 0, 0);
        t.show();
    }

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static void setTransparentStatusBar(Window window){
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    public static AlertDialog showDialog(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setView(view);
        builder.setCancelable(false);
        AlertDialog dialog=builder.create();
        dialog.show();

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        DisplayMetrics d = view.getContext().getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
        window.setAttributes(lp);

        return dialog;
    }
}
