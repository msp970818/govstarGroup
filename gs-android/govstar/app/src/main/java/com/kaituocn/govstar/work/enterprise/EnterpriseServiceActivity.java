package com.kaituocn.govstar.work.enterprise;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.MyEntity;
import com.kaituocn.govstar.main.BaseAdapter;
import com.kaituocn.govstar.util.MyRefreshLayout;
import com.kaituocn.govstar.util.Util;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class EnterpriseServiceActivity extends AppCompatActivity implements View.OnClickListener{

    TwinklingRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    BaseAdapter adapter;

    PopupWindow window;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_base_list_search);
        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("企业服务平台");
        View backView=findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView actionView=findViewById(R.id.action2View);
        actionView.setVisibility(View.VISIBLE);
        actionView.setOnClickListener(this);

        adapter=new BaseAdapter(this, BaseAdapter.TYPE_WORK_ENTERPRISE);
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
//        loadData();
    }

    private void loadData(){
        List<Object> list=new ArrayList<>();
        for (int i = 0; i <10 ; i++) {
            list.add(new MyEntity(i,"Name "+i));
        }
        adapter.setData(list,false);
        adapter.notifyDataSetChanged();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.finishRefreshing();
            }
        },2000);

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
}
