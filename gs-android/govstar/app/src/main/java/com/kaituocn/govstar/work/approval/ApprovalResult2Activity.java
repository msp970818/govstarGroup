package com.kaituocn.govstar.work.approval;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.ApprovalEntity;
import com.kaituocn.govstar.util.Util;
import com.kaituocn.govstar.work.worklist.WorkDoActivity;

public class ApprovalResult2Activity extends AppCompatActivity {

    ApprovalEntity entity;
    int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_approval_result2);

        entity= (ApprovalEntity) getIntent().getSerializableExtra("entity");
        type=getIntent().getIntExtra("type",0);
        if (type==1) {//同意
            findViewById(R.id.group2).setVisibility(View.GONE);
        }else{
            findViewById(R.id.group1).setVisibility(View.GONE);
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

        ((TextView)findViewById(R.id.actionTextView1)).setText("立即办理");
        ((TextView)findViewById(R.id.actionTextView1)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_btn_do,0,0,0);
        findViewById(R.id.actionView1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),WorkDoActivity.class);
                startActivity(intent);
            }
        });


        initView(entity);
    }

    private void initView(ApprovalEntity entity){
        if (entity == null)
            return;

        if (type==1) {//同意
            Util.setBackgroundTint(findViewById(R.id.view3),R.color.item_green);
            ((TextView)findViewById(R.id.textView47)).setText("审批延期通过");
            ((TextView)findViewById(R.id.textView61)).setText(Util.stempToStr(entity.getDelayTime(),null));
        }else{
            Util.setBackgroundTint(findViewById(R.id.view3),R.color.item_red);
            ((TextView)findViewById(R.id.textView47)).setText("审批延期通未过");
            ((EditText)findViewById(R.id.editText2)).setText(entity.getCheckText());
        }
        ((TextView)findViewById(R.id.textView48)).setText(entity.getApproverName());
        ((TextView)findViewById(R.id.textView69)).setText(Util.stempToStr(entity.getCheckTime(),null));
        ((TextView)findViewById(R.id.textView51)).setText(entity.getDelayTitle());
        ((TextView)findViewById(R.id.textView72)).setText(entity.getTanskNum());
        ((TextView)findViewById(R.id.textView60)).setText(Util.stempToStr(Long.parseLong(entity.getAskedCompleteTime()),"yyyy-MM-dd"));



    }
}
