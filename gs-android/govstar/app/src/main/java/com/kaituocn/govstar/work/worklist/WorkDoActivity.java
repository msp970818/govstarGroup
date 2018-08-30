package com.kaituocn.govstar.work.worklist;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.Group;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.FileEntity;
import com.kaituocn.govstar.entity.OptionEntity;
import com.kaituocn.govstar.entity.PersonEntity;
import com.kaituocn.govstar.entity.WorkDoEntity;
import com.kaituocn.govstar.entity.WorkFlowEntity;
import com.kaituocn.govstar.entity.WorkPlanInfoEntity;
import com.kaituocn.govstar.event.RefreshEvent;
import com.kaituocn.govstar.set.OperationInfoActivity;
import com.kaituocn.govstar.util.Constant;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;
import com.kaituocn.govstar.work.PreviewActivity;
import com.kaituocn.govstar.work.WebViewActivity;
import com.kaituocn.govstar.work.supervision.PersonChoseActivity;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkDoActivity extends AppCompatActivity implements View.OnClickListener {

    TagFlowLayout tagFlowLayout1;
    TagFlowLayout tagFlowLayout2;
    TagFlowLayout tagFlowLayout3;
    TagFlowLayout tagFlowLayout4;

    TagAdapter tagAdapter1, tagAdapter2, tagAdapter3, tagAdapter4;

    TextView quickView;
    TextView addView;
    EditText editText;
    ScrollView scrollView;
    View quicklyLayout, feedbackLayout;

    int id, taskId;
    WorkDoEntity entity;

    Map<String, String> scheduleTypes = new HashMap<>();

    List<OptionEntity> tagList1 = new ArrayList<>();
    List<FileEntity> tagList2 = new ArrayList<>();
    List<Uri> tagList3 = new ArrayList<>();
    List<WorkFlowEntity> process = new ArrayList<>();

    List<File> fileList = new ArrayList<>();

    View actionLayout1, actionLayout2, actionLayout3;

    Dialog dialog;
    WorkPlanInfoEntity workPlanInfoEntity;
    List<Integer> forwardUserIds = new ArrayList<>();

    private int mMotionY;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_work_do);

        TextView titleView = findViewById(R.id.titleView);
        titleView.setText("任务办理");
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
        actionView.setText("完成");
        actionView.setOnClickListener(this);

        id = getIntent().getIntExtra("id", 0);
        taskId = getIntent().getIntExtra("taskId", 0);
        System.out.println("============" + id + "   " + taskId);


        tagFlowLayout1 = findViewById(R.id.frameLayout1);
        tagFlowLayout2 = findViewById(R.id.frameLayout2);
        tagFlowLayout3 = findViewById(R.id.frameLayout3);
        tagFlowLayout4 = findViewById(R.id.frameLayout4);


        tagFlowLayout1.setAdapter(tagAdapter1 = new TagAdapter<OptionEntity>(tagList1) {
            @Override
            public View getView(FlowLayout parent, int position, OptionEntity entity) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag, parent, false);
                TextView textView = view.findViewById(R.id.textView);
                if (entity.getKey() == 1) {
                    Util.setBackgroundTint(textView, R.color.tag_red);
                } else {
                    Util.setBackgroundTint(textView, R.color.tag_green);
                }
                textView.setText(entity.getValue());


                return view;
            }
        });
        tagFlowLayout2.setAdapter(tagAdapter2 = new TagAdapter<FileEntity>(tagList2) {
            @Override
            public View getView(FlowLayout parent, int position, FileEntity entity) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag_annex, parent, false);
                TextView textView1 = view.findViewById(R.id.textView1);
                TextView textView2 = view.findViewById(R.id.textView2);
                textView1.setText("附件" + (position + 1) + "：");
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

        tagFlowLayout3.setAdapter(tagAdapter3 = new TagAdapter<Uri>(tagList3) {
            @Override
            public View getView(FlowLayout parent, int position, Uri uri) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag_annex, parent, false);
                TextView textView1 = view.findViewById(R.id.textView1);
                TextView textView2 = view.findViewById(R.id.textView2);
                textView1.setText("附件" + (position + 1) + "：");
                File file = Util.getFileByUri(uri, getBaseContext());
                textView2.setText(file.getName());
                return view;
            }
        });

        tagFlowLayout3.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                Intent intent = new Intent(view.getContext(), PreviewActivity.class);
                intent.putExtra("path", Util.getFileByUri(tagList3.get(position), getBaseContext()).getPath());
                startActivity(intent);
                return true;
            }
        });

        tagFlowLayout4.setAdapter(tagAdapter4 = new TagAdapter<WorkFlowEntity>(process) {
            @Override
            public View getView(FlowLayout parent, int position, WorkFlowEntity entity) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag_work_do_process, parent, false);
                Group group = view.findViewById(R.id.group);
                View dividerOver = view.findViewById(R.id.dividerOver);
                if (position == 0) {
                    dividerOver.setVisibility(View.VISIBLE);
                } else {
                    dividerOver.setVisibility(View.GONE);
                }

                ImageView pointView = view.findViewById(R.id.flagView);
                TextView topTitleView = view.findViewById(R.id.textView78);
                TextView stateView = view.findViewById(R.id.textView100);
                if (showTitle(position)) {
                    group.setVisibility(View.VISIBLE);

                    topTitleView.setText(entity.getTitle());
                    if (entity.getMyType() == 1) {
                        pointView.setBackgroundResource(R.drawable.icon_point_blue);
                        topTitleView.setTextColor(parent.getResources().getColor(R.color.item_blue));
                    } else if (entity.getMyType() == 2) {
                        pointView.setBackgroundResource(R.drawable.icon_point_red);
                        topTitleView.setTextColor(parent.getResources().getColor(R.color.tag_red));
                    } else {
                        pointView.setBackgroundResource(R.drawable.icon_point_green);
                        topTitleView.setTextColor(parent.getResources().getColor(R.color.tag_green));
                    }
                    switch (entity.getState()) {
                        case "0":
                            stateView.setText("进行中");
                            break;
                        case "1":
                            stateView.setText("已完成");
                            break;
                        case "999":
                            stateView.setText("已验收");
                            break;
                    }


                } else {
                    group.setVisibility(View.GONE);
                }

                TextView infoView = view.findViewById(R.id.textView101);
                infoView.setText(entity.getInfo());
                TextView userView = view.findViewById(R.id.textView102);
                userView.setText(entity.getUser());
                TextView timeView = view.findViewById(R.id.textView103);
                timeView.setText(entity.getDate().substring(0, 16));

                ImageView headView = view.findViewById(R.id.headView);
                Util.setAvatar(parent.getContext(), entity.getAvatarUrl(), headView);

                return view;
            }

            private boolean showTitle(int position) {
                int preType = position == 0 ? -1 : process.get(position - 1).getMyType();
                int curType = process.get(position).getMyType();
                if (preType == curType) {
                    return false;
                }
                return true;
            }
        });

        editText = findViewById(R.id.editText6);
        quickView = findViewById(R.id.quickView);
        quickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    showQuickReplyWindow(v);
            }
        });

        addView = findViewById(R.id.addView);
        addView.setOnClickListener(this);

        scrollView = findViewById(R.id.scrollView);
        feedbackLayout = findViewById(R.id.feedbackLayout);
        feedbackLayout.setOnClickListener(this);
        quicklyLayout = findViewById(R.id.quicklyLayout);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int y = (int) event.getY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 第一次点击是 ACTION_DOWN 事件，把值保存起来
                        mMotionY = y;
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        mMotionY = 0;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 当你滑动屏幕时是 ACTION_MOVE 事件，在这里做逻辑处理
                        // （y - mMotionY） 的正负就代表了 向上和向下
                        if (mMotionY == 0) {
                            mMotionY = y;
                        } else {

                            if ((y - mMotionY) > 30) {
                                mMotionY = y;
                                if (quicklyLayout.getVisibility() == View.GONE) {
                                    quicklyLayout.setVisibility(View.VISIBLE);
                                }
                            } else if ((mMotionY - y) > 30) {
                                mMotionY = y;
                                if (quicklyLayout.getVisibility() == View.VISIBLE) {
                                    quicklyLayout.setVisibility(View.GONE);
                                }
                            }
                        }

                        break;
                }
                return false;
            }
        });


        actionLayout1 = findViewById(R.id.actionLayout1);
        actionLayout1.setOnClickListener(this);
        actionLayout1.setVisibility(View.GONE);

        actionLayout2 = findViewById(R.id.actionLayout2);
        actionLayout2.setOnClickListener(this);
        actionLayout2.setVisibility(View.GONE);

        actionLayout3 = findViewById(R.id.actionLayout3);
        actionLayout3.setOnClickListener(this);
        actionLayout2.setVisibility(View.GONE);

        getDetailInfo();
        getFileList();
        getWorkList();
        getQuicklyReply();
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.feedbackLayout:
                if (quicklyLayout.getVisibility() == View.GONE) {
                    quicklyLayout.setVisibility(View.VISIBLE);
                }else{
                    quicklyLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.actionView:
                if ("1".equals(entity.getIsFeedbak())) {
                    dialog = Util.showDialog(initDialogView(v.getContext()));
                } else {
                    String info = editText.getText().toString().trim();
                    if (TextUtils.isEmpty(info)) {
                        Util.showToast(v.getContext(), "请填写办理反馈");
                    } else {
                        submit();
                    }
                }
                break;
            case R.id.addView:
                pickerFile();
                break;
            case R.id.actionLayout1:
                if ("1".equals(entity.getIsDelay())) {
                    intent = new Intent(v.getContext(), ApplyDelayActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("entity", entity);
                    startActivityForResult(intent, 1);
                }
                break;
            case R.id.actionLayout2:
                intent = new Intent(v.getContext(), WorkReturnActivity.class);
                intent.putExtra("entity", entity);
                startActivityForResult(intent, 2);
                break;
            case R.id.actionLayout3:
                intent = new Intent(v.getContext(), PersonChoseActivity.class);
                intent.putExtra("type", 8);
                startActivityForResult(intent, 3);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    entity.setIsDelay("0");
                    actionLayout1.setVisibility(View.GONE);
                    break;
                case 2:
                    entity.setIsBackOffice("0");
                    actionLayout2.setVisibility(View.GONE);
                    break;
                case 3:
                    List<PersonEntity> list = data.getParcelableArrayListExtra("entities");
                    forwardUserIds.clear();
                    for (PersonEntity personEntity : list) {
                        forwardUserIds.add(personEntity.getId());
                    }
                    System.out.println("forwardUserIds=============" + forwardUserIds.toString());
                    break;
                case 4:
                    entity.setIsFeedbak("0");
                    workPlanInfoEntity = (WorkPlanInfoEntity) data.getSerializableExtra("entity");
                    System.out.println("workPlanInfoEntity=================" + workPlanInfoEntity);


                    break;
                case 10:
                    List<Uri> uriList = Matisse.obtainResult(data);
                    tagList3.clear();
                    tagList3.addAll(uriList);
                    tagAdapter3.notifyDataChanged();
                    for (Uri uri : uriList) {
                        fileList.add(Util.getFileByUri(uri, getBaseContext()));
                    }
                    upLoadFiles();
                    break;
            }
        }
    }

    private void pickerFile() {
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

    private void initView(WorkDoEntity entity) {
        if (entity == null) {
            return;
        }
        ((TextView) findViewById(R.id.textView45)).setText(entity.getSupervisionTitle());
        ((TextView) findViewById(R.id.textView48)).setText(entity.getSupervisionNumber());
        WorkDoEntity.Company[] leaderAccs = entity.getUserList().getLeaderAccs();
        if (leaderAccs != null && leaderAccs.length > 0) {

            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < leaderAccs.length; i++) {
                stringBuffer.append(leaderAccs[i].getUserName());
                if (i != leaderAccs.length - 1) {
                    stringBuffer.append(",");
                }
            }
            ((TextView) findViewById(R.id.textView70)).setText(stringBuffer.toString());
        }

        ((TextView) findViewById(R.id.textView57)).setText(Util.stempToStr(entity.getEndTime(), "yyyy-MM-dd"));
        ((TextView) findViewById(R.id.editText2)).setText(Html.fromHtml(entity.getSupervisionInfo()));
        ((TextView) findViewById(R.id.textView69)).setText(entity.getSupervisionTypeName());

        WorkDoEntity.Company[] firstComps = entity.getUserList().getFirstComps();
        if (firstComps != null && firstComps.length > 0) {
            for (int i = 0; i < firstComps.length; i++) {
                tagList1.add(new OptionEntity(1, firstComps[i].getComName()));
            }
        }
        WorkDoEntity.Company[] secondComps = entity.getUserList().getSecondComps();
        if (secondComps != null && secondComps.length > 0) {
            for (int i = 0; i < secondComps.length; i++) {
                tagList1.add(new OptionEntity(2, secondComps[i].getComName()));
            }
        }
        tagAdapter1.notifyDataChanged();

        if ("1".equals(entity.getIsDelay())) {
            actionLayout1.setVisibility(View.VISIBLE);
        }
        if ("1".equals(entity.getIsBackOffice())) {
            actionLayout2.setVisibility(View.VISIBLE);
        }
        actionLayout3.setVisibility(View.VISIBLE);

        System.out.println("getIsFeedbak==================" + entity.getIsFeedbak());
    }


    private View initDialogView(final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_4, null, false);
        view.findViewById(R.id.btn2View).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                Intent intent = new Intent(context, WorkPlanActivity.class);
                WorkDoEntity.Company[] firstComps = entity.getUserList().getFirstComps();
                StringBuffer stringBuffer = new StringBuffer();
                if (firstComps != null && firstComps.length > 0) {
                    for (int i = 0; i < firstComps.length; i++) {
                        stringBuffer.append(firstComps[i].getUserName());
                        if (i != firstComps.length - 1) {
                            stringBuffer.append(",");
                        }
                    }
                }
                intent.putExtra("person", stringBuffer.toString());
                intent.putExtra("time", Util.stempToStr(entity.getEndTime(), "yyyy-MM-dd"));
                startActivityForResult(intent, 4);
            }
        });
        return view;
    }


    PopupWindow window;
    TagAdapter quickAdapter;
    EditText quickEditText;
    List<OptionEntity> quickList = new ArrayList<>();
    private void showQuickReplyWindow(View anchor){
        View contentView= LayoutInflater.from(anchor.getContext()).inflate(R.layout.popup_quick_reply, null, false);
        TagFlowLayout tagFlowLayout=contentView.findViewById(R.id.tagFlowLayout);
        tagFlowLayout.setAdapter(quickAdapter=new TagAdapter<OptionEntity>(quickList) {
            @Override
            public View getView(FlowLayout parent, int position, final OptionEntity entity) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag_quick_reply, parent, false);
                TextView textView=view.findViewById(R.id.textView);
                textView.setText(entity.getValue());
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        window.dismiss();
                        editText.setText(((TextView)v).getText().toString());
                        editText.setSelection(editText.length());
                    }
                });
                view.findViewById(R.id.deleteView).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteQuick(entity.getKey());
                    }
                });
                return view;
            }
        });
        quickEditText=contentView.findViewById(R.id.editText);
        contentView.findViewById(R.id.addView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info=quickEditText.getText().toString().trim();
                if ((!TextUtils.isEmpty(info))&&quickList.size()<5) {
                    addQuick(info,quickEditText);
                }else{
                    if (quickList.size()>=5) {
                        Util.showToast(v.getContext(),"快速回复最多5条");
                    }else{
                        Util.showToast(v.getContext(),"请输入快速回复内容");
                    }
                }
            }
        });

        window=new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,  ViewGroup.LayoutParams.WRAP_CONTENT, true);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setOutsideTouchable(true);
        window.setTouchable(true);
