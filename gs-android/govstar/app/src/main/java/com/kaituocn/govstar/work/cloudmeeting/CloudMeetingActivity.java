package com.kaituocn.govstar.work.cloudmeeting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.MeetingEntity;
import com.kaituocn.govstar.entity.MyEntity;
import com.kaituocn.govstar.main.BaseAdapter;
import com.kaituocn.govstar.util.Constant;
import com.kaituocn.govstar.util.MyRefreshLayout;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;
import com.kaituocn.govstar.yunav.Control;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CloudMeetingActivity extends AppCompatActivity {

    TwinklingRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    BaseAdapter adapter;
    EditText searchView;
    List<MeetingEntity> list;

    String meetingName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_base_list_search);
        TextView titleView = findViewById(R.id.titleView);
        titleView.setText("云会晤");
        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ArrayList<String> list = new ArrayList<>();
//                list.add("1");
//                list.add("217");
//                list.add("218");
//                list.add("220");
//                Control.createChatRoom(v.getContext(),list,"测试","11111","iconUrl");
            }
        });
        View backView = findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView actionView = findViewById(R.id.actionView);
        //44 发起会议权限
        if (Constant.userEntity.getPowerMap().containsKey("44")&&Constant.userEntity.getPowerMap().get("44")) {
            actionView.setVisibility(View.VISIBLE);
        }else{
            actionView.setVisibility(View.GONE);
        }

        actionView.setText("创建");
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),CreateMeetingActivity.class);
                startActivityForResult(intent,1);

            }
        });

        searchView=findViewById(R.id.searchView);
        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    Util.hideInput(v.getContext(),v);
                    meetingName=v.getText().toString().trim();
                    refreshLayout.startRefresh();
                    return true;
                }
                return false;
            }
        });

        adapter = new BaseAdapter(this, BaseAdapter.TYPE_WORK_CLOUDMEETING);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        refreshLayout = findViewById(R.id.refreshLayout);
        MyRefreshLayout.decorate(refreshLayout, new MyRefreshLayout.RefreshListener() {
            @Override
            public void refresh() {
                loadData();
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshLayout.startRefresh();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK) {

        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    adapter.setData(list, false);
                    adapter.notifyDataSetChanged();
                    break;
                case 2:
                    list=new ArrayList<>();
                    for (int i = 0; i <10 ; i++) {
                        list.add(new MeetingEntity());
                    }
                    adapter.setData(list, false);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private void loadData() {
        getMeetingList();
    }

    private void getMeetingList(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/CloudMetting/getAppMettingList";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String,String> map=new HashMap<>();
                map.put("mettingName",meetingName);
                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("getMeetingList====================="+jsonObj);
                refreshLayout.finishRefreshing();
                try {
                    int code = jsonObj.getInt("code");
                    if (code==1) {
                        list=new Gson().fromJson(jsonObj.getString("data"), new TypeToken<List<MeetingEntity>>(){}.getType());
                        for (MeetingEntity meetingEntity : list) {
                            if (meetingEntity.getMettingRoomId() != null) {
                                System.out.println("getMettingRoomId==============="+meetingEntity.getMettingRoomId());
                                MeetingEntity.Room room=new Gson().fromJson(meetingEntity.getMettingRoomId(),MeetingEntity.Room.class);
                                meetingEntity.setRoom(room);
                            }

                        }
                        handler.sendEmptyMessage(1);
                    }else {
                        Util.showToast(getApplication(),jsonObj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                refreshLayout.finishRefreshing();
                Util.showToast(getApplication(),getString(R.string.error_server));
//                handler.sendEmptyMessage(2);
            }
        });
    }
}
