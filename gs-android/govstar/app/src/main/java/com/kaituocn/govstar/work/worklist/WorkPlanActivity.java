package com.kaituocn.govstar.work.worklist;

import android.content.Intent;
import android.os.Bundle;
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
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.PersonEntity;
import com.kaituocn.govstar.entity.WorkPlanInfoEntity;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;
import com.kaituocn.govstar.work.supervision.PersonChoseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class WorkPlanActivity extends AppCompatActivity implements View.OnClickListener{

    String person;
    String time;
    EditText editText;
    TextView departView,personView,priorityView,timeView;

    WorkPlanInfoEntity entity=new WorkPlanInfoEntity();

    JSONObject object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_work_plan);
        TextView titleView = findViewById(R.id.titleView);
        titleView.setText("工作计划");
        View backView = findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        editText=findViewById(R.id.editText2);
        departView=findViewById(R.id.textView12);
        personView=findViewById(R.id.textView13);
        priorityView=findViewById(R.id.textView14);
        timeView=findViewById(R.id.textView15);

        if (getIntent().getBooleanExtra("isDoing",false)) {
            getDetailInfo(getIntent().getIntExtra("id",0));

        }else {
            TextView actionView = findViewById(R.id.actionView);
            actionView.setVisibility(View.VISIBLE);
            actionView.setText("反馈");
            actionView.setOnClickListener(this);

            person=getIntent().getStringExtra("person");
            time=getIntent().getStringExtra("time");

            ((TextView)findViewById(R.id.textView10)).setText(person);
            ((TextView)findViewById(R.id.textView11)).setText(time);


            editText.post(new Runnable() {
                @Override
                public void run() {
                    editText.setFocusableInTouchMode(true);
                    editText.setFocusable(true);
                }
            });

            departView.setOnClickListener(this);
            personView.setOnClickListener(this);
            priorityView.setOnClickListener(this);
            timeView.setOnClickListener(this);
        }

    }

    private void initView(JSONObject object){
        try {

            departView.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            personView.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            priorityView.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            timeView.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);

            ((TextView)findViewById(R.id.textView10)).setText(object.getString("companyLead"));
            ((TextView)findViewById(R.id.textView11)).setText(Util.stempToStr(object.getLong("endTime"),"yyyy-MM-dd"));
            departView.setText(object.getString("depName"));
            personView.setText(object.getString("userName"));
            priorityView.setText((object.getInt("priority")==1)?"紧急":"一般");
            timeView.setText(object.getString("requirementEndTime"));
            editText.setText(object.getString("planInfo"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        Util.hideInput(v.getContext(),v);
        switch (v.getId()) {
            case R.id.actionView:
                String info=editText.getText().toString().trim();
                if (TextUtils.isEmpty(info)) {
                    Util.showToast(v.getContext(),"请填写工作计划");
                }else{
                    entity.setPlanInfo(info);
                    Intent data=new Intent();
                    data.putExtra("entity",entity);
                    setResult(RESULT_OK,data);
                    finish();
                }
                break;
            case R.id.textView12:
                intent =new Intent(v.getContext(),PersonChoseActivity.class);
                intent.putExtra("type",11);
                intent.putExtra("title","负责部门");
                startActivityForResult(intent,1);
                break;
            case R.id.textView13:
                intent =new Intent(v.getContext(),PersonChoseActivity.class);
                intent.putExtra("type",10);
                intent.putExtra("title","主责人");
                startActivityForResult(intent,2);
                break;
            case R.id.textView14:

                List<String> optionsItems = new ArrayList<>();
                optionsItems.add("紧急");
                optionsItems.add("一般");
                initOptionsPicker(optionsItems);
                break;
            case R.id.textView15:
                initTimePicker();
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK) {
            switch (requestCode) {
                case 1:
                    List<PersonEntity> list = data.getParcelableArrayListExtra("entities");                    ;
                    entity.setDepId(Util.getPersonIds(list));
                    departView.setText(String.format("已选择 %d 个负责部门",list.size()));
                    break;
                case 2:
                   list = data.getParcelableArrayListExtra("entities");
                    entity.setUserId(Util.getPersonIds(list));
                    personView.setText(String.format("已选择 %d 位负责领导",list.size()));
                    break;
            }
        }
    }

    OptionsPickerView pvOptions;
    private void initOptionsPicker(final List<String> optionsItems) {

        pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
               priorityView.setText(optionsItems.get(options1));
               entity.setPriority(options1==0?"1":"2");
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
        pvOptions.setPicker(optionsItems);
        pvOptions.show();
    }

    TimePickerView pvTime;
    private void initTimePicker() {
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            public void onTimeSelect(Date date, View v) {
                timeView.setText(Util.getDateStr(date, null));
                entity.setEndTime(date.getTime());
            }
        }).setType(new boolean[]{true, true, true, true, true, false})
                .setLabel("年", "月", "日", ":时", "分", "秒")
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
        Calendar calendar=Calendar.getInstance();
        if (entity.getEndTime()!=0) {
            Date date=new Date();
            date.setTime(entity.getEndTime());
            calendar.setTime(date);
        }
        pvTime.setDate(calendar);
        pvTime.show();
    }



    private void getDetailInfo(final int id){
        RequestUtil.jsonObjectRequest(new RequestListener() {
            @Override
            public String getUrl() {
                return "/supervision/getWorkPlanInfo";
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
                System.out.println("getWorkPlanInfo=============="+jsonObj);
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1){
                        object=jsonObj.getJSONObject("data");
                        initView(object);
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
