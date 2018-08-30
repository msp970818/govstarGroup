package com.kaituocn.govstar.work.schedulemanage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.PersonEntity;
import com.kaituocn.govstar.entity.PersonScheduleEntity;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;
import com.kaituocn.govstar.work.supervision.PersonChoseActivity;
import com.kaituocn.govstar.yunav.TeamAVChatProfile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleManageActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ScheduleAdapter adapter;

    List<PersonEntity> executors;
    TimePickerView pvTime;

    String curData;
    String accountIds="";

    List<PersonScheduleEntity> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_schedule_manage);
        TextView titleView = findViewById(R.id.titleView);
        titleView.setText("日程管理");

        View backView = findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView actionView = findViewById(R.id.actionView);
        actionView.setVisibility(View.VISIBLE);
        actionView.setText("新建");
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CreateScheduleActivity.class);
                startActivityForResult(intent,2);
            }
        });

        findViewById(R.id.actionGroup1).setVisibility(View.GONE);
        findViewById(R.id.actionGroup2).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.actionTextView2)).setText("显示人群");
        ((TextView) findViewById(R.id.actionTextView3)).setText("调整显示日期");
        ((TextView) findViewById(R.id.actionTextView2)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_btn_people, 0, 0, 0);
        ((TextView) findViewById(R.id.actionTextView3)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_btn_date, 0, 0, 0);
        findViewById(R.id.actionView2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PersonChoseActivity.class);
                intent.putExtra("type", 7);
                startActivityForResult(intent, 1);
            }
        });

        findViewById(R.id.actionView3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTimePicker();
            }
        });


        recyclerView = findViewById(R.id.recyclerView);
        adapter = new ScheduleAdapter(this, ScheduleAdapter.TYPE_A);
        recyclerView.setAdapter(adapter);

        initScheduleLayout();

        loadData();
    }


    private void initScheduleLayout() {
        LinearLayout titleLayout = findViewById(R.id.titleLayout);
        for (int i = 0; i < 24; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_panel_time, null);
            ((TextView) view.findViewById(R.id.textView)).setText((i < 10 ? "0" : "") + i + ":00");
            if(i>7&&i<18){
                ((TextView) view.findViewById(R.id.textView)).setTextColor(getResources().getColor(R.color.item_red));
            }else {
                ((TextView) view.findViewById(R.id.textView)).setTextColor(getResources().getColor(R.color.item_gray_dark));
            }
            titleLayout.addView(view);
        }
    }

    private void initTimePicker() {
        if (pvTime == null) {
            pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
                public void onTimeSelect(Date date, View v) {
                    curData = Util.getDateStr(date, "yyyy-MM-dd");
                    search();
                }
            }).setType(new boolean[]{true, true, true, false, false, false})
                    .setLabel("年", "月", "日", "时", "分", "秒")
//                .setCancelColor(Color.GRAY)
//                .setSubmitColor(getResources().getColor(R.color.gs_red))
//                .setSubCalSize(15)
                    .setLineSpacingMultiplier(3f)
                    .setContentTextSize(15)
                    .setDividerColor(0x00ffffff)
                    .setLayoutRes(R.layout.layout_custom_pickerview_time, new CustomListener() {
                        @Override
                        public void customLayout(View v) {
                            final TextView tvSubmit = v.findViewById(R.id.tv_finish);
                            TextView tvCancel = v.findViewById(R.id.tv_cancel);
                            tvSubmit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    pvTime.returnData();
                                    pvTime.dismiss();
                                }
                            });
                            tvCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    pvTime.dismiss();
                                }
                            });
                        }
                    })
                    .build();
        }

        Calendar calendar = Calendar.getInstance();
        if (curData != null) {
            Date date = new Date();
            date.setTime(Util.strToStemp(curData, "yyyy-MM-dd"));
            calendar.setTime(date);
        }

        pvTime.setDate(calendar);
        pvTime.show();
    }

    @SuppressLint("HandlerLeak")
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    adapter.setCurDate(curData!=null?curData:Util.stempToStr(System.currentTimeMillis(), "yyyy-MM-dd"));
                    adapter.setData(list, false);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    executors = data.getParcelableArrayListExtra("entities");
                    if (executors != null&&executors.size()>0) {
                        StringBuffer sb = new StringBuffer();
                        for (PersonEntity personEntity : executors) {
                            sb.append(personEntity.getId());
                            sb.append(",");
                        }
                        String string = sb.toString();
                        accountIds = string.substring(0, string.length() - 1);
                        System.out.println("accountIds============="+accountIds);
                        search();
                    }
                    break;
                case 2:
                    loadData();
                    break;
            }
        }
    }


    private void loadData() {

        String time = Util.stempToStr(System.currentTimeMillis(), "yyyy-MM-dd");
        getScheduleList(time);

    }



    private void getScheduleList(final String time) {
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/schedule/get/list";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("time", time);
                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("============" + jsonObj);
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1) {
                        list=new Gson().fromJson(jsonObj.getString("data"), new TypeToken<List<PersonScheduleEntity>>(){}.getType());
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

    private void search() {
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/schedule/accountIds";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("accountIds", TextUtils.isEmpty(accountIds)? TeamAVChatProfile.getUserId():accountIds);
                params.put("time", curData!=null?curData:Util.stempToStr(System.currentTimeMillis(), "yyyy-MM-dd"));
                return params;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("search========="+jsonObj);
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1) {
                        list=new Gson().fromJson(jsonObj.getString("data"), new TypeToken<List<PersonScheduleEntity>>(){}.getType());
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
