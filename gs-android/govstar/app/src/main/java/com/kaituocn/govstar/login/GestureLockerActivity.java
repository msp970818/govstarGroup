package com.kaituocn.govstar.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.github.ihsg.patternlocker.OnPatternChangeListener;
import com.github.ihsg.patternlocker.PatternIndicatorView;
import com.github.ihsg.patternlocker.PatternLockerView;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.main.MainActivity;
import com.kaituocn.govstar.util.SharedPreferencesUtils;
import com.kaituocn.govstar.util.Util;

import java.util.ArrayList;
import java.util.List;


public class GestureLockerActivity extends AppCompatActivity {

    PatternIndicatorView indicatorView;
    PatternLockerView lockerView;
    View skipView;

    final int STATUS_SET = 1;
    final int STATUS_UNLOCK = 2;
    int status = 1;
    int step = 0;

    List<Integer> password = new ArrayList<>();


    boolean isSetting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_gesture_locker);
        TextView titleView = findViewById(R.id.titleView);
        titleView.setText("设置解锁手势");

        isSetting=getIntent().getBooleanExtra("isSetting",false);

        skipView=findViewById(R.id.skipView);
        skipView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getBaseContext(), MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                overridePendingTransition(R.anim.activity_enter_next, R.anim.activity_exit_next);

                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if (getIntent().getBooleanExtra("unlock",false)) {
            skipView.setVisibility(View.GONE);
            status = STATUS_UNLOCK;
            password=getPassword(SharedPreferencesUtils.getData(this,SharedPreferencesUtils.KEY_GESTURE_CODE));
            ((TextView)findViewById(R.id.textView43)).setText("请用手势密码解锁");
        }

        if (isSetting) {
            skipView.setVisibility(View.GONE);
        }

        indicatorView = findViewById(R.id.pattern_indicator_view);
        lockerView = findViewById(R.id.pattern_lock_view);

        lockerView.setOnPatternChangedListener(new OnPatternChangeListener() {
            @Override
            public void onStart(PatternLockerView patternLockerView) {

            }

            @Override
            public void onChange(PatternLockerView patternLockerView, List<Integer> list) {
                indicatorView.updateState(list, false);
            }

            @Override
            public void onComplete(PatternLockerView patternLockerView, List<Integer> list) {
                if (status == STATUS_SET) {
                    if (step == 0) {
                        password.clear();
                        password.addAll(list);
                        handler.sendEmptyMessage(1);
                        step++;
                    } else if (step > 0) {
                        boolean isError = isErrorPassword(list, password);
                        if (isError) {
                            step = 0;
                            handler.sendEmptyMessage(2);
                        } else {
//                            status = STATUS_UNLOCK;
                            handler.sendEmptyMessage(3);
                        }
                    }

                } else if (status == STATUS_UNLOCK) {
                    boolean isError = isErrorPassword(list, password);
                    patternLockerView.updateStatus(isError);
                    indicatorView.updateState(list, isError);
                    handler.sendEmptyMessage(isError ? 10 : 11);
                }


            }

            @Override
            public void onClear(PatternLockerView patternLockerView) {

            }
        });


    }

    private boolean isErrorPassword(List<Integer> listA, List<Integer> listB) {
        boolean isError = false;
        if (listA == null || listB == null) {
            return true;
        }
        if (listA.size() != listB.size()) {
            return true;
        }
        for (int i = 0; i < listA.size(); i++) {
            if (listA.get(i) != listB.get(i)) {
                isError = true;
                break;
            }
        }
        return isError;
    }

    private String getPassword(List<Integer> list){
        StringBuffer sb=new StringBuffer();
        for (int i = 0; i < list.size()-1; i++) {
           sb.append(list.get(i)+"-");
        }
        sb.append(list.get(list.size()-1)+"");
        return sb.toString();

    }

    private List<Integer> getPassword(String string){
        String[] strings=string.split("-");
        List<Integer> list=new ArrayList<>();
        for (String s : strings) {
            list.add(Integer.parseInt(s));
        }
        return list;
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent;
            switch (msg.what) {
                case 1:
                    Util.showToast(getApplication(), "请再次绘制确认密码");
                    break;
                case 2:
                    Util.showToast(getApplication(), "两次绘制手势不同，请重新输入");
                    break;
                case 3:
//                    Util.showToast(getApplication(), "请解锁手势");
                    SharedPreferencesUtils.setParam(getBaseContext(),SharedPreferencesUtils.KEY_GESTURE,true);
                    SharedPreferencesUtils.setParam(getBaseContext(),SharedPreferencesUtils.KEY_GESTURE_CODE,getPassword(password));
                    if (isSetting) {
                        setResult(RESULT_OK);
                        finish();
                    }else{
                        intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    break;
                case 10:
                    Util.showToast(getApplication(), "密码错误");
                    break;
                case 11:
                    intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };
}
