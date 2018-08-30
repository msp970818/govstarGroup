package com.kaituocn.govstar.work.supervision;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
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
import com.kaituocn.govstar.entity.PersonEntity;
import com.kaituocn.govstar.entity.SupervisionEntity;
import com.kaituocn.govstar.util.Constant;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;
import com.kaituocn.govstar.work.PreviewActivity;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CreateSupervisionActivity extends AppCompatActivity implements View.OnClickListener {

    TimePickerView pvTime;
    OptionsPickerView pvOptions;

    TextView typeView;
    TextView feedbackView;
    TextView warningView;
    TextView timeView;
    TextView leaderView;
    TextView hostUnitView;
    TextView cooperateUnitView;
    EditText titleEditView;
    EditText infoEditView;
    EditText remarkView;

    TagFlowLayout tagFlowLayout;
    TagAdapter tagAdapter;
    List<Uri> annexList=new ArrayList<>();

    SupervisionEntity entity=new SupervisionEntity();

   List<String>  supervisionTypes=new ArrayList<>();
   List<String>  supervisionNames=new ArrayList<>();

   List<String>  feedbackTypes=new ArrayList<>();
   List<String>  feedbackNames=new ArrayList<>();

   List<String>  warningTypes=new ArrayList<>();
   List<String>  warningNames=new ArrayList<>();

    boolean authorityFlag;
    String supervisionNumber;

    ArrayList<Integer> resultIds=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_create_supervision);
        TextView titleView = findViewById(R.id.titleView);
        titleView.setText("新建督查任务");
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
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkInput()) {
                    entity.setUserList(new SupervisionEntity.Unit(selectPerson,firstComp,secondComp));
                    entity.setSupervisionTitle(titleEditView.getText().toString().trim());
                    entity.setSupervisionInfo(infoEditView.getText().toString().trim());
                    entity.setRemarks(remarkView.getText().toString().trim());
                    createSupervision();
                }

            }
        });

        authorityFlag= Constant.userEntity.getPowerMap().get("62");
        if(!authorityFlag){
            ((TextView)findViewById(R.id.textView32)).setText("单位领导");
            ((TextView)findViewById(R.id.textView33)).setText("主责部门");
            ((TextView)findViewById(R.id.textView34)).setText("配合部门");
        }

        entity.setNoticeType("3");
        entity.setFeedbackType("0");
        entity.setIsDelay("1");
        entity.setIsFeedbak("0");
        entity.setIsNoticeLeader("0");

        tagFlowLayout=findViewById(R.id.tagFlowLayout);
        tagFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                Intent intent=new Intent(view.getContext(), PreviewActivity.class);
                intent.putExtra("path",Util.getFileByUri(annexList.get(position),getBaseContext()).getPath());
                startActivity(intent);
                return true;
            }
        });
        tagFlowLayout.setAdapter(tagAdapter=new TagAdapter<Uri>(annexList) {
            @Override
            public View getView(FlowLayout parent, int position, Uri s) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag_annex,parent, false);

                TextView textView1=view.findViewById(R.id.textView1);
                TextView textView2=view.findViewById(R.id.textView2);
                textView1.setText("附件"+(position+1)+"：");
                File file= Util.getFileByUri(s,getBaseContext());
                textView2.setText(file.getName());

                return view;
            }
        });

        titleEditView=findViewById(R.id.editText);
        infoEditView=findViewById(R.id.editText2);
        remarkView=findViewById(R.id.editText3);

        typeView = findViewById(R.id.textView10);
        typeView.setOnClickListener(this);
        timeView = findViewById(R.id.textView11);
        timeView.setOnClickListener(this);

        leaderView = findViewById(R.id.textView12);
        leaderView.setOnClickListener(this);
        hostUnitView = findViewById(R.id.textView13);
        hostUnitView.setOnClickListener(this);
        cooperateUnitView = findViewById(R.id.textView14);
        cooperateUnitView.setOnClickListener(this);

        warningView = findViewById(R.id.textView15);
        warningView.setOnClickListener(this);
        feedbackView = findViewById(R.id.textView16);
        feedbackView.setOnClickListener(this);

        findViewById(R.id.textView17).setOnClickListener(this);
        findViewById(R.id.textView18).setOnClickListener(this);
        findViewById(R.id.textView19).setOnClickListener(this);
        findViewById(R.id.textView20).setOnClickListener(this);
        findViewById(R.id.textView21).setOnClickListener(this);

        getNumber();
    }

    private boolean checkInput(){
        if (TextUtils.isEmpty(entity.getSupervisionType())) {
            Util.showToast(this,"请选择督查类型");
            return false;
        }
        if (entity.getEndTime()==0) {
            Util.showToast(this,"请设置办结日期");
            return false;
        }
        if (selectPerson.isEmpty()) {
            if(authorityFlag){
                Util.showToast(this,"请选择负责领导");
            }else{
                Util.showToast(this,"请选择单位领导");
            }
            return false;
        }
        if (firstComp.isEmpty()) {
            if(authorityFlag){
                Util.showToast(this,"请选择主责单位");
            }else{
                Util.showToast(this,"请选择主责部门");
            }
            return false;
        }
        if (TextUtils.isEmpty(titleEditView.getText().toString().trim())) {
            Util.showToast(this,"请输入督查标题");
            return false;
        }
        if (TextUtils.isEmpty(remarkView.getText().toString().trim())) {
            Util.showToast(this,"请输入备注信息");
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.textView10:
                if (supervisionNames.isEmpty()) {
                    getSupervisionTypeData();
                }else{
                    initOptionsPicker(supervisionNames,v,1);
                }
                break;
            case R.id.textView11:
                initTimePicker();
                break;
            case R.id.textView12:
                intent =new Intent(v.getContext(),PersonChoseActivity.class);
                intent.putExtra("type",9);
                if(authorityFlag){
                    intent.putExtra("title","负责领导");
                }else{
                    intent.putExtra("title","单位领导");
                }
                resultIds.clear();
                resultIds.addAll(selectPerson);
                intent.putExtra("resultIds",resultIds);
                startActivityForResult(intent,0);
                break;
            case R.id.textView13:
                intent =new Intent(v.getContext(),PersonChoseActivity.class);
                intent.putExtra("type",20);
                if(authorityFlag){
                    intent.putExtra("title","主责单位");
                }else{
                    intent.putExtra("title","主责部门");
                }
                startActivityForResult(intent,1);
                break;
            case R.id.textView14:
                intent =new Intent(v.getContext(),PersonChoseActivity.class);
                intent.putExtra("type",21);
                if(authorityFlag){
                    intent.putExtra("title","配合单位");
                }else{
                    intent.putExtra("title","配合部门");
                }
                startActivityForResult(intent,2);
                break;
            case R.id.textView15:
                if (warningNames.isEmpty()) {
                    getWarningData();
                }else{
                    initOptionsPicker(warningNames,v,2);
                }
                break;
            case R.id.textView16:
                if (feedbackNames.isEmpty()) {
                    getFeedbackData();
                }else{
                    initOptionsPicker(feedbackNames,v,3);
                }
                break;
            case R.id.textView17:
                initOptionsPicker(getTypeDelay(),v,4);
                break;
            case R.id.textView18:
                initOptionsPicker(getTypePlan(),v,5);
                break;
            case R.id.textView19:
                initOptionsPicker(getTypeBaosong(),v,6);
                break;
            case R.id.textView20:
//                initOptionsPicker(getTypeDate(),v);
                break;
            case R.id.textView21:
                pickerFile();
                break;
        }
    }

    List<Integer> selectPerson=new ArrayList<>();
    List<Integer> firstComp=new ArrayList<>();
    List<Integer> secondComp=new ArrayList<>();
    //上传文件list
    List<File> fileList=new ArrayList<>();
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK) {
            switch (requestCode) {
                case 0:
                    List<PersonEntity> personList = data.getParcelableArrayListExtra("entities");
                    leaderView.setText("已选择 "+personList.size()+" 位负责领导");
                    for (PersonEntity enity : personList) {
                        selectPerson.add(enity.getId());
                    }
                    break;
                case 1:
                    List<PersonEntity> list = data.getParcelableArrayListExtra("entities");
                    hostUnitView.setText("已选择 "+list.size()+" 个主责单位");
                    firstComp.clear();
                    for (PersonEntity entity : list) {
                        firstComp.add(entity.getId());
                    }
                    break;
                case 2:
                    list = data.getParcelableArrayListExtra("entities");
                    cooperateUnitView.setText("已选择 "+list.size()+" 个配合单位");
                    for (PersonEntity entity : list) {
                        secondComp.add(entity.getId());
                    }
                    break;
                case 10:
                    List<Uri> uriList=Matisse.obtainResult(data);
                    annexList.clear();
                    annexList.addAll(uriList);
                    tagAdapter.notifyDataChanged();
                    for (Uri uri : uriList) {
                        fileList.add(Util.getFileByUri(uri,getBaseContext()));
                    }
                    uploadFiles();
                    break;
            }
        }
    }
    
    private int getSelectedNum(boolean[] selecteds){
        int num = 0;
        for (boolean selected : selecteds) {
            if (selected) {
                num++;
            }
        }
        return num;
    }

    private void initTimePicker() {
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            public void onTimeSelect(Date date, View v) {
                if (Util.checkStartTime(getApplication(),date)) {
                    timeView.setText(Util.getDateStr(date, "yyyy-MM-dd HH:00"));
                    entity.setEndTime(date.getTime());
                }else{
                    timeView.setText("");
                }
            }
        }).setType(new boolean[]{true, true, true, true, false, false})
                .setLabel("年", "月", "日", ":00", "分", "秒")
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
        calendar.set(Calendar.MINUTE,0);
        calendar.add(Calendar.HOUR_OF_DAY,1);
        pvTime.setDate(calendar);
        pvTime.show();
    }

    private void initOptionsPicker(final List<String> optionsItems, final View view, final int type) {

        pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                if (view instanceof TextView) {
                    ((TextView) view).setText(optionsItems.get(options1));
                }
                if (type==1) {
                    entity.setSupervisionType(supervisionTypes.get(options1));
                }else if(type==2){
                    entity.setNoticeType(warningTypes.get(options1));
                }else if(type==3){
                    entity.setFeedbackType(feedbackTypes.get(options1));
                }else if (type==4){
                    if (options1==0) {
                        entity.setIsDelay("1");
                    }else{
                        entity.setIsDelay("0");
                    }
                }else if (type==5){
                    if (options1==0) {
                        entity.setIsFeedbak("1");
                    }else{
                        entity.setIsFeedbak("0");
                    }
                }else if (type==6){
                    if (options1==0) {
                        entity.setIsNoticeLeader("1");
                    }else{
                        entity.setIsNoticeLeader("0");
                    }
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

//    private  List<String> getTypeSupervision(){
//        List<String> optionsItems = new ArrayList<>();
//
//        optionsItems.add("市人大建议落实情况");
//        optionsItems.add("市政协提案落实情况");
//        optionsItems.add("区人大建议落实情况");
//        optionsItems.add("区政协提案落实情况");
//        optionsItems.add("区领导组织重点工作");
//        optionsItems.add("重要会议督查");
//        optionsItems.add("领导交办的督查任务");
//        optionsItems.add("年度重点工作");
//        optionsItems.add("市政府办公厅（上级）单位转办的督查事项");
//        optionsItems.add("民心工程重点任务");
//        optionsItems.add("年度重点工作");
//        optionsItems.add("政务信息报送");
//        optionsItems.add("政务督查总结报送");
//        optionsItems.add("年度目标绩效考核工作指标");
//        optionsItems.add("其他事项");
//        return optionsItems;
//    }



    private  List<String> getTypeAlert(){
        List<java.lang.String> optionsItems = new ArrayList<>();
        optionsItems.add("一级预警（仅到期前2日预警）");
        optionsItems.add("二级预警（到期前5日预警）");
        optionsItems.add("三级预警（到期前10日预警）");

        return optionsItems;
    }
    //反馈类型
    private  List<String> getTypeFeedback(){
        List<java.lang.String> optionsItems = new ArrayList<>();
        optionsItems.add("按天反馈循环至办结日期");
        optionsItems.add("按周反馈循环至办结日期");
        optionsItems.add("按月反馈循环至办结日期");
        optionsItems.add("按季反馈循环至办结日期");
        optionsItems.add("按年反馈循环至办结日期");
        optionsItems.add("要求办结时间一次督查");
        return optionsItems;
    }
    //申请延期
    private  List<String> getTypeDelay(){
        List<java.lang.String> optionsItems = new ArrayList<>();
        optionsItems.add("允许");
        optionsItems.add("不允许");

        return optionsItems;
    }

    //反馈计划
    private  List<String> getTypePlan(){
        List<java.lang.String> optionsItems = new ArrayList<>();
        optionsItems.add("需要");
        optionsItems.add("不需要");
        return optionsItems;
    }

    //报送领导
    private  List<String> getTypeBaosong(){
        List<java.lang.String> optionsItems = new ArrayList<>();
        optionsItems.add("需要");
        optionsItems.add("不需要");

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

    private void createSupervision(){
        dialog=Util.showLoadingDialog(this,"创建中…");
        RequestUtil.jsonObjectRequest(new RequestListener() {
            @Override
            public String getUrl() {
                return "/supervision/addTask";
            }

            @Override
            public Map<String, String> getParams() {
                return null;
            }

            @Override
            public JSONObject getJsonObj() {

//                SupervisionEntity entity=new SupervisionEntity();
//                List<SupervisionEntity.UploadFile> list=new ArrayList<>();
//                list.add(new SupervisionEntity.UploadFile("name1","path1"));
//                list.add(new SupervisionEntity.UploadFile("name2","path2"));
//                entity.setFilesUrlArray(list);
//                entity.setUserList(new SupervisionEntity.Unit("1,2","1","1"));
//                entity.setEndTime(System.currentTimeMillis()+60*1000);
//                entity.setFeedbackType("1");
//                entity.setIsDelay("允许");
//                entity.setIsFeedbak("需要");
//                entity.setSupervisionInfo("督查内容督查内容督查内容督查内容");
//                entity.setSupervisionNumber("123456789");
//                entity.setSupervisionTitle("标题");
//                entity.setSupervisionType("市人大建议落实情况");
//                entity.setNoticeType("三级预警");

                JSONObject jsonObject= null;
                try {
                    jsonObject = new JSONObject(new Gson().toJson(entity));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("jsonObject================="+jsonObject.toString());
                return jsonObject;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                dialog.dismiss();
                System.out.println("createSupervision========="+jsonObj.toString());
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

    private void uploadFile(){
        File file=new File(getExternalCacheDir().getAbsolutePath()+"/temp.jpg");
        RequestUtil.uploadFile(file, new RequestListener() {
            @Override
            public String getUrl() {
                return "/supervision/fileUpload";
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
                System.out.println("uploadFile==========="+jsonObj);
            }

            @Override
            public void onError(String error) {

            }
        });

    }
    Dialog dialog;
    private void uploadFiles(){
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

    private void getSupervisionTypeData(){
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
                System.out.println("jsonObj========"+jsonObj);
                try {

                    JSONArray array=jsonObj.getJSONArray("data");
                    supervisionTypes.clear();
                    supervisionNames.clear();
                    for (int i = 0; i < array.length(); i++) {
                        supervisionTypes.add(array.getJSONObject(i).getString("kay"));
                        supervisionNames.add(array.getJSONObject(i).getString("name"));
                    }
                    initOptionsPicker(supervisionNames,typeView,1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private void getFeedbackData(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/supervision/getFeedbackType";
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
                System.out.println("jsonObj========"+jsonObj);
                try {

                    JSONArray array=jsonObj.getJSONArray("data");
                    feedbackTypes.clear();
                    feedbackNames.clear();
                    for (int i = 0; i < array.length(); i++) {
                        feedbackTypes.add(array.getJSONObject(i).getString("kay"));
                        feedbackNames.add(array.getJSONObject(i).getString("name"));
                    }
                    initOptionsPicker(feedbackNames,feedbackView,3);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }


    private void getWarningData(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/supervision/getNoticeType";
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
                System.out.println("jsonObj========"+jsonObj);
                try {

                    JSONArray array=jsonObj.getJSONArray("data");
                    warningTypes.clear();
                    warningNames.clear();
                    for (int i = 0; i < array.length(); i++) {
                        warningTypes.add(array.getJSONObject(i).getString("kay"));
                        warningNames.add(array.getJSONObject(i).getString("name"));
                    }
                    initOptionsPicker(warningNames,warningView,2);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }



    private void getNumber(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/supervision/getSupervisionNumber";
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
                System.out.println("getSupervisionNumber==================="+jsonObj);
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1) {
                        supervisionNumber=jsonObj.getJSONObject("data").getString("supervision_number");
                        ((TextView)findViewById(R.id.textView20)).setText(supervisionNumber);
                        entity.setSupervisionNumber(supervisionNumber);
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
