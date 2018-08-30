package com.kaituocn.govstar.work.supervision;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.LevelUnitEntity;
import com.kaituocn.govstar.entity.LevelUnitTagEntity;
import com.kaituocn.govstar.entity.PersonEntity;
import com.kaituocn.govstar.util.MyRefreshLayout;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonChoseActivity extends AppCompatActivity {

    TwinklingRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    PersonChoseAdapter adapter;
    int adapterType;
    int type;
    String meetingId = "";
    String url;

    EditText searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_base_list_search);
        TextView titleView = findViewById(R.id.titleView);

        type = getIntent().getIntExtra("type", 0);
        meetingId = getIntent().getStringExtra("meetingId");

        if (type == 0) {
            titleView.setText("负责领导");
            adapterType = PersonChoseAdapter.TYPE_SCHEDULE_PERSON;
        } else if (type == 1) {
            titleView.setText("主责单位");
            adapterType = PersonChoseAdapter.TYPE_WORK_CREATESUPERVISION_UNIT;
        } else if (type == 2) {
            titleView.setText("配合单位");
            adapterType = PersonChoseAdapter.TYPE_WORK_CREATESUPERVISION_UNIT;
        } else if (type == 5) {//新建日程-执行人
            titleView.setText("选择执行人");
            url = "/schedule/getReplaceUserList";
            adapterType = PersonChoseAdapter.TYPE_SCHEDULE_PERSON;
        } else if (type == 7) {//日程管理-显示人群
            titleView.setText("选择执行人");
            url = "/schedule/getReplaceUserList/understrapper";
            adapterType = PersonChoseAdapter.TYPE_SCHEDULE_PERSON;
        } else if (type == 8) {//任务办理-转发人员列表
            titleView.setText("选择转发人");
            url = "/AccountManage/getCompanyForwardUserList";
            adapterType = PersonChoseAdapter.TYPE_SCHEDULE_PERSON;
        }else if (type == 9) {//创建督查-负责领导
            url = "/AccountManage/getPresideLead";
            adapterType = PersonChoseAdapter.TYPE_SCHEDULE_PERSON;
        }else if (type == 10) {//工作计划-主责人
            url = "/Company/getDepDeputyLeadList";
            adapterType = PersonChoseAdapter.TYPE_SCHEDULE_PERSON;
        }else if (type == 11) {//工作计划-负责部门
            url = "/Company/getDepartmentListByUserId";
            adapterType = PersonChoseAdapter.TYPE_WORK_CREATESUPERVISION_UNIT;
        }else if (type == 20) {//添加主责单位
            titleView.setText("添加主责单位");
            url = "/Loading/getCompanyList";
            adapterType = PersonChoseAdapter.TYPE_LEVEL_UNIT_TAG;
        }else if (type == 21) {//添加协办单位
            titleView.setText("添加协办单位");
            url = "/Loading/getSecondCompsList";
            adapterType = PersonChoseAdapter.TYPE_LEVEL_UNIT_TAG;
        } else if (type == 22) {//创建会议-参会人列表
            titleView.setText("参会人");
            url = "/CloudMetting/getInvitePersonList";
            adapterType = PersonChoseAdapter.TYPE_LEVEL_UNIT_TAG;
        } else if (type == 23) {//会议报名-选择代理人
            titleView.setText("选择代理人");
            url = "/CloudMetting/getAppReplacePerson";
            adapterType = PersonChoseAdapter.TYPE_LEVEL_UNIT_TAG;
        }else if (type == 24) {//会议通知中-增加参会人
            titleView.setText("增加参会人");
            url = "/CloudMetting/getAddInvite";
            adapterType = PersonChoseAdapter.TYPE_LEVEL_UNIT_TAG;
        }
        String title = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(title)) {
            titleView.setText(title);
        }

        TextView actionView = findViewById(R.id.actionView);
        actionView.setVisibility(View.VISIBLE);
        actionView.setText("确定");
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 4 && adapter.getEntityList().size() > 1) {
                    Util.showToast(v.getContext(), "代理人只能选择 1 位");
                }else if(type == 10 && adapter.getEntityList().size() > 1){
                    Util.showToast(v.getContext(), "主责人只能选择 1 位");
                }else if(type == 11 && adapter.getEntityList().size() > 1){
                    Util.showToast(v.getContext(), "负责部门只能选择 1 个");
                }
                else {
                    Intent intent = new Intent();
                    intent.putParcelableArrayListExtra("entities", adapter.getEntityList());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        View backView = findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchView=findViewById(R.id.searchView);
        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    Util.hideInput(v.getContext(),v);
                    String searchStr=v.getText().toString().trim();
                    adapter.searchData(searchStr);
                    return true;
                }
                return false;
            }
        });

        adapter = new PersonChoseAdapter(this, adapterType);
        adapter.setResultIds(getIntent().getIntegerArrayListExtra("resultIds"));

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        refreshLayout = findViewById(R.id.refreshLayout);
        MyRefreshLayout.decorate(refreshLayout, new MyRefreshLayout.RefreshListener() {
            @Override
            public void refresh() {
                searchView.setText("");
                loadData(type, url);
            }
        });

        refreshLayout.startRefresh();
    }

    private void loadData(int type, String url) {
        switch (type) {

            case 10://工作计划-主责人
            case 11://工作计划-负责部门
                getPersonList(url);
                break;
            case 5://新建日程-执行人
            case 7://日程管理-显示人群
                getExecutorList(url);
                break;
            case 8://任务办理-转发人员列表
            case 9://创建督查-负责领导
                getList(type,url);
                break;
            case 20:////添加主责单位
            case 21:////添加协办单位
            case 22://创建会议-参会人列表
            case 23://会议报名-选择代理人
            case 24://会议通知中-增加参会人
                getLevelTagList(type,url);
                break;
            default:
                loadData();
                break;
        }
    }

    private void getPersonList(final String url) {
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return url;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("mettingId", meetingId == null ? "" : meetingId);
                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                refreshLayout.finishRefreshing();
                System.out.println("getPersonList=============" + jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
                        List<PersonEntity> list = new Gson().fromJson(jsonObj.getString("data"), new TypeToken<List<PersonEntity>>() {
                        }.getType());
                        if (type==10){
                            JSONArray array=jsonObj.getJSONArray("data");
                            for (int i = 0; i <array.length() ; i++) {
                                list.get(i).setName(array.getJSONObject(i).getString("label"));
                            }
                        }
                        adapter.setData(list, false);
                        adapter.notifyDataSetChanged();
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

    private void getExecutorList(final String url) {
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return url;
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
                System.out.println("==============" + jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
                        List<PersonEntity> list = new Gson().fromJson(jsonObj.getJSONObject("data").getString("ReplaceUserList"), new TypeToken<List<PersonEntity>>() {
                        }.getType());
                        adapter.setData(list, false);
                        adapter.notifyDataSetChanged();
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

    private void getList(final int type, final String url) {
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return url;
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
                System.out.println("getList==============" + jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
//                       list = new Gson().fromJson(jsonObj.getJSONObject("data").getString("ReplaceUserList"), new TypeToken<List<PersonEntity>>() {}.getType());

                        List<PersonEntity> list=new ArrayList<>();

                        JSONArray jsonArray=jsonObj.getJSONObject("data").getJSONArray("allleader");
                        for (int i = 0; i <jsonArray.length() ; i++) {
                            PersonEntity entity=new PersonEntity();
                            entity.setId(jsonArray.getJSONObject(i).getInt("id"));
                            entity.setName(jsonArray.getJSONObject(i).getString("label"));
                            list.add(entity);
                        }

                        adapter.setData(list, false);
                        adapter.notifyDataSetChanged();
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

    private void getLevelList(final int type, final String url){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return url;
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
                System.out.println("jsonObj======================"+jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1){
                        List<LevelUnitEntity> list=new ArrayList<>();
                        if (type==20||type==21) {
                            JSONArray jsonArray=jsonObj.getJSONObject("data").getJSONArray("companyList");
                            for (int i = 0; i <jsonArray.length() ; i++) {
                                int topId=jsonArray.getJSONObject(i).getInt("id");
                                String topName=jsonArray.getJSONObject(i).getString("label");
                                JSONArray array=jsonArray.getJSONObject(i).getJSONArray("children");
                                for (int j = 0; j < array.length(); j++) {
                                    LevelUnitEntity enity=new LevelUnitEntity();
                                    enity.setTopName(topName);
                                    enity.setTopId(topId);
                                    enity.setId(array.getJSONObject(j).getInt("id"));
                                    enity.setName(array.getJSONObject(j).getString("label"));
                                    list.add(enity);
                                }

                            }
                        }else if(type==22){
                            JSONArray jsonArray=jsonObj.getJSONArray("data");
                            for (int i = 0; i <jsonArray.length() ; i++) {
                                int topId=jsonArray.getJSONObject(i).getInt("deptId");
                                String topName=jsonArray.getJSONObject(i).getString("deptName");
                                JSONArray array=jsonArray.getJSONObject(i).getJSONArray("user");
                                for (int j = 0; j < array.length(); j++) {
                                    LevelUnitEntity enity=new LevelUnitEntity();
                                    enity.setTopName(topName);
                                    enity.setTopId(topId);
                                    enity.setId(array.getJSONObject(j).getInt("id"));
                                    enity.setName(array.getJSONObject(j).getString("name"));
                                    enity.setAvatarUrl(array.getJSONObject(j).getString("avatarUrl"));
                                    list.add(enity);
                                }

                            }
                            adapter.setPerson(true);
                        }
                        adapter.setData(list, false);
                        adapter.notifyDataSetChanged();
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

    private void getLevelTagList(final int type, final String url){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return url;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("mettingId", meetingId == null ? "" : meetingId);
                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                refreshLayout.finishRefreshing();
                System.out.println("jsonObj======================"+jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1){
                        List<LevelUnitTagEntity> list=new ArrayList<>();
                        if (type==20||type==21) {
                            JSONArray jsonArray=jsonObj.getJSONObject("data").getJSONArray("companyList");

                            for (int i = 0; i <jsonArray.length() ; i++) {
                                int id=jsonArray.getJSONObject(i).getInt("id");
                                String name=jsonArray.getJSONObject(i).getString("label");
                                LevelUnitTagEntity enity=new LevelUnitTagEntity();
                                enity.setId(id);
                                enity.setName(name);
                                List<LevelUnitTagEntity.ChildItem> tags=new ArrayList<>();
                                enity.setChildren(tags);
                                JSONArray array=jsonArray.getJSONObject(i).getJSONArray("children");
                                for (int j = 0; j < array.length(); j++) {
                                    LevelUnitTagEntity.ChildItem child=new LevelUnitTagEntity.ChildItem();
                                    child.setId(array.getJSONObject(j).getInt("id"));
                                    child.setName(array.getJSONObject(j).getString("label"));
                                    tags.add(child);
                                }
                                list.add(enity);
                            }
                        }else if(type==22||type==23||type==24){
                            JSONArray jsonArray=jsonObj.getJSONArray("data");
                            for (int i = 0; i <jsonArray.length() ; i++) {
                                int id=jsonArray.getJSONObject(i).getInt("deptId");
                                String name=jsonArray.getJSONObject(i).getString("deptName");
                                LevelUnitTagEntity enity=new LevelUnitTagEntity();
                                enity.setId(id);
                                enity.setName(name);
                                List<LevelUnitTagEntity.ChildItem> tags=new ArrayList<>();
                                enity.setChildren(tags);
                                JSONArray array=jsonArray.getJSONObject(i).getJSONArray("user");
                                for (int j = 0; j < array.length(); j++) {
                                    LevelUnitTagEntity.ChildItem child=new LevelUnitTagEntity.ChildItem();
                                    child.setId(array.getJSONObject(j).getInt("id"));
                                    child.setName(array.getJSONObject(j).getString("name"));
                                    child.setAvatarUrl(array.getJSONObject(j).getString("avatarUrl"));
                                    tags.add(child);
                                }
                                list.add(enity);
                            }
                            adapter.setPerson(true);
                        }
                        adapter.setData(list, false);
                        adapter.notifyDataSetChanged();
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


    private void loadData() {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            PersonEntity entity = new PersonEntity();
            entity.setId(i);
            entity.setName("name " + i);
            list.add(entity);
        }
        adapter.setData(list, false);
        adapter.notifyDataSetChanged();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.finishRefreshing();
            }
        }, 2000);

    }
}
