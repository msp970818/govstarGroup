package com.kaituocn.govsafety;

import android.app.Application;
import android.os.SystemClock;

import com.kaituocn.govsafety.util.RequestUtil;

import java.io.PrintWriter;
import java.io.StringWriter;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RequestUtil.initRequestQueue(getApplicationContext());

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                StringWriter sw=new StringWriter();
                e.printStackTrace(new PrintWriter(sw,true));
                System.out.println("fffff========"+sw.toString());


                SystemClock.sleep(60000);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
    }
}
