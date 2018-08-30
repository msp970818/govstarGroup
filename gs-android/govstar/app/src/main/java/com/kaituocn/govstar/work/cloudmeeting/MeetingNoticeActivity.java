package com.kaituocn.govstar.work.cloudmeeting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.MyEntity;
import com.kaituocn.govstar.entity.NoticePersonEntity;
import com.kaituocn.govstar.entity.PersonEntity;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;
import com.kaituocn.govstar.work.supervision.PersonChoseActivity;
import com.kaituocn.govstar.work.worklist.ProcessAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeetingNoticeActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProcessAdapter adapter;

    int type;
    String meetingId;
    int meetingType;
    String addPersonsId;

    List<NoticePersonEntity> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_meeting_notice);

        type = getIntent().getIntExtra("type", 0);
        meetingId = getIntent().getStringExtra("meetingId");
        meetingType = getIntent().getIntExtra("meetingType",0);

        TextView titleView = findViewById(R.id.titleView);
        titleView.setText(getIntent().getStringExtra("titleName"));
        View backView = findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (type == 1) {
            ((TextView) findViewById(R.id.actionTextView1)).setText("增加参会人");
            ((TextView) findViewById(R.id.actionTextView1)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_btn_add, 0, 0, 0);
            findViewById(R.id.actionView1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), PersonChoseActivity.class);
                    intent.putExtra("type", 24);
                    intent.putExtra("meetingId", meetingId);
                    startActivityForResult(intent, 1);
                }
            });

            adapter = new ProcessAdapter(this, ProcessAdapter.TYPE_CLOUD_MEETING_NOTICE);
            adapter.setMeetingId(meetingId);
            adapter.setMeetingType(meetingType);
        }

        if (type == 2) {
            findViewById(R.id.bottomLayout).setVisibility(View.GONE);
            adapter = new ProcessAdapter(this, ProcessAdapter.TYPE_CLOUD_MEETING_SIGN);
            adapter.setMeetingId(meetingId);
            adapter.setMeetingType(meetingType);
        }


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);

        loadData(type);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                List<PersonEntity> list = data.getParcelableArrayListExtra("entities");
                addPersonsId = Util.getPersonIds(list);
                addPersons();
            }
        }
    }


    @SuppressLint("HandlerLeak")
    Handler handler=new Handler(){
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

    @Override
    protected void onDestroy() {
        setResult(RESULT_OK);
        super.onDestroy();
    }

    private void loadData(int type) {
        switch (type) {
            case 1:
                getNoticeList();
                break;
            case 2:
                getSignUpList();
                break;
        }
//        List<Object> list = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            MyEntity entity = new MyEntity(i, "Name " + i);
//            entity.setMyType(i % 4 == 0 ? 1 : 2);
//            list.add(entity);
//        }
//        adapter.setData(list, false);
//        adapter.notifyDataSetChanged();

    }

    private void getNoticeList() {
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/CloudMetting/noticeAppInfo";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("mettingId", meetingId);
                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("getNoticeList===================" + jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
                        list.clear();
                        NoticePersonEntity entity=new Gson().fromJson(jsonObj.getString("createPersonInfo"),NoticePersonEntity.class);
                        entity.setMyType(1);
                        list.add(entity);

                        List<NoticePersonEntity> listA = new Gson().fromJson(jsonObj.getString("joinPersonInfo"), new TypeToken<List<NoticePersonEntity>>() {}.getType());
                        list.addAll(listA);

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

    private void addPersons() {
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/CloudMetting/addInviter";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("personId", addPersonsId);
                map.put("mettingId", meetingId);
                System.out.println("addPersons map========"+map.toString());
                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("addPersons============" + jsonObj);
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1) {
                        getNoticeList();
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

    private void getSignUpList() {
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/CloudMetting/getAppSingInfo";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("mettingId", meetingId);
                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("getSignUpList===================" + jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
                        list.clear();
                        NoticePersonEntity entity=new Gson().fromJson(jsonObj.getString("createPersonInfo"),NoticePersonEntity.class);
                        entity.setMyType(1);
                        list.add(entity);

                        List<NoticePersonEntity> listA = new Gson().fromJson(jsonObj.getString("joinPersonInfo"), new TypeToken<List<NoticePersonEntity>>() {}.getType());
                        list.addAll(listA);

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
}
