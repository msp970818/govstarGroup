package com.kaituocn.govstar.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.kaituocn.govstar.R;
import com.kaituocn.govstar.util.SharedPreferencesUtils;
import com.kaituocn.govstar.util.Util;

public class Step3Activity extends AppCompatActivity {

    Button nextBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_step3);
        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("首次使用");

        nextBtn=findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtils.setParam(v.getContext(),SharedPreferencesUtils.KEY_FIRST_USE,true);
                Intent intent=new Intent(v.getContext(),LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
