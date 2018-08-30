package com.kaituocn.govstar.work.supervisionnotice;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterviewNoticeStatusActivity extends AppCompatActivity {

    TagFlowLayout tagFlowLayout;
    TagAdapter tagAdapter;
    List<HashMap<String,String>> tagList=new ArrayList<>();

    int supervisionId;

    TextView statusView;
    TextView interviewTime;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_create_interview_notice);

        supervisionId=getIntent().getIntExtra("supervisionId",0);

        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("约谈告知状态");
        View backView=findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ((TextView)findViewById(R.id.actionTextView1)).setText("结案");
        ((TextView)findViewById(R.id.actionTextView1)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_btn_jiean,0,0,0);
        findViewById(R.id.actionView1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endInterview();
            }
        });

        findViewById(R.id.textView65).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.textView52)).setText("状态");
        statusView=findViewById(R.id.textView60);
        statusView.setText("已约谈");
        statusView.setTextColor(getResources().getColor(R.color.text_gray));
        interviewTime=findViewById(R.id.textView57);
        interviewTime.setCompoundDrawables(null,null,null,null);
        editText=findViewById(R.id.editText2);
//        editText.setBackgroundColor(0x0000);



        tagFlowLayout=findViewById(R.id.frameLayout2);
        tagFlowLayout.setAdapter(tagAdapter=new TagAdapter<HashMap<String,String>>(tagList) {
            @Override
            public View getView(FlowLayout parent, int position, HashMap<String,String> map) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag_2,parent, false);
                TextView textView=view.findViewById(R.id.textView);
                TextView textView2=view.findViewById(R.id.textView2);
                if ("2".equals(map.get("state"))) {
                    Util.setBackgroundTint(textView,R.color.tag_gray);
                    Util.setBackgroundTint(textView2,R.color.tag_green);
                    textView2.setText("已签收");
                }else if("1".equals(map.get("state"))){
                    Util.setBackgroundTint(textView,R.color.tag_gray);
                    Util.setBackgroundTint(textView2,R.color.tag_gray);
                    textView2.setText("未签收");
                }
                textView.setText(map.get("company"));


                return view;
            }
        });

        getInfo();
    }

    private void initView(JSONObject object){
        try {
            ((TextView)findViewById(R.id.textView45)).setText(object.getString("supervisionTitle"));
            ((TextView)findViewById(R.id.textView48)).setText(object.getString("supervisionNumber"));
            ((TextView)findViewById(R.id.textView57)).setText(object.getString("endTime"));
            ((TextView)findViewById(R.id.editText2)).setText(object.getString("overtimeInfo"));
            tagList.clear();
            JSONArray jsonArray=object.getJSONArray("supervUnits");
            for (int i = 0; i < jsonArray.length(); i++) {
                HashMap<String,String> map=new HashMap<>();
                map.put("company",jsonArray.getJSONObject(i).getString("company"));
                map.put("state",jsonArray.getJSONObject(i).getString("state"));
                tagList.add(map);
            }
            tagAdapter.notifyDataChanged();
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
                    initView((JSONObject) msg.obj);
                    break;
            }
        }
    };

    private void getInfo(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/app/talk/state/detail";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String,String> map=new HashMap<>();
                map.put("supervisionId",supervisionId+"");
                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("getInfo========="+jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code==1) {
                        JSONObject resultObj=jsonObj.getJSONObject("data");
                        handler.obtainMessage(1,resultObj).sendToTarget();
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
