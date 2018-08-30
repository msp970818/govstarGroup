package com.kaituocn.govsafety.login;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kaituocn.govsafety.MainActivity;
import com.kaituocn.govsafety.R;

public class Login2Activity extends AppCompatActivity {

    Button bindBtn,sendAgainBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("验证并绑定");
        View backView=findViewById(R.id.action1View);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bindBtn=findViewById(R.id.button2);
        bindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),MainActivity.class);
                startActivity(intent);
            }
        });
        sendAgainBtn=findViewById(R.id.button3);
        sendAgainBtn.setEnabled(false);

        timer.start();

    }


    private CountDownTimer timer = new CountDownTimer(30*1000,1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            sendAgainBtn.setText("重新发送验证码(" + millisUntilFinished/1000 +")");
        }

        @Override
        public void onFinish() {
            sendAgainBtn.setText("重新发送验证码");
            sendAgainBtn.setEnabled(true);
        }
    };
}
