package com.kaituocn.govsafety;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaituocn.govsafety.util.CircleView;
import com.kaituocn.govsafety.util.Util;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView action1View,action2View;
    CircleView circleView;
    TextView timeView;
    TextView numView;
    TextView showClientTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("政务安全宝");

        action1View=findViewById(R.id.action1View);
        action2View=findViewById(R.id.action2View);
        action1View.setVisibility(View.VISIBLE);
        action2View.setVisibility(View.VISIBLE);
        action1View.setImageResource(R.drawable.icon_scan);
        action2View.setImageResource(R.drawable.icon_set);
        action1View.setOnClickListener(this);
        action2View.setOnClickListener(this);

        showClientTV=findViewById(R.id.textView4);
        showClientTV.setOnClickListener(this);

        circleView=findViewById(R.id.circleView);
        timeView=findViewById(R.id.timeView);
        timeView.setText("60");
        circleView.setProgress(600);
        numView=findViewById(R.id.numView);

        timer.start();
    }

    private CountDownTimer timer=new CountDownTimer(60*1000,100) {
        @Override
        public void onTick(long millisUntilFinished) {
            circleView.setProgress((int) (millisUntilFinished/100));
            timeView.setText(""+millisUntilFinished/1000);
            if (millisUntilFinished/1000>15) {
                numView.setTextColor(0xff666666);
            }else{
                numView.setTextColor(getResources().getColor(R.color.gs_red));
            }
        }

        @Override
        public void onFinish() {
            circleView.setProgress(0);
            timeView.setText("0");

            circleView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    timer.start();
                }
            },1000);
        }
    };

    private boolean checkPermission(Context context){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            return false;
        }else{
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length == 1) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Util.ShowToast(this, "请打开相机权限");
                    } else{
                        Intent intent=new Intent(this,ScanActivity.class);
                        startActivity(intent);
                    }
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.action1View:
                if (checkPermission(v.getContext())) {
                    intent=new Intent(v.getContext(),ScanActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.action2View:
                intent=new Intent(v.getContext(),SetActivity.class);
                startActivity(intent);
                break;
            case R.id.textView4:
                intent=new Intent(v.getContext(),ClientsActivity.class);
                startActivity(intent);
                break;
        }
    }
}
