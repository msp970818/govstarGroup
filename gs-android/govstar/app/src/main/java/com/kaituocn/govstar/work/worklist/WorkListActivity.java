package com.kaituocn.govstar.work.worklist;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.DuChaEntity;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class WorkListActivity extends AppCompatActivity implements View.OnClickListener{

    TwinklingRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    BaseAdapter adapter;

    PopupWindow window;

    List<DuChaEntity> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_base_list_search);
        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("工作任务");
        View backView=findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView actionView=findViewById(R.id.action2View);
        actionView.setVisibility(View.GONE);
        actionView.setOnClickListener(this);

        EventBus.getDefault().register(this);

        adapter=new BaseAdapter(this, BaseAdapter.TYPE_WROK_WORKLIST);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action2View:
                showPopupWindow(v);
                break;
            case R.id.sortAView:
                if (window != null) {
                    window.dismiss();
                }
                break;
            case R.id.sortBView:
                if (window != null) {
                    window.dismiss();
                }
                break;
            case R.id.sortCView:
                if (window != null) {
                    window.dismiss();
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(RefreshEvent event) {
        if (event.getType()== RefreshEvent.Refresh_WorkList) {
            loadData();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private void showPopupWindow(View anchor) {
        View contentView = LayoutInflater.from(anchor.getContext()).inflate(R.layout.popup_sort, null, false);
        window = new PopupWindow(initPopupView(contentView), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setOutsideTouchable(true);
        window.setTouchable(true);
//        window.showAsDropDown(anchor,  0,0);
        window.showAtLocation(anchor, Gravity.TOP | Gravity.RIGHT, Util.dp2px(anchor.getContext(), 10), Util.dp2px(anchor.getContext(), 72));

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.6f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);

        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
            }
        });
    }

    private View initPopupView(View view) {
        TextView sortAView=view.findViewById(R.id.sortAView);
        sortAView.setText("仅显示进行中的事项");
        sortAView.setOnClickListener(this);
        TextView sortBView=view.findViewById(R.id.sortBView);
        sortBView.setText("仅显示已及处理的事项");
        sortBView.setOnClickListener(this);
        TextView sortCView=view.findViewById(R.id.sortCView);
        sortCView.setText("仅显示已完成的事项");
        sortCView.setOnClickListener(this);

        return view;
    }


    private void getList(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/AppSupervision/queryMyWorkTask";
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
                System.out.println("queryMyWorkTask==============="+jsonObj);
                refreshLayout.finishRefreshing();
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
