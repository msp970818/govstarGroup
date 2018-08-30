package com.kaituocn.govstar.work.worklist;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.WorkDoEntity;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class ApplyDelayActivity extends AppCompatActivity {

    int id;

    EditText editText;
    TextView delayTimeView;
    String delayTime;

    WorkDoEntity entity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_apply_delay);

        id = getIntent().getIntExtra("id", 0);
        entity= (WorkDoEntity) getIntent().getSerializableExtra("entity");

        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("申请延期");
        View backView=findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView actionView = findViewById(R.id.actionView);
        actionView.setVisibility(View.VISIBLE);
        actionView.setText("提交");
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    submit();
                }
            }
        });


        editText=findViewById(R.id.editText2);
        editText.post(new Runnable() {
            @Override
            public void run() {
                editText.setFocusableInTouchMode(true);
                editText.setFocusable(true);
            }
        });

        delayTimeView=findViewById(R.id.textView53);
        delayTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.hideInput(v.getContext(),v);
                initTimePicker();
            }
        });

        initView(entity);
    }

    private boolean checkInput(){
        if (TextUtils.isEmpty(delayTime)) {
            Util.showToast(this,"请设置延期时间");
            return false;
        }
        if (TextUtils.isEmpty(editText.getText().toString().trim())) {
            Util.showToast(this,"请填写延期说明");
            return false;
        }
        return true;
    }

    private void initView(WorkDoEntity entity){
        if (entity==null) {
            return;
        }
        ((TextView)findViewById(R.id.textView45)).setText(entity.getSupervisionTitle());
        ((TextView)findViewById(R.id.textView46)).setText(entity.getSupervisionNumber());
        ((TextView)findViewById(R.id.textView60)).setText(Util.stempToStr(entity.getEndTime(),"yyyy-MM-dd"));

        WorkDoEntity.Company[] firstComps = entity.getUserList().getFirstComps();
        if (firstComps != null && firstComps.length > 0) {

            StringBuffer compBuffer = new StringBuffer();
            for (int i = 0; i < firstComps.length; i++) {
                compBuffer.append(firstComps[i].getComName());
                if (i != firstComps.length - 1) {
                    compBuffer.append(",");
                }
            }
            ((TextView) findViewById(R.id.textView54)).setText(compBuffer.toString());

            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < firstComps.length; i++) {
                stringBuffer.append(firstComps[i].getUserName());
                if (i != firstComps.length - 1) {
                    stringBuffer.append(",");
                }
            }
            ((TextView) findViewById(R.id.textView69)).setText(stringBuffer.toString());
        }


    }

    TimePickerView pvTime;
    private void initTimePicker() {
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            public void onTimeSelect(Date date, View v) {
                if (Util.checkStartTime(getApplication(),date)) {
                    delayTimeView.setText(delayTime=Util.getDateStr(date, "yyyy-MM-dd"));
                }else{
                    delayTimeView.setText("");
                }
            }
        }).setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("年", "月", "日", "时", "分", "秒")
//                .setCancelColor(Color.GRAY)
//                .setSubmitColor(getResources().getColor(R.color.gs_red))
//                .setSubCalSize(15)
                .setLineSpacingMultiplier(3f)
                .setContentTextSize(15)
                .setDividerColor(0x00ffffff)
                .setLayoutRes(R.layout.layout_custom_pickerview_time, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = v.findViewById(R.id.tv_finish);
                        TextView tvCancel = v.findViewById(R.id.tv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvTime.returnData();
                                pvTime.dismiss();
                            }
                        });
                        tvCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvTime.dismiss();
                            }
                        });
                    }
                })
                .build();
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        pvTime.setDate(calendar);
        pvTime.show();
    }





    Dialog dialog;
    private void submit(){
        dialog=Util.showLoadingDialog(this,"提交中…");
        RequestUtil.jsonObjectRequest(new RequestListener() {
            @Override
            public String getUrl() {
                return "/DelayApproval/addDelay";
            }

            @Override
            public Map<String, String> getParams() {
                return null;
            }

            @Override
            public JSONObject getJsonObj() {
                JSONObject object=new JSONObject();
                try {
                    object.putOpt("delayTime",delayTime);
                    object.putOpt("taskId",id);
                    object.putOpt("delayText",editText.getText().toString().trim());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return object;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                dialog.dismiss();
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1) {
                        setResult(RESULT_OK);
                        finish();
                    }else{
                        Util.showToast(getApplication(),jsonObj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                dialog.dismiss();
                Util.showToast(getApplication(),getString(R.string.error_server));
            }
        });
    }
}
