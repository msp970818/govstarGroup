package com.kaituocn.govstar.work.visibledata;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.MyEntity;
import com.kaituocn.govstar.entity.VisibleDataEntity;
import com.kaituocn.govstar.main.BaseAdapter;
import com.kaituocn.govstar.util.MyRefreshLayout;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountDataActivity extends AppCompatActivity {

    TwinklingRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    BaseAdapter adapter;

    String curYear;
    String state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_look_process);

        curYear=getIntent().getStringExtra("curYear");
        state=getIntent().getStringExtra("state");

        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("统计数据");
        View backView=findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.base_action_layout).setVisibility(View.GONE);

        adapter=new BaseAdapter(this, BaseAdapter.TYPE_WORK_VISIBLEDATA_COUNTDATA);
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

    private void loadData(){

        getList();
    }

    private void getList(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/viewData/getStatisticalDetails";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> map=new HashMap<>();
                map.put("state",state);
                map.put("annual",curYear);
                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("getList======================"+jsonObj);
                refreshLayout.finishRefreshing();
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1) {
                        VisibleDataEntity entity=new Gson().fromJson(jsonObj.getString("data"),VisibleDataEntity.class);
                        List<Object> list=new ArrayList<>();
                        list.add(entity);
                        adapter.setData(list,false);
                        adapter.notifyDataSetChanged();
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
