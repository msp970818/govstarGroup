package com.kaituocn.govstar.login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaituocn.govstar.R;
import com.kaituocn.govstar.util.Util;

public class ForgetPasswordActivity extends AppCompatActivity {

    ImageView backView;
    Button loginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_forget_password);
        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("忘记用户名或密码");
        backView=findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loginBtn=findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),ModifyPasswordActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.button6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission(v.getContext())) {

                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_CALL);
                    intent.setAction(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:4006970960"));
                    startActivity(intent);
                }
            }
        });
    }

    private boolean checkPermission(Context context){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
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
                        Util.showToast(this, "请打开拨打电话权限权限");
                    } else{
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:4006970960"));
                        startActivity(intent);
                    }
                }
                break;
        }
    }
}
