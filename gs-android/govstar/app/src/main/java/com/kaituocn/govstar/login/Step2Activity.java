package com.kaituocn.govstar.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kaituocn.govstar.R;
import com.kaituocn.govstar.util.Util;

public class Step2Activity extends AppCompatActivity {

    Button button;
    TextView skipView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_step2);
        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("首次使用");

        button=findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showToast(v.getContext(),"此功能未参与试用，请等待正式版。");
            }
        });
        skipView=findViewById(R.id.skipView);
        skipView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),Step3Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
