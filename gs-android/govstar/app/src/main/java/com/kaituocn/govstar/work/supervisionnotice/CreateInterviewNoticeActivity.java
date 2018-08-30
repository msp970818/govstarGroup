package com.kaituocn.govstar.work.supervisionnotice;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateInterviewNoticeActivity extends AppCompatActivity implements View.OnClickListener {
    TimePickerView pvTime;
    TagFlowLayout tagFlowLayout;
    TagAdapter tagAdapter;
    List<JSONObject> tagList = new ArrayList<>();

    TextView interviewTime;
    EditText editText;

    int supervisionId;
    String endTimeString, overtimeInfo;
    Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_create_interview_notice);

        supervisionId = getIntent().getIntExtra("supervisionId", 0);

        TextView titleView = findViewById(R.id.titleView);
        titleView.setText("发送约谈告知单");
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
        actionView.setText("发送");
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    sendInterview();
                }
            }
        });

        ((TextView) findViewById(R.id.actionTextView1)).setText("结案，不执行约谈");
        ((TextView) findViewById(R.id.actionTextView1)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_btn_jiean, 0, 0, 0);
        findViewById(R.id.actionView1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endInterview();
            }
        });

        interviewTime = findViewById(R.id.textView57);
        interviewTime.setOnClickListener(this);

        editText = findViewById(R.id.editText2);
        editText.postDelayed(new Runnable() {
            @Override
            public void run() {
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
            }
        }, 1000);


        tagFlowLayout = findViewById(R.id.frameLayout2);
        tagFlowLayout.setAdapter(tagAdapter = new TagAdapter<JSONObject>(tagList) {
            @Override
            public View getView(FlowLayout parent, int position, JSONObject object) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag, parent, false);
                TextView textView = view.findViewById(R.id.textView);
                try {
                    textView.setText(object.getString("companyName"));
                    if ("1".equals(object.getString("code"))) {
                        Util.setBackgroundTint(textView,R.color.tag_red);
                    }else{
                        Util.setBackgroundTint(textView,R.color.tag_green);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                return view;
            }
        });

        getInfo();
    }

    private void initView(JSONObject object) {
        try {
            ((TextView) findViewById(R.id.textView45)).setText(object.getString("supervisionTitle"));
            ((TextView) findViewById(R.id.textView48)).setText(object.getString("supervisionNumber"));
            ((TextView) findViewById(R.id.textView60)).setText(object.getString("overTime") + "天");
            JSONArray jsonArray = object.getJSONArray("canSupervUnit");
            tagList.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                tagList.add(jsonArray.getJSONObject(i));
            }
            ((TextView) findViewById(R.id.textView65)).setText("可约谈 " + tagList.size() + " 个单位");
            tagAdapter.notifyDataChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    boolean checkInput() {
        if (TextUtils.isEmpty(endTimeString)) {
            Util.showToast(this, "请设置约谈时间！");
            return false;
        }
        overtimeInfo = editText.getText().toString().trim();
        if (TextUtils.isEmpty(overtimeInfo)) {
            Util.showToast(this, "请填写约谈信息！");
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView57:
                initTimePicker();
                break;
        }
    }

    private void initTimePicker() {
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            public void onTimeSelect(Date date, View v) {
                if (Util.checkStartTime(getApplication(),date)) {
                    interviewTime.setText(endTimeString = Util.getDateStr(date, "yyyy-MM-dd HH:00"));
                }else{
                    interviewTime.setText("");
                }
            }
        }).setType(new boolean[]{true, true, true, true, false, false})
                .setLabel("年", "月", "日", ":00", "分", "秒")
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
        calendar.set(Calendar.MINUTE,0);
        calendar.add(Calendar.HOUR_OF_DAY,1);
        pvTime.setDate(calendar);
        pvTime.show();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    initView((JSONObject) msg.obj);
                    break;
            }
        }
    };

    private void getInfo() {
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/app/talk/detail";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("supervisionId", supervisionId + "");
                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("getInfo=========" + jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
                        JSONObject resultObj = jsonObj.getJSONObject("data");
                        handler.obtainMessage(1, resultObj).sendToTarget();
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


    private void sendInterview() {
        dialog = Util.showLoadingDialog(this, "发送中…");
        RequestUtil.jsonObjectRequest(new RequestListener() {
            @Override
            public String getUrl() {
                return "/app/send/talk";
            }

            @Override
            public Map<String, String> getParams() {
                return null;
            }

            @Override
            public JSONObject getJsonObj() {
                JSONObject object=new JSONObject();
                try {
                    object.putOpt("supervisionId",supervisionId);
                    object.putOpt("endTimeString",endTimeString);
                    object.putOpt("overtimeInfo",overtimeInfo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return object;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Util.showToast(getBaseContext(), jsonObj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
    }

    private void endInterview(){
        dialog = Util.showLoadingDialog(this, "处理中…");
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/app/task/end";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("supervisionId", supervisionId + "");
                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Util.showToast(getBaseContext(), jsonObj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
    }
}
