package com.kaituocn.govstar.schedule;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.MyEntity;
import com.kaituocn.govstar.entity.ScheduleEntity;
import com.kaituocn.govstar.entity.TodoEntity;
import com.kaituocn.govstar.event.RefreshEvent;
import com.kaituocn.govstar.main.BaseAdapter;
import com.kaituocn.govstar.util.MyRefreshLayout;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleTodoFragment extends Fragment {


    TwinklingRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    BaseAdapter adapter;

    boolean initialized;

    List<TodoEntity> list=new ArrayList<>();

    public ScheduleTodoFragment() {
        // Required empty public constructor
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        adapter=new BaseAdapter(context, BaseAdapter.TYPE_SCHEDULE_TODO);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(RefreshEvent event) {
        if (event.getType()== RefreshEvent.Refresh_WorkList) {
            loadData();
        }

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_schedule_todo, container, false);
        recyclerView =view.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        refreshLayout=view.findViewById(R.id.refreshLayout);
        MyRefreshLayout.decorate(refreshLayout, new MyRefreshLayout.RefreshListener() {
            @Override
            public void refresh() {
                loadData();
            }
        });

        initData();
        return view;
    }

    private void initData(){
        if (!initialized) {
            initialized=true;
            refreshLayout.startRefresh();
        }
    }



    private void loadData(){
        getTodoList();
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

    private void getTodoList(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/AppSupervision/queryPendingTask";
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
                System.out.println("getTodoList=========="+jsonObj.toString());
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1) {
                        list.clear();
                        List<TodoEntity> list1=new Gson().fromJson((jsonObj.getJSONObject("data")).getString("PendingTask"), new TypeToken<List<TodoEntity>>(){}.getType());
                        list.addAll(list1);
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

    private void getTypeName(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/supervision/getSupervisionType";
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
                System.out.println("jsonObj========"+jsonObj);
                try {
                    Map<String,String> typeName=new HashMap<>();
                    JSONArray array=jsonObj.getJSONArray("data");
                    for (int i = 0; i < array.length(); i++) {
                        typeName.put(array.getJSONObject(i).getString("kay"),array.getJSONObject(i).getString("name"));
                    }
                    adapter.setScheduleTypeName(typeName);

                    System.out.println("typeName========"+typeName);
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
