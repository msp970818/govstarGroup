package com.kaituocn.govsafety;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaituocn.govsafety.entity.DeviceEntity;
import com.kaituocn.govsafety.entity.MyEntity;
import com.kaituocn.govsafety.util.RequestListener;
import com.kaituocn.govsafety.util.RequestUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    BaseAdapter adapter;
    List<DeviceEntity> list=new ArrayList<>();
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
                setResult(RESULT_OK);
                finish();
            }
        });

        adapter=new BaseAdapter(this, BaseAdapter.TYPE_CLIENT);
        recyclerView =findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        loadData();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @SuppressLint("HandlerLeak")
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1) {
//                addTestData();
                adapter.setData(list,false);
                adapter.notifyDataSetChanged();
            }
        }
    };
    private void loadData(){
        getLoginDevicesInfo();
    }

    private void addTestData(){
        for (int i = 0; i <4 ; i++) {
            DeviceEntity entity=new DeviceEntity();
            if(i<2){
                entity.setMyType(1);
            }

            list.add(entity);
        }
    }


    private void getLoginDevicesInfo(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/GsSafe/checkSystem";
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
                System.out.println("checkSystem================="+jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1){

                        list.clear();
                        List<DeviceEntity> listA=new Gson().fromJson(jsonObj.getJSONObject("data").getString("mobileInformations"), new TypeToken<List<DeviceEntity>>(){}.getType());
                        for (DeviceEntity deviceEntity : listA) {
                            deviceEntity.setMyType(1);
                        }
                        List<DeviceEntity> listB=new Gson().fromJson(jsonObj.getJSONObject("data").getString("pcInformations"), new TypeToken<List<DeviceEntity>>(){}.getType());
                        list.addAll(listA);
                        list.addAll(listB);

                        handler.sendEmptyMessage(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
