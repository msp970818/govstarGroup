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
import com.kaituocn.govstar.entity.ScheduleEntity;
import com.kaituocn.govstar.event.RefreshEvent;
import com.kaituocn.govstar.main.BaseAdapter;
import com.kaituocn.govstar.util.MyRefreshLayout;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleDateFragment extends Fragment {

    TwinklingRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    BaseAdapter adapter;

    boolean initialized;

    List<ScheduleEntity> list;

    public ScheduleDateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        adapter=new BaseAdapter(context, BaseAdapter.TYPE_SCHEDULE_DATE);
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
        if (event.getType()== RefreshEvent.Refresh_MySchedule) {
            loadData();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
        getTodaySchedule();
    }


    private void getTodaySchedule(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/schedule/today";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String,String> map=new HashMap<>();
                map.put("time", Util.stempToStr(System.currentTimeMillis(),"yyyy-MM-dd"));
                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                refreshLayout.finishRefreshing();
                System.out.println("getTodaySchedule=============="+jsonObj);
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1) {
                        JSONArray array=jsonObj.getJSONArray("data");
                        if (array!=null&&array.length()>0) {
                            list=new Gson().fromJson((jsonObj.getJSONArray("data").getJSONObject(0)).getString("body"), new TypeToken<List<ScheduleEntity>>(){}.getType());
                            System.out.println("========="+list.size());
                            handler.sendEmptyMessage(1);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String error) {
                refreshLayout.finishRefreshing();
                Util.showToast(getContext(),getString(R.string.error_server));
            }
        });
    }


}
