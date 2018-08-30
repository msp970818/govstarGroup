package com.kaituocn.govstar.work.approval;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.ApprovalEntity;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApprovalDoActivity extends AppCompatActivity {

    EditText editText;

    ApprovalEntity entity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_approval_do);

        entity= (ApprovalEntity) getIntent().getSerializableExtra("entity");

        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("审批");
        View backView=findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.actionGroup1).setVisibility(View.GONE);
        findViewById(R.id.actionGroup2).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.actionTextView2)).setText("拒绝");
        ((TextView)findViewById(R.id.actionTextView3)).setText("同意");
        ((TextView)findViewById(R.id.actionTextView2)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_btn_jujue,0,0,0);
        ((TextView)findViewById(R.id.actionTextView3)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_btn_tongyi,0,0,0);

        findViewById(R.id.actionView2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editText.getText().toString().trim())) {
                    Util.showToast(v.getContext(),"请填写拒绝原因！");
                }else{
                    refuse(true);
                }
            }
        });
        findViewById(R.id.actionView3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refuse(false);
            }
        });

        editText=findViewById(R.id.editText2);
        editText.post(new Runnable() {
            @Override
            public void run() {
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
            }
        });

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
        ((TextView)findViewById(R.id.textView61)).setText(Util.stempToStr(entity.getDelayTime(),"yyyy-MM-dd"));
        ((TextView)findViewById(R.id.textView61)).setTextColor(getResources().getColor(R.color.item_blue));
        ((TextView)findViewById(R.id.textView57)).setText(entity.getDelayText());
    }


    private void refuse(final boolean refuse){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/DelayApproval/handleDelay";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String,String> map=new HashMap<>();
                if (refuse){
                    map.put("resultState",5+"");
                    map.put("checkText",editText.getText().toString().trim());
                }else {
                    map.put("resultState",4+"");
                    map.put("checkText","");
                }
                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1) {
                        setResult(RESULT_OK);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }
}
