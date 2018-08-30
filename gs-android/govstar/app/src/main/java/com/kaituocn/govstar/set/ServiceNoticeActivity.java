package com.kaituocn.govstar.set;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.DuChaEntity;
import com.kaituocn.govstar.entity.MyEntity;
import com.kaituocn.govstar.entity.SysNoticeEntity;
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

public class ServiceNoticeActivity extends AppCompatActivity {

    TwinklingRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    BaseAdapter adapter;

    EditText searchView;
    List<SysNoticeEntity> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_base_list_search);

        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("系统通知");
        View backView=findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchView=findViewById(R.id.searchView);
        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    Util.hideInput(v.getContext(),v);
                    refreshLayout.startRefresh();
                    return true;
                }
                return false;
            }
        });

        adapter=new BaseAdapter(this, BaseAdapter.TYPE_SET_SERVICE_NOTICE);
        recyclerView =findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        refreshLayout=findViewById(R.id.refreshLayout);
        MyRefreshLayout.decorate(refreshLayout, new MyRefreshLayout.RefreshListener() {
            @Override
            public void refresh() {
                Util.hideInput(getApplication(),refreshLayout);
                getList();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshLayout.startRefresh();
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


    private void getList(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/TheLog/getAppPushLogList";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> map=new HashMap<>();
                map.put("title",searchView.getText().toString().trim());
                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                JSONObject object=new JSONObject();
                try {
                    object.putOpt("title",searchView.getText().toString().trim());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return object;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("getList==============="+jsonObj);
                refreshLayout.finishRefreshing();
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1) {
                        list=new Gson().fromJson(jsonObj.getString("data"), new TypeToken<List<SysNoticeEntity>>(){}.getType());
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
