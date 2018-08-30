package com.kaituocn.govstar.work.supervisionnotice;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
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

public class SupervisionNoticeStatusActivity extends AppCompatActivity {

    TagFlowLayout tagFlowLayout;
    TagAdapter tagAdapter;
    List<HashMap<String,String>> tagList=new ArrayList<>();

    int supervisionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_supervision_notice_status);

        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("督办通知状态");
        View backView=findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        supervisionId=getIntent().getIntExtra("supervisionId",0);

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

        getDetail();
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

    private void getDetail(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/app/sup/state/detail";
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
                System.out.println("getDetail========="+jsonObj);
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
}
