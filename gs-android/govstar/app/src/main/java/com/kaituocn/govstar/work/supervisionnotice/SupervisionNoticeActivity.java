package com.kaituocn.govstar.work.supervisionnotice;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.DuBanEntity;
import com.kaituocn.govstar.entity.MeetingEntity;
import com.kaituocn.govstar.entity.MyEntity;
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

public class SupervisionNoticeActivity extends AppCompatActivity implements View.OnClickListener {

    TwinklingRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    BaseAdapter adapter;
    EditText searchView;
    PopupWindow window;

    List<DuBanEntity> list;
    String supervisionTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_base_list_search);
        TextView titleView = findViewById(R.id.titleView);
        titleView.setText("督办通知");
        View backView = findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView actionView = findViewById(R.id.action2View);
        actionView.setVisibility(View.GONE);
        actionView.setOnClickListener(this);

        searchView=findViewById(R.id.searchView);
        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    Util.hideInput(v.getContext(),v);
                    supervisionTitle=v.getText().toString().trim();
                    refreshLayout.startRefresh();
                    return true;
                }
                return false;
            }
        });

        adapter = new BaseAdapter(this, BaseAdapter.TYPE_WROK_SUPERSIVIONNOTICE);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        refreshLayout = findViewById(R.id.refreshLayout);
        MyRefreshLayout.decorate(refreshLayout, new MyRefreshLayout.RefreshListener() {
            @Override
            public void refresh() {
                loadData();
            }
        });

        refreshLayout.startRefresh();
    }

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
        TextView sortAView = view.findViewById(R.id.sortAView);
        sortAView.setText("仅显示可督办事项");
        sortAView.setOnClickListener(this);
        TextView sortBView = view.findViewById(R.id.sortBView);
        sortBView.setText("仅显示进行中的事项");
        sortBView.setOnClickListener(this);
        TextView sortCView = view.findViewById(R.id.sortCView);
        sortCView.setText("仅显示已完成的事项");
        sortCView.setOnClickListener(this);

        return view;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    refreshLayout.startRefresh();
                    break;
            }
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    adapter.setData(list, false);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private void loadData() {
        getSupervisionList();

    }

    private void getSupervisionList() {
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/app/list/sup";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("supervisionTitle", supervisionTitle);
                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                refreshLayout.finishRefreshing();
                System.out.println("getSupervisionList===========" + jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
                        list = new Gson().fromJson(jsonObj.getString("data"), new TypeToken<List<DuBanEntity>>() {
                        }.getType());
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
