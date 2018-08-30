package com.kaituocn.govstar.login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kaituocn.govstar.R;
import com.kaituocn.govstar.util.Util;

public class Step1Activity extends AppCompatActivity {

    Button btn1, btn2, nextBtn;
    boolean flag1, flag2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());

        setContentView(R.layout.activity_step1);
        TextView titleView = findViewById(R.id.titleView);
        titleView.setText("首次使用");

        btn1 = findViewById(R.id.button1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Step1ChoseServiceActivity.class);
                intent.putExtra("check", flag1);
                startActivityForResult(intent, 1);
            }
        });
        btn2 = findViewById(R.id.button2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission(v.getContext())) {
                    Intent intent = new Intent(v.getContext(), Step1ScanActivity.class);
                    startActivityForResult(intent, 2);
                }
            }
        });
        nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Step2Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    if (data.getBooleanExtra("check", false)) {
                        btn1.setText("已选择 天津市-蓟州区");
                        flag1 = true;
                    } else {
                        btn1.setText("选择所在服务区");
                        flag1 = false;
                    }
                    break;
                case 2:
                    String id = data.getStringExtra("id");
                    if ("TJJZZFBDC2025".equals(id)) {
                        btn2.setText("认证成功");
                        flag2 = true;
                    } else {
                        btn2.setText("请扫码正确二维码");
                        flag2 = false;
                    }
                    break;
            }

        } else {
            switch (requestCode) {
                case 2:
                    btn2.setText("请扫码正确二维码");
                    flag2 = false;
//                    btn2.setText("认证成功");
//                    flag2=true;
                    break;
            }
        }
        if (flag1 && flag2) {
            nextBtn.setEnabled(true);
        } else {
            nextBtn.setEnabled(false);
        }
    }

    private boolean checkPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length == 1) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Util.showToast(this, "请打开相机权限");
                    } else {
                        Intent intent = new Intent(this, Step1ScanActivity.class);
                        startActivityForResult(intent, 2);
                    }
                }
                break;
        }
    }
}
