package com.kaituocn.govstar.work.approval;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.ApprovalEntity;
import com.kaituocn.govstar.util.Util;

public class ApprovalResult1Activity extends AppCompatActivity {

    ApprovalEntity entity;
    int type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_approval_do);

        entity= (ApprovalEntity) getIntent().getSerializableExtra("entity");
        type=getIntent().getIntExtra("type",0);
        if (type==1) {//同意
            findViewById(R.id.group).setVisibility(View.GONE);
        }
        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("审批结果");
        View backView=findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.bottomLayout).setVisibility(View.GONE);

        initView(entity);
    }

    private void initView(ApprovalEntity entity){
        if (entity == null)
            return;
        ((TextView)findViewById(R.id.textView47)).setText(entity.getDelayTitle());
        ((TextView)findViewById(R.id.textView48)).setText(entity.getTanskNum());
        ((TextView)findViewById(R.id.textView69)).setText(entity.getDelayUserName());
        ((TextView)findViewById(R.id.textView51)).setText(entity.getDelayComName()+entity.getDelayDeptName());
        ((TextView)findViewById(R.id.textView60)).setText(Util.stempToStr(Long.parseLong(entity.getAskedCompleteTime()),"yyyy-MM-dd"));
        if (type==1) {
            ((TextView)findViewById(R.id.textView61)).setText("已同意"+Util.stempToStr(entity.getDelayTime(),"yyyy-MM-dd"));
            ((TextView)findViewById(R.id.textView61)).setTextColor(getResources().getColor(R.color.item_green));
        }else{
            ((TextView)findViewById(R.id.textView61)).setText("已拒绝"+Util.stempToStr(entity.getDelayTime(),"yyyy-MM-dd"));
            ((TextView)findViewById(R.id.textView61)).setTextColor(getResources().getColor(R.color.item_red));
            ((EditText)findViewById(R.id.editText2)).setText(entity.getCheckText());
        }
        ((TextView)findViewById(R.id.textView57)).setText(entity.getDelayText());
    }
}
