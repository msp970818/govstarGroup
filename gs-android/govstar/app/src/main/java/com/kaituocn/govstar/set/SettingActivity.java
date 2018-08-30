package com.kaituocn.govstar.set;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.login.GestureLockerActivity;
import com.kaituocn.govstar.login.LoginActivity;
import com.kaituocn.govstar.util.Constant;
import com.kaituocn.govstar.util.DataCleanManager;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.SharedPreferencesUtils;
import com.kaituocn.govstar.util.Util;
import com.kaituocn.govstar.yunav.TeamAVChatProfile;
import com.kaituocn.govstar.yunav.player.LiveActivity;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener{

    Dialog dialog;
    CheckBox bindCheckBox, faceCheckBox, gestureCheckBox, noticeCheckBox, authorCheckBox;

    boolean authorityError;
    int isOpenTemporary;//1为开启，0为关闭',

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_setting);

        TextView titleView = findViewById(R.id.titleView);
        titleView.setText("系统设置");
        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(), LiveActivity.class);
                intent.putExtra("videoPath", "rtmp://v637a7e0f.live.126.net/live/3da45850d0174e3ab962607d259e8581");
                startActivity(intent);
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

        findViewById(R.id.button5).setOnClickListener(this);
        findViewById(R.id.button6).setOnClickListener(this);

        bindCheckBox=findViewById(R.id.checkBox2);
        faceCheckBox=findViewById(R.id.checkBox3);
        gestureCheckBox=findViewById(R.id.checkBox4);
        if((Boolean)SharedPreferencesUtils.getParam(getBaseContext(),SharedPreferencesUtils.KEY_GESTURE,false)){
            gestureCheckBox.setChecked(true);
        }else{
            gestureCheckBox.setChecked(false);
        }
        noticeCheckBox =findViewById(R.id.checkBox5);
        if (JPushInterface.isPushStopped(getApplication())) {
            noticeCheckBox.setChecked(false);
        }else{
            noticeCheckBox.setChecked(true);
        }
        authorCheckBox=findViewById(R.id.checkBox6);

        bindCheckBox.setOnCheckedChangeListener(this);
        faceCheckBox.setOnCheckedChangeListener(this);
        gestureCheckBox.setOnCheckedChangeListener(this);
        noticeCheckBox.setOnCheckedChangeListener(this);
        authorCheckBox.setOnCheckedChangeListener(this);

        getSystemConfig();

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.checkBox2://与本机绑定
                if (isChecked) {
                    authorCheckBox.setChecked(false);
                }else{
                    authorCheckBox.setChecked(true);
                }
                break;
            case R.id.checkBox3://面部识别
                break;
            case R.id.checkBox4://手势识别
                if (isChecked) {
                    String password= SharedPreferencesUtils.getData(this,SharedPreferencesUtils.KEY_GESTURE_CODE);
                    if (TextUtils.isEmpty(password)) {
                        Intent intent=new Intent(getBaseContext(), GestureLockerActivity.class);
                        intent.putExtra("isSetting",true);
                        startActivityForResult(intent,1);
                    }else{
                        SharedPreferencesUtils.setParam(getApplication(),SharedPreferencesUtils.KEY_GESTURE,true);
                    }
                }else{
                    SharedPreferencesUtils.setParam(getApplication(),SharedPreferencesUtils.KEY_GESTURE,false);
                }
                break;
            case R.id.checkBox5://系统通知
                if (isChecked) {
                    if(JPushInterface.isPushStopped(getApplication())){
                        JPushInterface.resumePush(getApplication());
                    }
                }else{
                    if(!JPushInterface.isPushStopped(getApplication())){
                        JPushInterface.stopPush(getApplication());
                    }
                }
                break;
            case R.id.checkBox6://临时授权
                if (authorityError) {
                    authorityError=false;
                }else{
                    authority(isChecked);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button5:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(getBaseContext()).clearDiskCache();
                        DataCleanManager.clearAllCache(getBaseContext());
                        handler.sendEmptyMessage(1);
                    }
                }).start();
                break;
            case R.id.button6:
                dialog = Util.showDialog(initDialogView(v.getContext()));
//                dialog=Util.showLoadingDialog(v.getContext(),null);
//                MyDialogFragment dialogFragment=MyDialogFragment.newInstance("hhhh");
//                dialogFragment.show(getFragmentManager(),"fragment");
                break;
            case R.id.btn1View:
                if (dialog != null) {
                    dialog.dismiss();
                }
                break;
            case R.id.btn2View:
                if (dialog != null) {
                    dialog.dismiss();
                }
                SharedPreferencesUtils.setData(this, SharedPreferencesUtils.KEY_AUTHKEY, "");
                SharedPreferencesUtils.setData(this, SharedPreferencesUtils.KEY_YUN_TOKEN, "");
                SharedPreferencesUtils.setData(this, SharedPreferencesUtils.KEY_JPUSH_ALIAS, "");
                TeamAVChatProfile.sharedInstance().registerObserver(false);
                NIMClient.getService(AuthService.class).logout();
                JPushInterface.deleteAlias(getApplicationContext(),1);
                LoginActivity.startLoginActivity(this);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1) {
            if (resultCode==RESULT_OK) {
                SharedPreferencesUtils.setParam(getApplication(),SharedPreferencesUtils.KEY_GESTURE,true);
            }else {
                gestureCheckBox.setChecked(false);

            }
        }
    }

    private View initDialogView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_1, null, false);
        view.findViewById(R.id.btn1View).setOnClickListener(this);
        view.findViewById(R.id.btn2View).setOnClickListener(this);
        return view;
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Util.showToast(getBaseContext(), "缓存已清除");
                    break;
            }
        }
    };


    private void authority(final boolean isChecked){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/GsSafe/provisionalAuthority";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> map=new HashMap<>();
                map.put("state",isChecked?"1":"0");
                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("authority============="+jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code==1) {
                        bindCheckBox.setChecked(!isChecked);
                    }else {
                        authorityError=true;
                        authorCheckBox.setChecked(!isChecked);
                        Util.showToast(getApplication(),jsonObj.getString("message"));
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


    private void getSystemConfig(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/CommonController/GetSystemConfig";
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
                System.out.println("jsonObj=================="+jsonObj);
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1){
                        isOpenTemporary=jsonObj.getJSONObject("data").getInt("isOpenTemporary");
                        if (isOpenTemporary==1) {
                            authorCheckBox.setChecked(true);
                            bindCheckBox.setChecked(false);
                        }else{
                            authorCheckBox.setChecked(false);
                            bindCheckBox.setChecked(true);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                String fileBaseUrl="https://gsstar.oss-cn-north-2-gov-1.aliyuncs.com/";
                SharedPreferencesUtils.setParam(getApplication(),SharedPreferencesUtils.KEY_FILE_BASE_URL,fileBaseUrl);
                Constant.FILE_BASE_URL=fileBaseUrl;
                handler.sendEmptyMessage(1);
            }
        });
    }

}
