package com.kaituocn.govstar.work.schedulemanage;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.OptionEntity;
import com.kaituocn.govstar.entity.PersonEntity;
import com.kaituocn.govstar.entity.ScheduleEntity;
import com.kaituocn.govstar.event.RefreshEvent;
import com.kaituocn.govstar.util.CalendarReminderUtils;
import com.kaituocn.govstar.util.Constant;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;
import com.kaituocn.govstar.work.supervision.PersonChoseActivity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CreateScheduleActivity extends AppCompatActivity implements View.OnClickListener {

    List<PersonEntity> executors;
    List<OptionEntity> importanceList;
    List<OptionEntity> typeList;

    List<String> importanceOptions;
    List<String> typeOptions;

    TextView executorView;
    TextView importanceView;
    TextView scheduleTypeView;
    TextView startTime;
    TextView endTime;
    TextView numberView;

    EditText scheduleTitleView;
    EditText scheduleInfoView;

    TimePickerView pvTime;
    OptionsPickerView pvOptions;

    ScheduleEntity entity = new ScheduleEntity();

    Dialog dialog;

    String scheduleNum="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_create_schedule);

        TextView titleView = findViewById(R.id.titleView);
        titleView.setText("新建日程");
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
        actionView.setText("创建");
        actionView.setOnClickListener(this);

        checkPermission(getApplication());

        entity.setAccountIds(Constant.userEntity.getPersonInfo().getId()+"");

        scheduleTitleView = findViewById(R.id.editText1);
        scheduleInfoView = findViewById(R.id.editText2);
        numberView=findViewById(R.id.textView9);

        executorView = findViewById(R.id.textView4);
        executorView.setOnClickListener(this);
        executorView.setText(Constant.userEntity.getPersonInfo().getName());

        importanceView = findViewById(R.id.textView7);
        importanceView.setOnClickListener(this);

        scheduleTypeView = findViewById(R.id.textView8);
        scheduleTypeView.setOnClickListener(this);

        startTime = findViewById(R.id.textView5);
        startTime.setOnClickListener(this);

        endTime = findViewById(R.id.textView6);
        endTime.setOnClickListener(this);

        getScheduleNum();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.actionView:
                if (checkInput()) {
                    entity.setScheduleTitle(scheduleTitleView.getText().toString().trim());
                    entity.setScheduleDescribe(scheduleInfoView.getText().toString().trim());

                    createSchedule();
                }
                break;
            case R.id.textView4://执行人
                intent = new Intent(v.getContext(), PersonChoseActivity.class);
                intent.putExtra("type", 5);
                startActivityForResult(intent, 1);
                break;
            case R.id.textView5://开始时间
                initTimePicker(1);
                break;
            case R.id.textView6://结束时间
                initTimePicker(2);
                break;
            case R.id.textView7://重要度
                if (importanceOptions == null) {
                    importanceView.setEnabled(false);
                    loadImportance();
                } else {
                    initOptionsPicker(importanceOptions, 1);
                }
                break;
            case R.id.textView8://日程类型
                if (typeOptions == null) {
                    scheduleTypeView.setEnabled(false);
                    loadScheduleType();
                } else {
                    initOptionsPicker(typeOptions, 2);
                }
                break;
        }


    }

    private boolean checkPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_DENIED||
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR,Manifest.permission.READ_CALENDAR}, 1);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length == 2) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED||grantResults[1] == PackageManager.PERMISSION_DENIED) {
                        Util.showToast(this, "请打开读写日历权限");
                    } else {

                    }
                }
                break;
        }
    }

    private boolean checkInput() {
        if (TextUtils.isEmpty(scheduleTitleView.getText().toString().trim())) {
            Util.showToast(this, "请输入日程标题");
            return false;
        }
//        if (TextUtils.isEmpty(scheduleInfoView.getText().toString().trim())) {
//            Util.showToast(this,"请输入日程描述");
//            return false;
//        }
        if (TextUtils.isEmpty(executorView.getText().toString().trim())) {
            Util.showToast(this, "请选择执行人");
            return false;
        }
        if (TextUtils.isEmpty(startTime.getText().toString().trim())) {
            Util.showToast(this, "请设置开始时间");
            return false;
        }
        if (TextUtils.isEmpty(endTime.getText().toString().trim())) {
            Util.showToast(this, "请设置结束时间");
            return false;
        }
        if(entity.getEndTime().compareTo(entity.getStartTime())<=0){
            Util.showToast(this, "结束时间应大于开始时间");
            return false;
        }
        if (TextUtils.isEmpty(importanceView.getText().toString().trim())) {
            Util.showToast(this, "请设置重要度");
            return false;
        }
        if (TextUtils.isEmpty(scheduleTypeView.getText().toString().trim())) {
            Util.showToast(this, "请设置日程类型");
            return false;
        }
        return true;
    }

    private void initTimePicker(final int type) {
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            public void onTimeSelect(Date date, View v) {
                if (type == 1) {
                    if (Util.checkStartTime(getApplication(),date)) {
                        startTime.setText(Util.getDateStr(date, null));
                        entity.setStartTime(startTime.getText().toString());
                    }else {
                        startTime.setText("");
                    }
                } else if (type == 2) {
                    endTime.setText(Util.getDateStr(date, null));
                    entity.setEndTime(endTime.getText().toString());
                }
            }
        }).setType(new boolean[]{true, true, true, true, true, false})
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

        Calendar calendar = Calendar.getInstance();
        if (type == 1) {
            if (!TextUtils.isEmpty(startTime.getText())) {
                Date date = new Date();
                date.setTime(Util.strToStemp(startTime.getText().toString(), null));
                calendar.setTime(date);
            }
        } else if (type == 2) {
            if (!TextUtils.isEmpty(endTime.getText())) {
                Date date = new Date();
                date.setTime(Util.strToStemp(endTime.getText().toString(), null));
                calendar.setTime(date);
            }
        }
        pvTime.setDate(calendar);
        pvTime.show();
    }


    private void initOptionsPicker(final List<String> optionsItems, final int type) {
        if (optionsItems == null) {
            return;
        }
        pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                if (type == 1) {
                    importanceView.setText(optionsItems.get(options1));
                    entity.setScheduleImportance(importanceList.get(options1).getKey());
                } else if (type == 2) {
                    scheduleTypeView.setText(optionsItems.get(options1));
                    entity.setScheduleType(typeList.get(options1).getKey());
                }
            }
        }).setLineSpacingMultiplier(3f)
                .setContentTextSize(15)
                .setDividerColor(0x00ffffff)
                .setLayoutRes(R.layout.layout_custom_pickerview_options, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = v.findViewById(R.id.tv_finish);
                        TextView tvCancel = v.findViewById(R.id.tv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvOptions.returnData();
                                pvOptions.dismiss();
                            }
                        });
                        tvCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvOptions.dismiss();
                            }
                        });
                    }
                })
                .build();

