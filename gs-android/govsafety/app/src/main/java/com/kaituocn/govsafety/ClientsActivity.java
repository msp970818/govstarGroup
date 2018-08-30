package com.kaituocn.govsafety;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.kaituocn.govsafety.entity.MyEntity;

import java.util.ArrayList;
import java.util.List;

public class ClientsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    BaseAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients);
        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("正在登录中的系统…");
        View backView=findViewById(R.id.action1View);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        adapter=new BaseAdapter(this, BaseAdapter.TYPE_CLIENT);
        recyclerView =findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        loadData();
    }

    private void loadData(){
        List<Object> list=new ArrayList<>();
        for (int i = 0; i <2 ; i++) {
            list.add(new MyEntity(i,"Name "+i));
        }
        adapter.setData(list,false);
        adapter.notifyDataSetChanged();


    }
}
