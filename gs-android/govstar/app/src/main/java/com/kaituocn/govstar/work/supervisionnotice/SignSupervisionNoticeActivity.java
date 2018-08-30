package com.kaituocn.govstar.work.supervisionnotice;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.kaituocn.govstar.R;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignSupervisionNoticeActivity extends AppCompatActivity {

    int type;
    int supervisionId;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_create_supervision_notice);

        type = getIntent().getIntExtra("type", 0);

        supervisionId = getIntent().getIntExtra("supervisionId", 0);

        TextView titleView = findViewById(R.id.titleView);
        if (type == 0) {
            titleView.setText("签收督办通知单");

            ((TextView) findViewById(R.id.actionTextView1)).setText("立即签收");
            ((TextView) findViewById(R.id.actionTextView1)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_btn_sign, 0, 0, 0);
            findViewById(R.id.actionView1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signSupervision();
                }
            });
        } else {
            titleView.setText("已签收督办通知单");
            findViewById(R.id.bottomLayout).setVisibility(View.GONE);
        }

        View backView = findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ((TextView) findViewById(R.id.textView56)).setText("逾期时间");
        ((TextView) findViewById(R.id.textView58)).setText("限时时间");
        ((TextView) findViewById(R.id.textView59)).setText("督办信息");

        findViewById(R.id.frameLayout2).setVisibility(View.GONE);

        getDetail();
    }

    private void initView(JSONObject object) {
        try {
            ((TextView) findViewById(R.id.textView45)).setText(object.getString("supervisionTitle"));
            ((TextView) findViewById(R.id.textView48)).setText(object.getString("supervisionNumber"));
            ((TextView) findViewById(R.id.textView50)).setText("督查单位");
            ((TextView) findViewById(R.id.textView51)).setText(object.getString("superviseUnit"));
            ((TextView) findViewById(R.id.textView51)).setTextColor(getResources().getColor(R.color.text_black));
            ((TextView) findViewById(R.id.textView60)).setText("逾期未办结");
            ((TextView) findViewById(R.id.textView60)).setTextColor(getResources().getColor(R.color.text_black));
            ((TextView) findViewById(R.id.textView53)).setText("当前等待");
            ((TextView) findViewById(R.id.textView61)).setText(object.getString("transactor"));
            ((TextView) findViewById(R.id.textView61)).setTextColor(getResources().getColor(R.color.text_black));
            ((TextView) findViewById(R.id.textView62)).setText(object.getString("overTime") + "天");
            ((TextView) findViewById(R.id.textView62)).setTextColor(getResources().getColor(R.color.text_black));
            ((TextView) findViewById(R.id.textView53)).setText("限时时间");
            ((TextView) findViewById(R.id.textView57)).setText(object.getString("endTime"));
            ((TextView) findViewById(R.id.textView57)).setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            ((TextView) findViewById(R.id.editText2)).setText(object.getString("overTimeInfo"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    private void getDetail() {
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/app/sign/sup/detail";
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
                System.out.println("getDetail=========" + jsonObj);
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


    private void signSupervision() {
        dialog = Util.showLoadingDialog(this, "处理中…");
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/app/sign/supervise";
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
