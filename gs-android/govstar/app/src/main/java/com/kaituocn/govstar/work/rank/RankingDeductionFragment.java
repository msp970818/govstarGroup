package com.kaituocn.govstar.work.rank;


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
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.MyEntity;
import com.kaituocn.govstar.entity.RankingDeductEntity;
import com.kaituocn.govstar.entity.RankingPersonEntity;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class RankingDeductionFragment extends Fragment {

    TwinklingRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    BaseAdapter adapter;
    boolean initialized;

    TextView yearView,scoreView;

    List<RankingDeductEntity> list;
    public RankingDeductionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        adapter=new BaseAdapter(context, BaseAdapter.TYPE_WORK_RANKING_REDUCTION);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_ranking_deduction, container, false);
        yearView=view.findViewById(R.id.yearView);
        scoreView=view.findViewById(R.id.scoreView);
        yearView.setText(((RankingActivity)getActivity()).getCurYear());
        scoreView.setText(" ");
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

    void loadData(){
        getList();
    }


    @SuppressLint("HandlerLeak")
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (!list.isEmpty()) {
                        scoreView.setText(list.get(0).getTotalScore());
                    }else{
                        scoreView.setText(" ");
                    }
                    yearView.setText(((RankingActivity)getActivity()).getCurYear());
                    adapter.setData(list, false);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    private void getList(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/Score/ownDeduct";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String,String> map=new HashMap<>();
                map.put("annual",((RankingActivity)getActivity()).getCurYear());
                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                refreshLayout.finishRefreshing();
                System.out.println("ownDeduct============"+jsonObj);
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1) {
                        list=new Gson().fromJson(jsonObj.getString("data"), new TypeToken<List<RankingDeductEntity>>(){}.getType());
                        handler.sendEmptyMessage(1);
                    }else {
                        Util.showToast(getContext(),jsonObj.getString("message"));
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
