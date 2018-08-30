package com.kaituocn.govsafety.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    public static boolean DEBUG = false;

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

    public static void setTransparentStatusBar(Window window) {
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    public static void showToast(Context c, String s) {
        Toast t = Toast.makeText(c, s, Toast.LENGTH_SHORT);
        t.setText(s);
        t.setGravity(Gravity.CENTER, 0, 0);
        t.show();
    }

    public static boolean checkPhoneNum(String phoneNum) {
        Pattern pattern = Pattern.compile("[1][345678]\\d{9}");
        Matcher matcher = pattern.matcher(phoneNum);
        return matcher.matches();
    }

    public static void setBackgroundTint(View view, int colorId) {
        view.setBackgroundTintList(new ColorStateList(new int[][]{{}}, new int[]{view.getContext().getResources().getColor(colorId)}));
    }

    public static AlertDialog showDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setView(view);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        DisplayMetrics d = view.getContext().getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
        window.setAttributes(lp);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    public static String getSerialNumber(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
        if (tm != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                if (Build.SERIAL.equals(Build.UNKNOWN)) {
                    return Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                } else {
                    return Build.SERIAL;
                }
            }
            return tm.getDeviceId();

        }else{
            if (Build.SERIAL.equals(Build.UNKNOWN)) {
                return Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            } else {
                return Build.SERIAL;
            }
        }
    }

    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

}
