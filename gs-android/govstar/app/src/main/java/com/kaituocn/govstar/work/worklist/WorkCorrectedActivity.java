package com.kaituocn.govstar.work.worklist;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kaituocn.govstar.R;
import com.kaituocn.govstar.event.RefreshEvent;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class WorkCorrectedActivity extends AppCompatActivity {

    boolean showAction;
    String id;
    int taskId;
    JSONObject entity;

    EditText editText;
    boolean isDoing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_work_corrected);
        TextView titleView = findViewById(R.id.titleView);
        titleView.setText("整改说明");
        View backView = findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editText=findViewById(R.id.editText2);

        showAction=getIntent().getBooleanExtra("showAction",false);
        if (showAction) {
            TextView actionView = findViewById(R.id.actionView);
            actionView.setVisibility(View.VISIBLE);
            actionView.setText("提交");
            actionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String info=editText.getText().toString().trim();
                    if (!TextUtils.isEmpty(info)) {
                        submit();
                    }else{
                        Util.showToast(v.getContext(),"请填写整改说明");
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
        }

        id=getIntent().getStringExtra("id");
        taskId=getIntent().getIntExtra("taskId",0);

        System.out.println("============"+id+"   "+taskId);

        isDoing=getIntent().getBooleanExtra("isDoing",false);
        if (isDoing) {
            getDetailInfoDoing();
        }else{
            getDetailInfo();
        }
    }

    private void initView(JSONObject object){
        try {
            ((TextView)findViewById(R.id.textView45)).setText(object.getString("supervisionTitle"));
            ((TextView)findViewById(R.id.textView48)).setText(object.getString("supervisionNumber"));
            ((TextView)findViewById(R.id.textView66)).setText(object.getString("mainRoleName"));
            ((TextView)findViewById(R.id.textView61)).setText(Util.stempToStr(object.getLong("endTime"),"yyyy-MM-dd"));
            ((TextView)findViewById(R.id.textView62)).setText(object.getString("timeout")+"天");
            ((TextView)findViewById(R.id.textView63)).setText(object.getString("ScoreNum"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void initViewDoing(JSONObject object){
        try {
            ((TextView)findViewById(R.id.textView45)).setText(object.getString("SupervisionTitle"));
            ((TextView)findViewById(R.id.textView48)).setText(object.getString("SupervisionNumber"));
            ((TextView)findViewById(R.id.textView66)).setText(object.getString("LeaderName"));
            ((TextView)findViewById(R.id.textView61)).setText(Util.stempToStr(object.getLong("EndTime"),"yyyy-MM-dd"));
            ((TextView)findViewById(R.id.textView62)).setText(object.getString("Overtime")+"天");
            ((TextView)findViewById(R.id.textView63)).setText(object.getString("ScoreNum"));
            editText.setText(object.getString("tSupervRectification"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    initView(entity);
                    break;
                case 2:
                    initViewDoing(entity);
                    break;
            }

        }
    };

    private void getDetailInfo(){
        RequestUtil.jsonObjectRequest(new RequestListener() {
            @Override
            public String getUrl() {
                return "/WorkTask/TimeoutLocking";
            }

            @Override
            public Map<String, String> getParams() {
                return null;
            }

            @Override
            public JSONObject getJsonObj() {
                JSONObject object=new JSONObject();
                try {
                    object.putOpt("taskId",taskId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return object;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("getDetailInfo==============="+jsonObj);
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1) {
                        entity=jsonObj.getJSONObject("data");
                        handler.sendEmptyMessage(1);
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

    private void getDetailInfoDoing(){
        RequestUtil.jsonObjectRequest(new RequestListener() {
            @Override
            public String getUrl() {
                return "/WorkTask/selectRectification";
            }

            @Override
            public Map<String, String> getParams() {
                return null;
            }

            @Override
            public JSONObject getJsonObj() {
                JSONObject object=new JSONObject();
                try {
                    object.putOpt("companyId",id);
                    object.putOpt("supervisionId",taskId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return object;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("getDetailInfoDoing==============="+jsonObj);
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1) {
                        entity=jsonObj.getJSONObject("data");
                        handler.sendEmptyMessage(2);
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

    Dialog dialog;
    private void submit(){
        dialog=Util.showLoadingDialog(this,"提交中…");
        RequestUtil.jsonObjectRequest(new RequestListener() {
            @Override
            public String getUrl() {
                return "/WorkTask/insert/rectification";
            }

            @Override
            public Map<String, String> getParams() {
                return null;
            }

            @Override
            public JSONObject getJsonObj() {
                JSONObject object=new JSONObject();
                try {
                    object.putOpt("supervisionId",id);
                    object.putOpt("taskId",taskId);
                    object.putOpt("rectificationInfo",editText.getText().toString().trim());
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
                        EventBus.getDefault().post(new RefreshEvent(RefreshEvent.Refresh_WorkList));
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
