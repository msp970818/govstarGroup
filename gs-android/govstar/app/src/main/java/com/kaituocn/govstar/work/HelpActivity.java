package com.kaituocn.govstar.work;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.MyEntity;
import com.kaituocn.govstar.main.BaseAdapter;
import com.kaituocn.govstar.util.Util;

import java.util.ArrayList;
import java.util.List;

public class HelpActivity extends AppCompatActivity implements View.OnClickListener{

    RecyclerView recyclerView;
    BaseAdapter adapter;
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_help);
        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("帮扶留言");
        View backView=findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.topView).setOnClickListener(this);
        findViewById(R.id.bottomView).setOnClickListener(this);

        editText=findViewById(R.id.editText5);
        editText.postDelayed(new Runnable() {
            @Override
            public void run() {
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
            }
        },1000);

        recyclerView=findViewById(R.id.recyclerView);
        adapter=new BaseAdapter(this, BaseAdapter.TYPE_WORK_HELP);
        recyclerView.setAdapter(adapter);

        loadData();
    }

    private void loadData() {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            list.add(new MyEntity(i, "Name " + i));
        }
        adapter.setData(list, false);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.topView:
                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView,null,0);
                break;
            case R.id.bottomView:
                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView,null,adapter.getItemCount()-1);
                break;
        }
    }
}