//        pvOptions.setSelectOptions(index);
        pvOptions.setPicker(optionsItems);
        pvOptions.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    executors = data.getParcelableArrayListExtra("entities");
                    if (executors != null&&executors.size()>0) {
                        if (executors.size()==1&&executors.get(0).getId()==Constant.userEntity.getPersonInfo().getId()) {
                            executorView.setText(Constant.userEntity.getPersonInfo().getName());
                            entity.setAccountIds(Constant.userEntity.getPersonInfo().getId()+"");
                        }else{
                            executorView.setText("已选择" + executors.size() + "位执行人");
                            StringBuffer sb = new StringBuffer();
                            for (PersonEntity personEntity : executors) {
                                sb.append(personEntity.getId());
                                sb.append(",");
                            }
                            String string = sb.toString();
                            entity.setAccountIds(string.substring(0, string.length() - 1));
                        }
                    }
                    break;
            }
        }
    }


    private void loadImportance() {
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/schedule/importance";
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
                importanceView.setEnabled(true);
                System.out.println("==============" + jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
                        importanceList = new Gson().fromJson(jsonObj.getString("data"), new TypeToken<List<OptionEntity>>() {
                        }.getType());
                        importanceOptions = new ArrayList<>();
                        for (OptionEntity optionEntity : importanceList) {
                            importanceOptions.add(optionEntity.getValue());
                        }
                        initOptionsPicker(importanceOptions, 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                importanceView.setEnabled(true);
            }
        });
    }

    private void loadScheduleType() {
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/schedule/state";
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
                scheduleTypeView.setEnabled(true);
                System.out.println("==============" + jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
                        typeList = new Gson().fromJson(jsonObj.getString("data"), new TypeToken<List<OptionEntity>>() {
                        }.getType());
                        typeOptions = new ArrayList<>();
                        for (OptionEntity optionEntity : typeList) {
                            typeOptions.add(optionEntity.getValue());
                        }
                        initOptionsPicker(typeOptions, 2);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                scheduleTypeView.setEnabled(true);
            }
        });
    }

    private void createSchedule() {
        dialog=Util.showLoadingDialog(this,"日程创建中…");
        RequestUtil.jsonObjectRequest(new RequestListener() {
            @Override
            public String getUrl() {
                return "/schedule/add";
            }

            @Override
            public Map<String, String> getParams() {
                return null;
            }

            @Override
            public JSONObject getJsonObj() {
                JSONObject jsonObject= null;
                try {
                    jsonObject = new JSONObject();
//                    jsonObject = new JSONObject(new Gson().toJson(entity));
                    jsonObject.putOpt("startTimeString",entity.getStartTime());
                    jsonObject.putOpt("endTimeString",entity.getEndTime());
                    jsonObject.putOpt("scheduleTitle",entity.getScheduleTitle());
                    jsonObject.putOpt("scheduleType",entity.getScheduleType());
                    jsonObject.putOpt("scheduleDescribe",entity.getScheduleDescribe());
                    jsonObject.putOpt("scheduleImportance",entity.getScheduleImportance());
                    jsonObject.putOpt("accountIds",entity.getAccountIds());
                    jsonObject.putOpt("scheduleNumber",entity.getScheduleNumber());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("jsonObject================="+jsonObject.toString());
                return jsonObject;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("createSchedule================" + jsonObj.toString());
                dialog.dismiss();
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1) {
                        if (checkPermission(getApplication())) {
                            CalendarReminderUtils.addCalendarEvent(getApplication(),entity.getScheduleTitle(),entity.getScheduleDescribe(),Util.strToStemp(entity.getStartTime(),null),Util.strToStemp(entity.getEndTime(),null),0);
                        }
                        EventBus.getDefault().post(new RefreshEvent(RefreshEvent.Refresh_MySchedule));
                        setResult(RESULT_OK);
                        finish();
                    }else{
                        Util.showToast(getBaseContext(),jsonObj.getString("message"));
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

    private void getScheduleNum(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/schedule/getSupervisionNumber";
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
                        scheduleNum= jsonObj.getJSONObject("data").getString("supervision_number");
                        entity.setScheduleNumber(scheduleNum);
                        ((TextView)findViewById(R.id.textView9)).setText(scheduleNum);
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
