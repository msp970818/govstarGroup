package com.kaituocn.govstar.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaituocn.govstar.R;
import com.kaituocn.govstar.util.Util;

public class Step1ChoseServiceActivity extends AppCompatActivity {

    ImageView backView;
    TextView actionView;
    CheckBox checkBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_step1_chose_service);
        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("选择所在服务区");
        backView=findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult();
                finish();
            }
        });
        actionView=findViewById(R.id.actionView);
        actionView.setVisibility(View.VISIBLE);
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult();
                finish();
            }
        });

        checkBox=findViewById(R.id.checkBox);
        if (getIntent().getBooleanExtra("check",false)) {
            checkBox.setChecked(true);
        }
    }

    private void setResult(){
        Intent intent=new Intent();
        intent.putExtra("check",checkBox.isChecked());
        setResult(RESULT_OK,intent);
    }

    @Override
    public void onBackPressed() {
        setResult();
        super.onBackPressed();
    }
}
