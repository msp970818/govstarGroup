package com.kaituocn.govstar.work.supervision;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.DuChaEntity;
import com.kaituocn.govstar.entity.MyEntity;
import com.kaituocn.govstar.main.BaseAdapter;
import com.kaituocn.govstar.util.Constant;
import com.kaituocn.govstar.util.MyRefreshLayout;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SupervisionActivity extends AppCompatActivity {

    TwinklingRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    BaseAdapter adapter;

    List<DuChaEntity> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_base_list_search);

        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("查看督查任务");
        View backView=findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (Constant.userEntity.getPowerMap().get("62")||Constant.userEntity.getPowerMap().get("6")){
            TextView actionView = findViewById(R.id.actionView);
            actionView.setVisibility(View.VISIBLE);
            actionView.setText("创建");
            actionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),CreateSupervisionActivity.class);
                    startActivityForResult(intent,1);
                }
            });
        }

        adapter=new BaseAdapter(this, BaseAdapter.TYPE_WROK_SUPERSIVION);
        recyclerView =findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        refreshLayout=findViewById(R.id.refreshLayout);
        MyRefreshLayout.decorate(refreshLayout, new MyRefreshLayout.RefreshListener() {
            @Override
            public void refresh() {
                loadData();
            }
        });

        refreshLayout.startRefresh();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK) {
            if (requestCode==1) {
                loadData();
            }
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1) {
                adapter.setData(list,false);
                adapter.notifyDataSetChanged();
            }
        }
    };

    private void loadData(){
        getSupervisionList();
    }

    private void getSupervisionList(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/AppSupervision/querySupervisionTask";
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
                refreshLayout.finishRefreshing();
                System.out.println("getSupervisionList======================"+jsonObj);
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1) {
                        list=new Gson().fromJson(jsonObj.getString("data"), new TypeToken<List<DuChaEntity>>(){}.getType());
                        handler.sendEmptyMessage(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                refreshLayout.finishRefreshing();
            }
        });
    }
}
