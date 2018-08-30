package com.kaituocn.govstar.work.worklist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.FileEntity;
import com.kaituocn.govstar.entity.OptionEntity;
import com.kaituocn.govstar.entity.WorkPlanTagEntity;
import com.kaituocn.govstar.util.Constant;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;
import com.kaituocn.govstar.work.WebViewActivity;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkDoingActivity extends AppCompatActivity implements View.OnClickListener{

    TagFlowLayout tagFlowLayout1;
    TagFlowLayout tagFlowLayout2;
    TagFlowLayout tagFlowLayout3;
    TagAdapter tagAdapter1,tagAdapter2,tagAdapter3;

    View actionView;

    int id,taskId;
    int taskState;
    boolean isOver;

    JSONObject entity;

    Map<String,String> scheduleTypes=new HashMap<>();
    String supervisionType;

    List<WorkPlanTagEntity> tagList1=new ArrayList<>();
    List<FileEntity> tagList2=new ArrayList<>();
    List<FileEntity> tagList3=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_work_do);

        TextView titleView=findViewById(R.id.titleView);
        String title=getIntent().getStringExtra("title");
        if (TextUtils.isEmpty(title)) {
            titleView.setText("任务进行中");
        }else{
            titleView.setText(title);
        }
        final View backView=findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.bottomLayout).setVisibility(View.GONE);
        findViewById(R.id.group).setVisibility(View.GONE);
        actionView=findViewById(R.id.doingActionLayout);
        actionView.setVisibility(View.VISIBLE);
        actionView.setOnClickListener(this);

        id=getIntent().getIntExtra("id",0);
        taskId=getIntent().getIntExtra("taskId",0);
        isOver=getIntent().getBooleanExtra("isOver",false);
        System.out.println("============"+id+"   "+taskId);

        tagFlowLayout1=findViewById(R.id.frameLayout1);
        tagFlowLayout2=findViewById(R.id.frameLayout2);
        tagFlowLayout3=findViewById(R.id.frameLayout3);


        tagFlowLayout1.setAdapter(tagAdapter1=new TagAdapter<WorkPlanTagEntity>(tagList1) {
            @Override
            public View getView(FlowLayout parent, int position, final WorkPlanTagEntity entity) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag_2,parent, false);
                TextView textView=view.findViewById(R.id.textView);
                if (entity.getMyType()==1) {
                    Util.setBackgroundTint(textView,R.color.tag_red);
                }else{
                    Util.setBackgroundTint(textView,R.color.tag_green);
                }
                textView.setText(entity.getComName());

                TextView stateView=view.findViewById(R.id.textView2);
                Util.setBackgroundTint(stateView,R.color.tag_red);
                if (isOver) {
                    switch (entity.getIsRectification()) {
                        case "0":
                            stateView.setText("未提交");
                            Util.setBackgroundTint(stateView,R.color.tag_gray);
                            break;
                        case "1":
                            stateView.setText("已提交");
                            break;

                    }
                }else{
                    switch (entity.getWorkType()) {
                        case "1":
                            stateView.setText("工作计划");
                            break;
                        case "2":
                            stateView.setText("工作计划");
                            Util.setBackgroundTint(stateView,R.color.tag_gray);
                            break;
                        case "3":
                            stateView.setText("退办");
                            break;
                    }
                }
                stateView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isOver) {
                            if ("1".equals(entity.getIsRectification())){
                                Intent intent=new Intent(v.getContext(),WorkCorrectedActivity.class);
                                intent.putExtra("id",entity.getComId());
                                intent.putExtra("taskId",entity.getSupervisionId());
                                intent.putExtra("isDoing",true);
                                startActivity(intent);
                            }
                        }else{
                            if ("1".equals(entity.getWorkType())) {
                                Intent intent=new Intent(v.getContext(),WorkPlanActivity.class);
                                intent.putExtra("isDoing",true);
                                intent.putExtra("id",entity.getId());
                                startActivity(intent);

                            }else if("3".equals(entity.getWorkType())){
                                Intent intent=new Intent(v.getContext(),WorkReturnActivity.class);
                                intent.putExtra("isDoing",true);
                                intent.putExtra("id",entity.getId());
                                intent.putExtra("taskId",entity.getTaskId());
                                startActivityForResult(intent,1);
                            }
                        }
                    }
                });

                return view;
            }
        });

        tagFlowLayout2.setAdapter(tagAdapter2=new TagAdapter<FileEntity>(tagList2) {
            @Override
            public View getView(FlowLayout parent, int position, FileEntity entity) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag_annex,parent, false);
                TextView textView1=view.findViewById(R.id.textView1);
                TextView textView2=view.findViewById(R.id.textView2);
                textView1.setText("附件"+(position+1)+"：");
                textView2.setText(entity.getFilesTitle());


                return view;
            }
        });
        tagFlowLayout2.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                Intent intent = new Intent(view.getContext(), WebViewActivity.class);
                intent.putExtra("path", Util.getFileBaseUrl(getApplication()) + tagList2.get(position).getFilesUrl());
                startActivity(intent);
                return true;
            }
        });

        tagFlowLayout3.setAdapter(tagAdapter3=new TagAdapter<FileEntity>(tagList3) {
            @Override
            public View getView(FlowLayout parent, int position, FileEntity entity) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag_annex,parent, false);
                TextView textView1=view.findViewById(R.id.textView1);
                TextView textView2=view.findViewById(R.id.textView2);
                textView1.setText("附件"+(position+1)+"：");
                textView2.setText(entity.getFilesTitle());


                return view;
            }
        });
        tagFlowLayout3.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                Intent intent = new Intent(view.getContext(), WebViewActivity.class);
                intent.putExtra("path", Util.getFileBaseUrl(getApplication()) + tagList3.get(position).getFilesUrl());
                startActivity(intent);
                return true;
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        getDetailInfo();
        getFileList();
        getCompList();
    }

    private void initView(JSONObject object){
        try {

            taskState=object.getInt("taskState");

            ((TextView)findViewById(R.id.textView45)).setText(object.getString("supervisionTitle"));
            ((TextView)findViewById(R.id.textView48)).setText(object.getString("supervisionNumber"));
            JSONArray leaderAccs=object.getJSONObject("userListAPP").getJSONArray("leaderAccs");
            if (leaderAccs!=null&&leaderAccs.length()>0) {

                StringBuffer stringBuffer=new StringBuffer();
                for (int i = 0; i <leaderAccs.length()-1 ; i++) {
                    stringBuffer.append(leaderAccs.getString(i));
                    stringBuffer.append(",");
                }
                stringBuffer.append(leaderAccs.getString(leaderAccs.length()-1));
                ((TextView)findViewById(R.id.textView70)).setText(stringBuffer.toString());
            }

            ((TextView)findViewById(R.id.textView57)).setText(Util.stempToStr(object.getLong("endTime"),"yyyy-MM-dd"));
            ((TextView)findViewById(R.id.editText2)).setText(Html.fromHtml(object.getString("supervisionInfo")));

            supervisionType=object.getString("supervisionType");
            if (scheduleTypes.isEmpty()) {
                getScheduleTypes();
            }else{
                ((TextView)findViewById(R.id.textView69)).setText(scheduleTypes.get(supervisionType));
                System.out.println("scheduleTypes========"+scheduleTypes.toString()+"   "+scheduleTypes);
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.doingActionLayout:
                Intent intent=new Intent(v.getContext(),LookProcessActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("showAction",taskState==1||taskState==2);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK) {
            if (requestCode==1) {
                getCompList();
            }
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1) {
                initView(entity);
            }else if(msg.what==2){
                ((TextView)findViewById(R.id.textView69)).setText(scheduleTypes.get(supervisionType));
            }else if(msg.what==3){
                tagAdapter2.notifyDataChanged();
                tagAdapter3.notifyDataChanged();
            }
        }
    };

    private void getDetailInfo(){
        RequestUtil.jsonObjectRequest(new RequestListener() {
            @Override
            public String getUrl() {
                return "/supervision/queryRespTaskInfo";
            }

            @Override
            public Map<String, String> getParams() {
                return null;
            }

            @Override
            public JSONObject getJsonObj() {
                JSONObject object=new JSONObject();
                try {
                    object.putOpt("id",id);
                    object.putOpt("taskId",taskId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return object;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("getDetailInfo==============="+jsonObj);
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1) {
                        entity=jsonObj.getJSONObject("data");
                        handler.sendEmptyMessage(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private void getCompList(){
        RequestUtil.jsonObjectRequest(new RequestListener() {
            @Override
            public String getUrl() {
                if (isOver) {
                    return "/overtime/queryOvertimeCompany";
                }else{
                    return "/supervision/queryResponsCompany";
                }
            }

            @Override
            public Map<String, String> getParams() {
                return null;
            }

            @Override
            public JSONObject getJsonObj() {
                JSONObject object=new JSONObject();
                try {
                    object.putOpt("id",id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return object;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("getCompList================="+jsonObj);
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1){
                        tagList1.clear();
                        List<WorkPlanTagEntity> firstComps = new Gson().fromJson(jsonObj.getJSONObject("data").getString("firstComps"), new TypeToken<List<WorkPlanTagEntity>>() {}.getType());
                        if (firstComps.size()>0) {
                            for (WorkPlanTagEntity firstComp : firstComps) {
                                firstComp.setMyType(1);
                            }
                            tagList1.addAll(firstComps);
                        }

                        List<WorkPlanTagEntity> secondComps = new Gson().fromJson(jsonObj.getJSONObject("data").getString("secondComps"), new TypeToken<List<WorkPlanTagEntity>>() {}.getType());
                        if (secondComps.size()>0) {
                            tagList1.addAll(secondComps);
                        }

                        tagAdapter1.notifyDataChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private void getScheduleTypes(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/supervision/getSupervisionType";
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
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1) {
                        scheduleTypes.clear();
                        JSONArray array=jsonObj.getJSONArray("data");
                        for (int i = 0; i < array.length(); i++) {
                            scheduleTypes.put(array.getJSONObject(i).getString("kay"),array.getJSONObject(i).getString("name"));
                        }
                        handler.sendEmptyMessage(2);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private void getFileList(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/supervision/list/upload/files";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String,String> map=new HashMap<>();
                map.put("supervisionId",id+"");
                map.put("pageNum","1");
                map.put("pageSize","100");

                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("getFileList================"+jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
                        tagList2.clear();
                        tagList3.clear();

                        List<FileEntity> entities = new Gson().fromJson(jsonObj.getString("data"), new TypeToken<List<FileEntity>>() {}.getType());
                        if (entities.size() > 0) {
                            for (FileEntity fileEntity : entities) {
                                if ("1".equals(fileEntity.getAddOrUpdate())) {
                                    tagList2.add(fileEntity);
                                }else{
                                    tagList3.add(fileEntity);
                                }
                            }
                        }
                        handler.sendEmptyMessage(3);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }
}
