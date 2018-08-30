package com.kaituocn.govstar.work.cloudmeeting;

import android.support.constraint.Group;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.MeetingEntity;
import com.kaituocn.govstar.util.Constant;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LeaveActivity extends AppCompatActivity {

    Group groupAgent, groupAnnex, groupNoSignUp;
    EditText editText;
    MeetingEntity entity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_meeting_sign_up);

        entity = (MeetingEntity) getIntent().getSerializableExtra("entity");

        TextView titleView = findViewById(R.id.titleView);
        titleView.setText("请假");

        View backView = findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView actionView = findViewById(R.id.actionView);
        actionView.setVisibility(View.VISIBLE);
        actionView.setText("完成");
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leave();
            }
        });

        findViewById(R.id.groupAnnex).setVisibility(View.GONE);
        findViewById(R.id.groupNoSignUp).setVisibility(View.GONE);
        findViewById(R.id.groupJoins).setVisibility(View.GONE);

        ((TextView)findViewById(R.id.textView59)).setText("请假说明");
        editText=findViewById(R.id.editText2);
        editText.setHint("");
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        initView();
    }

    private void initView(){
        ((TextView) findViewById(R.id.textView45)).setText(entity.getMeetingTitile());
        ((TextView) findViewById(R.id.textView46)).setText(entity.getMettingNum());
        ((TextView) findViewById(R.id.textView54)).setText(Constant.getMeetingName(entity.getMettingType()));
        ((TextView) findViewById(R.id.textView69)).setText(entity.getCreateName());
        ((TextView) findViewById(R.id.textView51)).setText(getMeetingState(entity));
        ((TextView) findViewById(R.id.textView113)).setText(Util.stempToStr(entity.getMettringStartTime(), null));
    }
    private String getMeetingState(MeetingEntity entity) {
        if (entity.getMettingState() == 1) {
            if (entity.getShowState() == 1) {
                return "等待报名";
            } else {
                return "进行中";
            }

        } else {
            return "已结束";
        }
    }

    private void leave(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/CloudMetting/mettingLeave";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> map=new HashMap<>();
                map.put("mettingId",entity.getId());
                map.put("leaveInfo",editText.getText().toString().trim());
                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                JSONObject object=new JSONObject();
                try {
                    object.putOpt("mettingId",entity.getId());
                    object.putOpt("leaveInfo",editText.getText().toString().trim());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return object;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
                        finish();
                    }else{
                        Util.showToast(getApplication(), jsonObj.getString("message"));
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
