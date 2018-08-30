package com.kaituocn.govstar.work.worklist;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.WorkDoEntity;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WorkReturnActivity extends AppCompatActivity {

    WorkDoEntity entity;
    EditText editText;

    int id,taskId;
    JSONObject object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_work_return);

        entity= (WorkDoEntity) getIntent().getSerializableExtra("entity");

        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("工作退办");
        View backView=findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editText=findViewById(R.id.editText2);

        if (getIntent().getBooleanExtra("isDoing",false)) {
            id=getIntent().getIntExtra("id",0);
            taskId=getIntent().getIntExtra("taskId",0);
            getDetailInfo();

            findViewById(R.id.bottomLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.actionGroup1).setVisibility(View.GONE);
            findViewById(R.id.actionGroup2).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.actionTextView2)).setText("拒绝");
            ((TextView)findViewById(R.id.actionTextView3)).setText("同意");
            ((TextView)findViewById(R.id.actionTextView2)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_btn_jujue,0,0,0);
            ((TextView)findViewById(R.id.actionTextView3)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_btn_tongyi,0,0,0);

            findViewById(R.id.actionView2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reject();
                }
            });
            findViewById(R.id.actionView3).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    agree();
                }
            });

        }else{
            TextView actionView = findViewById(R.id.actionView);
            actionView.setVisibility(View.VISIBLE);
            actionView.setText("完成");
            actionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String info=editText.getText().toString().trim();
                    if (!TextUtils.isEmpty(info)) {
                        submit();
                    }else{
                        Util.showToast(v.getContext(),"请填写退办原因");
                    }
                }
            });

            editText.post(new Runnable() {
                @Override
                public void run() {
                    editText.setFocusableInTouchMode(true);
                    editText.setFocusable(true);
                }
            });

            initView(entity);
        }





    }

    private void initView(JSONObject object){
        try {
            ((TextView)findViewById(R.id.textView45)).setText(object.getString("supervisionTitle"));
            ((TextView)findViewById(R.id.textView48)).setText(object.getString("supervisionNumber"));
            ((TextView)findViewById(R.id.textView69)).setText(object.getString("mainPerson"));
            editText.setText(object.getString("supervisionInfo"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initView(WorkDoEntity entity) {
        if (entity == null) {
            return;
        }
        ((TextView)findViewById(R.id.textView45)).setText(entity.getSupervisionTitle());
        ((TextView)findViewById(R.id.textView48)).setText(entity.getSupervisionNumber());
        WorkDoEntity.Company[] firstComps = entity.getUserList().getFirstComps();
        if (firstComps != null && firstComps.length > 0) {

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


    Dialog dialog;
    private void submit(){
        dialog=Util.showLoadingDialog(this,"提交中…");
        RequestUtil.jsonObjectRequest(new RequestListener() {
            @Override
            public String getUrl() {
                return "/WorkTask/backOffice";
            }

            @Override
            public Map<String, String> getParams() {
                return null;
            }

            @Override
            public JSONObject getJsonObj() {
                JSONObject object=new JSONObject();
                try {
                    object.putOpt("id",entity.getId());
                    object.putOpt("taskId",entity.getTaskId());
                    object.putOpt("supervisionInfo",editText.getText().toString().trim());
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

    private void getDetailInfo(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/supervision/back/office";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> map=new HashMap<>();
                map.put("id",id+"");
                map.put("taskId",taskId+"");
                return map;
            }

            @Override
            public JSONObject getJsonObj() {

                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("getWorkPlanInfo=============="+jsonObj);
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1){
                        object=jsonObj.getJSONObject("data");
                        initView(object);
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

    private void agree(){
        RequestUtil.jsonObjectRequest(new RequestListener() {
            @Override
            public String getUrl() {
                return "/WorkTask/backOfficeAgree";
            }

            @Override
            public Map<String, String> getParams() {
                return null;
            }

            @Override
            public JSONObject getJsonObj() {
                JSONObject object=new JSONObject();
                try {
                    object.putOpt("id",id);
                    object.putOpt("taskId",taskId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return object;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
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
                Util.showToast(getApplication(),getString(R.string.error_server));
            }
        });
    }

    private void reject(){
        RequestUtil.jsonObjectRequest(new RequestListener() {
            @Override
            public String getUrl() {
                return "/WorkTask/backOfficeReject";
            }

            @Override
            public Map<String, String> getParams() {
                return null;
            }

            @Override
            public JSONObject getJsonObj() {
                JSONObject object=new JSONObject();
                try {
                    object.putOpt("id",id);
                    object.putOpt("taskId",taskId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return object;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
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
                Util.showToast(getApplication(),getString(R.string.error_server));
            }
        });
    }
}
