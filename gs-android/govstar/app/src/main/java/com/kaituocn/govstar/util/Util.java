package com.kaituocn.govstar.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.MyEntity;
import com.kaituocn.govstar.entity.PersonEntity;
import com.kaituocn.govstar.entity.ScheduleEntity;
import com.kaituocn.govstar.entity.TodoEntity;
import com.kaituocn.govstar.yunav.MyReceiver;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;

public class Util {

    public static void setTransparentStatusBar(Window window) {
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }

    public static void setBackgroundTint(View view, int colorId) {
        view.setBackgroundTintList(new ColorStateList(new int[][]{{}}, new int[]{view.getContext().getResources().getColor(colorId)}));
    }

    public static void setImageTint(ImageView view, int colorId) {
        view.setImageTintList(new ColorStateList(new int[][]{{}}, new int[]{view.getContext().getResources().getColor(colorId)}));
    }

    public static String getCacheDir(Context context) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    public static String getSerialNumber(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
        if (tm != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
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

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static void showToast(Context c, String s) {
        Toast t = Toast.makeText(c, s, Toast.LENGTH_SHORT);
        t.setText(s);
        t.setGravity(Gravity.CENTER, 0, 0);
        t.show();
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

    public static AlertDialog showLoadingDialog(Context context, String info) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_loading, null, false);
        Animation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setRepeatCount(-1);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(1500);
        animation.setFillAfter(true);
        view.findViewById(R.id.loadingView).startAnimation(animation);
        ((TextView) view.findViewById(R.id.infoView)).setText(info == null ? "处理中，请稍后…" : info);

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setView(view);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
//        DisplayMetrics d = view.getContext().getResources().getDisplayMetrics(); // 获取屏幕宽、高用
//        lp.width = d.widthPixels ; // 高度设置为屏幕的
//        lp.height=d.heightPixels;
//        lp.gravity=Gravity.BOTTOM;
//        window.setAttributes(lp);
        window.setBackgroundDrawableResource(android.R.color.transparent);

        return dialog;
    }

    public static String getDateStr(Date date, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    public static String stempToStr(long time, String format) {
        String dateStr;
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        dateStr = sdf.format(time);
        return dateStr;
    }

    //日期字符串转时间戳
    public static long strToStemp(String dateStr, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date d = sdf.parse(dateStr);
            return d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Date strToDate(String dateStr, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date d = sdf.parse(dateStr);
            return d;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }



    public static void sort(List<Object> list, final int type) {
        Collections.sort(list, new Comparator<Object>() {
            /*
             * 返回负数表示：p1 小于p2，
             * 返回0 表示：p1和p2相等，
             * 返回正数表示：p1大于p2
             */
            public int compare(Object p1, Object p2) {

                if (p1 instanceof MyEntity) {
                    if (((MyEntity) p1).getId() == ((MyEntity) p2).getId()) {
                        return 0;
                    }
                    if (type == 1) { //升序
                        if (((MyEntity) p1).getId() > ((MyEntity) p2).getId()) {
                            return 1;
                        }

                    } else if (type == 2) {//降序
                        if (((MyEntity) p2).getId() > ((MyEntity) p1).getId()) {
                            return 1;
                        }
                    }
                    return -1;
                }else if(p1 instanceof TodoEntity){

                    if (type == 1) { //升序
                       return  ((TodoEntity) p2).getCreateTime().compareTo(((TodoEntity) p1).getCreateTime());

                    } else if (type == 2) {//降序
                        return  ((TodoEntity) p1).getCreateTime().compareTo(((TodoEntity) p2).getCreateTime());
                    }
                }else if(p1 instanceof ScheduleEntity){

                    if (type == 1) { //升序
                        return  ((ScheduleEntity) p2).getStartTime().compareTo(((ScheduleEntity) p1).getStartTime());

                    } else if (type == 2) {//降序
                        return  ((ScheduleEntity) p1).getStartTime().compareTo(((ScheduleEntity) p2).getStartTime());
                    }
                }

                return 0;
            }
        });
    }


    public static boolean checkStartTime(Context context,Date date){
        if(date.getTime()<System.currentTimeMillis()-1000*60){
            showToast(context,"开始时间不能小于当前时间");
            return false;
        }
        return true;
    }
    public static String getPersonIds(List<PersonEntity> list) {
        if (list != null&&list.size()>0) {
            StringBuffer sb = new StringBuffer();
            for (PersonEntity personEntity : list) {
                sb.append(personEntity.getId());
                sb.append(",");
            }
            String string = sb.toString();
            return string.substring(0, string.length() - 1);
        }
        return "";
    }

    public static String getFileBaseUrl(Context context){
        if (TextUtils.isEmpty(Constant.FILE_BASE_URL)) {
            Constant.FILE_BASE_URL= SharedPreferencesUtils.getParam(context,SharedPreferencesUtils.KEY_FILE_BASE_URL,"").toString();
            return Constant.FILE_BASE_URL;
        } else{
            return Constant.FILE_BASE_URL;
        }
    }

    public static void hideInput(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public static File getFileByUri(Uri uri, Context context) {
        String path = null;
        if ("file".equals(uri.getScheme())) {
            path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = context.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA}, buff.toString(), null, null);
                int index = 0;
                int dataIdx = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    index = cur.getInt(index);
                    dataIdx = cur.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    path = cur.getString(dataIdx);
                }
                cur.close();
                if (index == 0) {
                } else {
                    Uri u = Uri.parse("content://media/external/images/media/" + index);
                    System.out.println("temp uri is :" + u);
                }
            }
            if (path != null) {
                return new File(path);
            }
        } else if ("content".equals(uri.getScheme())) {
            // 4.2.2以后
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
            }
            cursor.close();
            return new File(path);
        } else {
            //Log.i(TAG, "Uri Scheme:" + uri.getScheme());
        }
        return null;
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    public static void setAvatar(Context context,String url,ImageView headView){
        Glide.with(context).load( Util.getFileBaseUrl(context) +url)
                .error(R.drawable.t2).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE ).into(headView);
    }

    public static void sendShortcut(Context context,int badgeCount){
        ShortcutBadger.applyCount(context, badgeCount);
    }

    public static void creatFloatLayout(final Context context, int width, int height) {
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //设置type.系统提示型窗口，一般都在应用程序窗口之上.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else{
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

        }
        //设置效果为背景透明.
        params.format = PixelFormat.RGBA_8888;
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        //设置窗口初始停靠位置.
        params.gravity = Gravity.RIGHT | Gravity.TOP;
        params.x = 0;
        params.y = 0;
        params.width = width;
        params.height = height;

        final View layout = LayoutInflater.from(context).inflate(R.layout.layout_float, null);
        windowManager.addView(layout, params);

        layout.setOnTouchListener(new View.OnTouchListener() {
            boolean isClick = false;
            int lastX, lastY;
            int windowX, windowY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = x;
                        lastY = y;
                        windowX = params.x;
                        windowY = params.y;
                        isClick = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        final int diff = Math.max(Math.abs(lastX - x), Math.abs(lastY - y));
                        if (diff < 10)
                            break;
                        isClick = false;
                        params.x = windowX - (x - lastX);
                        params.y = windowY + (y - lastY);
                        windowManager.updateViewLayout(layout, params);
                        break;
                }

                return isClick ? false : true;
            }
        });
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MyReceiver.class);
                context.sendBroadcast(intent);
                windowManager.removeView(layout);
            }
        });

    }


}
