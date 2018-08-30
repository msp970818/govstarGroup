package com.kaituocn.govstar.set;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.DuChaEntity;
import com.kaituocn.govstar.entity.FeedbackInfoEntity;
import com.kaituocn.govstar.main.BaseAdapter;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedbackInfoActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    BaseAdapter adapter;

    int id;
    Button closeBtn,sendBtn;
    EditText editText;

    List<FeedbackInfoEntity> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_feedback_info);

        id=getIntent().getIntExtra("id",0);
        System.out.println("id==========="+id);
        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("反馈信息");
        View backView=findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView=findViewById(R.id.recyclerView);
        adapter=new BaseAdapter(this, BaseAdapter.TYPE_SET_SERVICE_FEEDBACK_INFO);
        recyclerView.setAdapter(adapter);

        editText=findViewById(R.id.editText);
        closeBtn=findViewById(R.id.button1);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFeeback();
            }
        });
        sendBtn=findViewById(R.id.button2);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info=editText.getText().toString().trim();
                if (TextUtils.isEmpty(info)) {
                    Util.showToast(v.getContext(),"请输入反馈信息");
                }else {
                    sendFeedbck();
                }
            }
        });

        getFeedbackInfoList(id);

    }

    @SuppressLint("HandlerLeak")
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1) {
                adapter.setData(list,false);
                adapter.notifyDataSetChanged();

                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView,null,adapter.getItemCount()-1);
                    }
                });

            }
        }
    };

    private void getFeedbackInfoList(final int id){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/UserFeedback/feedback/info";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> map =new HashMap<>();
                map.put("id",id+"");
                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("getFeedbackInfoList=================="+jsonObj);
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1) {
                        list=new Gson().fromJson(jsonObj.getString("data"), new TypeToken<List<FeedbackInfoEntity>>(){}.getType());
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


    private void sendFeedbck(){
        RequestUtil.jsonObjectRequest(new RequestListener() {
            @Override
            public String getUrl() {
                return "/UserFeedback/commit/first";
            }

            @Override
            public Map<String, String> getParams() {
                return null;
            }

            @Override
            public JSONObject getJsonObj() {
                List<Object> list= adapter.getList();
                FeedbackInfoEntity entity= (FeedbackInfoEntity) list.get(list.size()-1);
                JSONObject object=new JSONObject();
                try {
                    object.putOpt("question",editText.getText().toString().trim());
                    object.putOpt("type",entity.getType());
                    object.putOpt("level",entity.getLevel());
                    object.putOpt("parentId",entity.getId());
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
                        editText.setText("");
                        getFeedbackInfoList(id);
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

    private void closeFeeback(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/UserFeedback/close";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> map=new HashMap<>();
                map.put("id",id+"");
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
