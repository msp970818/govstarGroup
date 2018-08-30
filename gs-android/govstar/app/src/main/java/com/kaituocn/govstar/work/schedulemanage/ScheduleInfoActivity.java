package com.kaituocn.govstar.work.schedulemanage;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.OptionEntity;
import com.kaituocn.govstar.entity.ScheduleEntity;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleInfoActivity extends AppCompatActivity {

    EditText scheduleTitleView;
    EditText scheduleInfoView;
    TextView executorView;
    TextView importanceView;
    TextView scheduleTypeView;
    TextView startTime;
    TextView endTime;
    TextView numberView;

    ScheduleEntity entity;
    int id;

    List<OptionEntity> importanceList;
    List<OptionEntity> typeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_create_schedule);

        id=getIntent().getIntExtra("id",0);

        TextView titleView = findViewById(R.id.titleView);
        titleView.setText("日程详情");
        View backView = findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initView();

        getScheduleInfo(id);
    }

    private void initView(){
        scheduleTitleView = findViewById(R.id.editText1);
        scheduleInfoView = findViewById(R.id.editText2);
        numberView=findViewById(R.id.textView9);
        executorView = findViewById(R.id.textView4);
        importanceView = findViewById(R.id.textView7);
        scheduleTypeView = findViewById(R.id.textView8);
        startTime = findViewById(R.id.textView5);
        endTime = findViewById(R.id.textView6);

        scheduleTitleView.setFocusable(false);
        scheduleInfoView.setFocusable(false);
        numberView.setCompoundDrawables(null,null,null,null);
        executorView.setCompoundDrawables(null,null,null,null);
        importanceView.setCompoundDrawables(null,null,null,null);
        scheduleTypeView.setCompoundDrawables(null,null,null,null);
        startTime.setCompoundDrawables(null,null,null,null);
        endTime.setCompoundDrawables(null,null,null,null);


    }



    private void setData(JSONObject jsonObject){
        try {
            JSONArray array = jsonObject.getJSONArray("names");
            StringBuffer stringBuffer=new StringBuffer();
            for (int i = 0; i < array.length(); i++) {
                stringBuffer.append(array.get(i).toString());
                stringBuffer.append(",");
            }
            String string = stringBuffer.toString();
            executorView.setText(string.substring(0, string.length() - 1));

            entity=new Gson().fromJson(jsonObject.getString("body"),ScheduleEntity.class);
            setData(entity);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setData(ScheduleEntity entity){
        if (entity != null) {
            scheduleTitleView.setText(entity.getScheduleTitle());
            scheduleInfoView.setText(entity.getScheduleDescribe());
            startTime.setText(entity.getStartTime());
            endTime.setText(entity.getEndTime());
            numberView.setText(entity.getScheduleNumber());
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    setData((JSONObject) msg.obj);
                    loadImportance();
                    loadScheduleType();
                    break;
                case 2:
                    importanceView.setText(msg.obj.toString());
                    break;
                case 3:
                    scheduleTypeView.setText(msg.obj.toString());
                    break;
            }
        }
    };

    private void getScheduleInfo(final int id){

        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/schedule/id";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String,String> map=new HashMap<>();
                map.put("id",id+"");
                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("==============="+jsonObj);
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1) {
                        handler.obtainMessage(1,jsonObj.getJSONObject("data")).sendToTarget();

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

    private void loadImportance() {
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/schedule/importance";
            }

            @Override
            public Map<String, String> getParams() {
                return null;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                importanceView.setEnabled(true);
//                System.out.println("==============" + jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
                        importanceList = new Gson().fromJson(jsonObj.getString("data"), new TypeToken<List<OptionEntity>>() {
                        }.getType());
                        for (OptionEntity optionEntity : importanceList) {
                            if (optionEntity.getKey()==entity.getScheduleImportance()) {
                                handler.obtainMessage(2,optionEntity.getValue()).sendToTarget();
                                break;
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                importanceView.setEnabled(true);
            }
        });
    }

    private void loadScheduleType() {
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/schedule/state";
            }

            @Override
            public Map<String, String> getParams() {
                return null;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                scheduleTypeView.setEnabled(true);
//                System.out.println("==============" + jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
                        typeList = new Gson().fromJson(jsonObj.getString("data"), new TypeToken<List<OptionEntity>>() {
                        }.getType());
                        for (OptionEntity optionEntity : typeList) {
                            if (optionEntity.getKey()==entity.getScheduleType()) {
                                handler.obtainMessage(3,optionEntity.getValue()).sendToTarget();
                                break;
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                scheduleTypeView.setEnabled(true);
            }
        });
    }
}