//        window.showAsDropDown(anchor,-Util.dp2px(this,160),-Util.dp2px(this,130));
        window.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        window.setFocusable(true);
        window.showAtLocation(anchor, Gravity.BOTTOM,0,0);

        WindowManager.LayoutParams lp=getWindow().getAttributes();
        lp.alpha=0.5f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);

        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp=getWindow().getAttributes();
                lp.alpha=1.0f;
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    initView(entity);
                    break;
                case 2:
                    break;
                case 3:
                    tagAdapter2.notifyDataChanged();
                    break;
                case 4:
                    tagAdapter3.notifyDataChanged();
                    break;
                case 5:
                    tagAdapter4.notifyDataChanged();
                    break;
                case 6:
                    Util.showToast(getBaseContext(), msg.obj.toString());
                    break;
            }

        }
    };

    private void getDetailInfo() {
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
                JSONObject object = new JSONObject();
                try {
                    object.putOpt("id", id);
                    object.putOpt("taskId", taskId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return object;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("getDetailInfo===============" + jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
                        entity = new Gson().fromJson(jsonObj.getString("data"), WorkDoEntity.class);
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

    private void getScheduleTypes() {
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
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
                        scheduleTypes.clear();
                        JSONArray array = jsonObj.getJSONArray("data");
                        for (int i = 0; i < array.length(); i++) {
                            scheduleTypes.put(array.getJSONObject(i).getString("kay"), array.getJSONObject(i).getString("name"));
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

    private void getFileList() {
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/supervision/list/upload/files";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("supervisionId", id + "");
                map.put("pageNum", "1");
                map.put("pageSize", "100");

                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("getFileList================" + jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
                        tagList2.clear();

                        List<FileEntity> entities = new Gson().fromJson(jsonObj.getString("data"), new TypeToken<List<FileEntity>>() {
                        }.getType());
                        if (entities.size() > 0) {
                            for (FileEntity fileEntity : entities) {
                                tagList2.add(fileEntity);
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

    private void getWorkList() {
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

                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
                        process.clear();
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
                            process.addAll(listA);
                        }
                        handler.sendEmptyMessage(5);
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


    private void upLoadFiles() {
        dialog = Util.showLoadingDialog(this, "附件上传中…");
        RequestUtil.uploadFiles(fileList, new RequestListener() {
            @Override
            public String getUrl() {
                return "/supervision/uploadFileListBySuperId";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("id", id + "");
                return map;
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
                System.out.println("upLoadFiles================" + jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
//                        List<SupervisionEntity.UploadFile> list = new Gson().fromJson(jsonObj.getString("data"), new TypeToken<List<SupervisionEntity.UploadFile>>() {}.getType());
                        handler.obtainMessage(6, "附件上传成功！").sendToTarget();

                    } else {
                        handler.obtainMessage(6, jsonObj.getString("message")).sendToTarget();
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
                handler.obtainMessage(6, getString(R.string.error_server)).sendToTarget();
            }
        });
    }

    private void getQuicklyReply() {
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/instruction/findFeedbackExplain";
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
                System.out.println("findFeedbackExplain=============" + jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
                        JSONArray array = jsonObj.getJSONArray("data");
                        quickList.clear();
                        for (int i = 0; i < array.length(); i++) {
                            quickList.add(new OptionEntity(array.getJSONObject(i).getInt("id"),(array.getJSONObject(i).getString("content"))));
                        }
                        if (quickAdapter != null) {
                            quickAdapter.notifyDataChanged();
                        }
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

    private void addQuick(final String content, final View view){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/instruction/addFeedbackExplain";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> map=new HashMap<>();
                map.put("content",content);
                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("addQuick============="+jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1){
                        ((TextView)view).setText("");
                        getQuicklyReply();
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

    private void deleteQuick(final int id){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/instruction/deleteFeedbackExplainById";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> map=new HashMap<>();
                map.put("id",id+"");
                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("deleteQuick============="+jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1){
                        getQuicklyReply();
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

    private void submit() {
        dialog = Util.showLoadingDialog(this, "提交中…");
        RequestUtil.jsonObjectRequest(new RequestListener() {
            @Override
            public String getUrl() {
                return "/WorkTask/handleTask";
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
                    object.putOpt("taskId", taskId);
                    JSONArray jsonArray = new JSONArray();
                    for (Integer forwardUserId : forwardUserIds) {
                        jsonArray.put(forwardUserId);
                    }
                    object.put("userId", jsonArray);
                    object.putOpt("supervisionInfo", editText.getText().toString().trim());
                    if (workPlanInfoEntity != null) {
                        object.putOpt("workPlanInfo", new JSONObject(new Gson().toJson(workPlanInfoEntity)));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("参数==============" + object);
                return object;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                int code;
                try {
                    code = jsonObj.getInt("code");
                    if (code == 1) {
                        EventBus.getDefault().post(new RefreshEvent(RefreshEvent.Refresh_WorkList));
                        finish();
                    } else {
                        Util.showToast(getApplication(), jsonObj.getString("message"));
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
                Util.showToast(getApplication(), getString(R.string.error_server));
            }
        });
    }


}
