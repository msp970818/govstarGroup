package com.kaituocn.govstar.work.worklist;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.PersonEntity;
import com.kaituocn.govstar.entity.WorkFlowEntity;
import com.kaituocn.govstar.util.MyRefreshLayout;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;
import com.kaituocn.govstar.work.supervision.PersonChoseActivity;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LookProcessActivity extends AppCompatActivity implements View.OnClickListener{
    TwinklingRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    ProcessAdapter adapter;



    boolean showAction;
    int id;
    List<WorkFlowEntity> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_look_process);

        showAction = getIntent().getBooleanExtra("showAction", false);
        id = getIntent().getIntExtra("id", 0);

        TextView titleView = findViewById(R.id.titleView);
        titleView.setText("查看流程");
        View backView = findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.base_action_layout).setVisibility(View.GONE);


        if (showAction) {
            TextView actionView = findViewById(R.id.actionView);
            actionView.setVisibility(View.VISIBLE);
            actionView.setText("提交");
            actionView.setOnClickListener(this);

            findViewById(R.id.base_action_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.actionGroup1).setVisibility(View.GONE);
            findViewById(R.id.actionGroup2).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.actionTextView2)).setText("增加主责单位");
            ((TextView) findViewById(R.id.actionTextView3)).setText("增加协办单位");
            ((TextView) findViewById(R.id.actionTextView2)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_btn_add, 0, 0, 0);
            ((TextView) findViewById(R.id.actionTextView3)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_btn_add, 0, 0, 0);
            findViewById(R.id.actionView2).setOnClickListener(this);
            findViewById(R.id.actionView3).setOnClickListener(this);

        }


        adapter = new ProcessAdapter(this, ProcessAdapter.TYPE_WORK_PROCESS);
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
        Intent intent;
        switch (v.getId()) {
            case R.id.actionView:
                if (firstComp.isEmpty()&&secondComp.isEmpty()) {
                    Util.showToast(v.getContext(),"请添加主责单位或协办单位");
                }else{
                    submit();
                }
                break;
            case R.id.actionView2:
                intent =new Intent(v.getContext(),PersonChoseActivity.class);
                intent.putExtra("type",20);
                startActivityForResult(intent,1);
                break;
            case R.id.actionView3:
                intent =new Intent(v.getContext(),PersonChoseActivity.class);
                intent.putExtra("type",21);
                startActivityForResult(intent,2);
                break;
        }
    }

    List<Integer> firstComp=new ArrayList<>();
    List<Integer> secondComp=new ArrayList<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    List<PersonEntity> list = data.getParcelableArrayListExtra("entities");
                    firstComp.clear();
                    for (PersonEntity enity : list) {
                        firstComp.add(enity.getId());
                    }
                    break;
                case 2:
                    list = data.getParcelableArrayListExtra("entities");
                    secondComp.clear();
                    for (PersonEntity enity : list) {
                        secondComp.add(enity.getId());
                    }
                    break;
            }
        }
    }

    private void loadData() {
        getList();
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


    private void getList() {
        RequestUtil.jsonObjectRequest(new RequestListener() {
            @Override
            public String getUrl() {
                return "/supervision/queryWorkFlow";
            }

            @Override
            public Map<String, String> getParams() {
                return null;
            }

            @Override
            public JSONObject getJsonObj() {
                JSONObject object = new JSONObject();
                try {
                    object.putOpt("id", id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return object;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("queryWorkFlow==================" + jsonObj);
                refreshLayout.finishRefreshing();

                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
                        list.clear();
                        JSONArray jsonArray = jsonObj.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            List<WorkFlowEntity> listA = new Gson().fromJson(jsonObject.getString("info"), new TypeToken<List<WorkFlowEntity>>() {
                            }.getType());
                            for (WorkFlowEntity workFlowEntity : listA) {
                                workFlowEntity.setTitle(jsonObject.getString("title"));
                                workFlowEntity.setState(jsonObject.getString("state"));
                                workFlowEntity.setMyType(i + 1);
                            }
                            list.addAll(listA);
                        }
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

    Dialog dialog;
    private void submit(){
        dialog = Util.showLoadingDialog(this, "提交中…");
        RequestUtil.jsonObjectRequest(new RequestListener() {
            @Override
            public String getUrl() {
                return "/supervision/addResponsCompany";
            }

            @Override
            public Map<String, String> getParams() {
                return null;
            }

            @Override
            public JSONObject getJsonObj() {
                JSONObject object=new JSONObject();

                try {
                    JSONObject userList=new JSONObject();
                    JSONArray firstArray=new JSONArray();
                    for (Integer integer : firstComp) {
                        firstArray.put(integer);
                    }
                    JSONArray secondArray=new JSONArray();
                    for (Integer integer : secondComp) {
                        secondArray.put(integer);
                    }
                    userList.putOpt("firstComps",firstArray);
                    userList.putOpt("secondComps",secondArray);

                    object.putOpt("id",id);
                    object.putOpt("userList",userList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return object;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("addResponsCompany=============="+jsonObj);
                dialog.dismiss();
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
                        finish();
                    }else{
                        Util.showToast(getApplication(),jsonObj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                dialog.dismiss();
            }
        });
    }


}
