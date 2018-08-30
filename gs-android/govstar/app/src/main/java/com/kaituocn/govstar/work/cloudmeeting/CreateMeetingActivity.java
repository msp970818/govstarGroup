package com.kaituocn.govstar.work.cloudmeeting;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.constraint.Group;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.kaituocn.govstar.entity.MeetingEntity;
import com.kaituocn.govstar.entity.PersonEntity;
import com.kaituocn.govstar.entity.SupervisionEntity;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;
import com.kaituocn.govstar.work.supervision.PersonChoseActivity;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateMeetingActivity extends AppCompatActivity implements View.OnClickListener{

    EditText meetingRoomView;
    EditText meetingTitleView;
    EditText meetingDescribeView;
    EditText meetingRemarksView;
    TextView timeView;
    TextView attendeeView;
    TextView meetingNumView;

    TimePickerView pvTime;
    OptionsPickerView pvOptions;
    MeetingEntity entity;
    Dialog dialog;
    Group groupMeetingRoom;
    int optionIndex=1;

    //上传文件list
    List<File> fileList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());

        setContentView(R.layout.activity_create_meeting);
        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("创建会议");
        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        View backView=findViewById(R.id.backView);
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
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    entity.setMeetingTitile(meetingTitleView.getText().toString());
                    entity.setMettingRoomName(meetingRoomView.getText().toString());
                    entity.setMettingDescribe(meetingDescribeView.getText().toString());
                    entity.setRemarksInfo(meetingRemarksView.getText().toString());

                    createMeeting();
                }
            }
        });

        entity=new MeetingEntity();
        entity.setMettingState(1);

        meetingTitleView =findViewById(R.id.editText1);
        meetingRoomView =findViewById(R.id.editText7);
        meetingDescribeView =findViewById(R.id.editText2);
        meetingRemarksView =findViewById(R.id.editText3);
        meetingNumView =findViewById(R.id.textView9);
        groupMeetingRoom=findViewById(R.id.groupMeetingRoom);
        groupMeetingRoom.setVisibility(View.GONE);

        timeView=findViewById(R.id.textView5);
        timeView.setOnClickListener(this);
        attendeeView=findViewById(R.id.textView6);
        attendeeView.setOnClickListener(this);

        findViewById(R.id.textView4).setOnClickListener(this);
        findViewById(R.id.imageView3).setOnClickListener(this);
        findViewById(R.id.textView109).setOnClickListener(this);
    }

    private boolean checkInput(){
        if (TextUtils.isEmpty(meetingTitleView.getText().toString().trim())) {
            Util.showToast(this,"请输入会议标题");
            return false;
        }
        if (optionIndex==0&&TextUtils.isEmpty(meetingRoomView.getText().toString().trim())) {
            Util.showToast(this,"请输入会议室名称");
            return false;
        }
        if (entity.getMettingType()==0) {
            Util.showToast(this,"请选择会议类型");
            return false;
        }
        if (entity.getMtstartTime()==0) {
            Util.showToast(this,"请设置开会时间");
            return false;
        }
        if (TextUtils.isEmpty(entity.getJoinUserId())) {
            Util.showToast(this,"请选择参会人");
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        Util.hideInput(v.getContext(),v);
        switch (v.getId()) {
            case R.id.textView4:
                initOptionsPicker(getMeetinggType(),v);
                break;
            case R.id.textView5:
                initTimePicker();
                break;
            case R.id.textView6:
                Intent intent =new Intent(v.getContext(),PersonChoseActivity.class);
                intent.putExtra("type",22);
                startActivityForResult(intent,2);
                break;
            case R.id.imageView3:
                if (checkPermission(this)) {
                    intent=new Intent(v.getContext(),MapActivity.class);
                    startActivityForResult(intent,1);
                }
                break;
            case R.id.textView109:
                pickerFile();
                break;
        }
    }

    private void initTimePicker() {
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            public void onTimeSelect(Date date, View v) {
                if (Util.checkStartTime(getApplication(),date)) {
                    timeView.setText(Util.getDateStr(date, null));
                    entity.setMtstartTime(date.getTime());
                }else{
                    timeView.setText("");
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

        pvTime.setDate(Calendar.getInstance());
        pvTime.show();
    }

    private void initOptionsPicker(final List<String> optionsItems, final View view) {

        pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                if (view instanceof TextView) {
                    optionIndex=options1;
                    ((TextView) view).setText(optionsItems.get(options1));
                    entity.setMettingType(options1+1);
                    if (options1==0) {
                        groupMeetingRoom.setVisibility(View.VISIBLE);
                    }else{
                        groupMeetingRoom.setVisibility(View.GONE);
                    }
                    getMettingNum();
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
        pvOptions.setPicker(optionsItems);
        pvOptions.setSelectOptions(optionIndex);
        pvOptions.show();
    }
    private  List<String> getMeetinggType(){
        List<java.lang.String> optionsItems = new ArrayList<>();
        optionsItems.add("现场会议");
        optionsItems.add("音视频通话");
        optionsItems.add("直播会议");

        return optionsItems;
    }

    private void pickerFile(){
        Matisse.from(this)
                .choose(MimeType.ofImage())
//                .countable(true)
                .maxSelectable(1000)
//                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
//                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .theme(R.style.PhotoPickerTheme)
                .imageEngine(new GlideEngine())
                .forResult(10);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK) {
            switch (requestCode) {
                case 1:
                    entity.setLongitudeLatitude(data.getStringExtra("latlon"));

                    String address = data.getStringExtra("address");
                    if (!TextUtils.isEmpty(address)) {
                        meetingRoomView.setText(address);
                        meetingRoomView.setSelection(address.length());
                        meetingRoomView.requestFocus();
                    }

                    break;
                case 2:

                    List<PersonEntity> list = data.getParcelableArrayListExtra("entities");
                    attendeeView.setText("已选择 " + list.size() + " 位参会人");
                    entity.setJoinUserId(Util.getPersonIds(list));

                    System.out.println("==============" + entity.getJoinUserId());

                    break;
                case 10:
                    List<Uri> uriList = Matisse.obtainResult(data);
                    if (uriList != null) {
                        ((TextView) findViewById(R.id.textView112)).setText("已选择" + uriList.size() + "个附件");
                    } else {
                        ((TextView) findViewById(R.id.textView112)).setText("已选择0个附件");
                    }
                    for (Uri uri : uriList) {
                        fileList.add(Util.getFileByUri(uri,getBaseContext()));
                    }
                    upLoadFiles();
                    break;
            }
        }


    }


    private boolean checkPermission(Context context){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return false;
        }else{
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length == 1) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Util.showToast(this, "请打开位置权限");
                    } else{
                        Intent intent=new Intent(this,MapActivity.class);
                        startActivityForResult(intent,1);
                    }
                }
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Util.showToast(getBaseContext(),msg.obj.toString());
                    break;
            }
        }
    };

    private void createMeeting(){
        dialog=Util.showLoadingDialog(this,"创建中…");
        RequestUtil.jsonObjectRequest(new RequestListener() {
            @Override
            public String getUrl() {
                return "/CloudMetting/addMetting";
            }

            @Override
            public Map<String, String> getParams() {
                return null;
            }

            @Override
            public JSONObject getJsonObj() {

                JSONObject jsonObject= null;
                try {
                    jsonObject = new JSONObject(new Gson().toJson(entity));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                List<SupervisionEntity.UploadFile> list=new ArrayList<>();
//                list.add(new SupervisionEntity.UploadFile("name1","path1"));
//                list.add(new SupervisionEntity.UploadFile("name2","path2"));
//                entity.setFilesUrlArray(list);
                System.out.println("jsonObject================"+jsonObject.toString());
                return jsonObject;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("createMeeting======="+jsonObj);
                dialog.dismiss();
                try {
                    int code = jsonObj.getInt("code");
                    if (code==1) {
                        setResult(RESULT_OK);
                        finish();
                    }else{
                        Util.showToast(getApplication(),jsonObj.getString("message"));
                    }
                } catch (JSONException e) {

                    Util.showToast(getApplication(),e.toString());
                }
            }

            @Override
            public void onError(String error) {
                dialog.dismiss();
                Util.showToast(getApplication(),getString(R.string.error_server));
            }
        });
    }

    private void getMettingNum(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/CloudMetting/getMettingNum";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String,String> map=new HashMap<>();
                map.put("mettingType",entity.getMettingType()+"");
                return map;
            }

            @Override
            public JSONObject getJsonObj() {

                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("==================="+jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code==1) {
                        entity.setMettingNum(jsonObj.getString("mettingNum"));
                        meetingNumView.setText(entity.getMettingNum());
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

    private void upLoadFiles(){
        dialog=Util.showLoadingDialog(this,"附件上传中…");
        RequestUtil.uploadFiles(fileList, new RequestListener() {
            @Override
            public String getUrl() {
                return "/supervision/uploadFileList";
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
                if (dialog != null) {
                    dialog.dismiss();
                }
                System.out.println("upLoadFiles================"+jsonObj);
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1) {
                        List<SupervisionEntity.UploadFile> list =new Gson().fromJson(jsonObj.getString("data"),new TypeToken<List<SupervisionEntity.UploadFile>>(){}.getType());
                        entity.setFilesUrlArray(list);
                        handler.obtainMessage(1,"附件上传成功！").sendToTarget();

                    }else{
                        handler.obtainMessage(1,jsonObj.getString("message")).sendToTarget();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String error) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                handler.obtainMessage(1,getString(R.string.error_server)).sendToTarget();
            }
        });
    }


}
