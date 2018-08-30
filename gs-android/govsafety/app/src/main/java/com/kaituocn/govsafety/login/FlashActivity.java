package com.kaituocn.govsafety.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;

import com.kaituocn.govsafety.MainActivity;
import com.kaituocn.govsafety.R;
import com.kaituocn.govsafety.util.SharedPreferencesUtils;

public class FlashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_flash);

        findViewById(R.id.imageView).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(SharedPreferencesUtils.getData(getBaseContext(), SharedPreferencesUtils.KEY_AUTHKEY))) {
                    Intent intent=new Intent(getBaseContext(),Login1Activity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent=new Intent(getBaseContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },2000);

    }
}
