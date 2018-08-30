package com.kaituocn.govstar.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import com.kaituocn.govstar.R;
import com.kaituocn.govstar.main.MainActivity;
import com.kaituocn.govstar.util.Constant;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.SharedPreferencesUtils;
import com.kaituocn.govstar.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class FlashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_flash);

        if (checkPermission(this)) {
            getSystemConfig();
        }
    }

    private boolean checkPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length == 1) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        finish();
                    } else {
                        getSystemConfig();
                    }
                }
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1) {
                if(!(Boolean)SharedPreferencesUtils.getParam(getBaseContext(),SharedPreferencesUtils.KEY_GUIDE,false)){
                    Intent intent=new Intent(getBaseContext(),GuideActivity.class);
                    startActivity(intent);
                    finish();
                }else if(!(Boolean)SharedPreferencesUtils.getParam(getBaseContext(),SharedPreferencesUtils.KEY_FIRST_USE,false)){
                    Intent intent=new Intent(getBaseContext(),Step1Activity.class);
                    startActivity(intent);
                    finish();

                }else if(TextUtils.isEmpty(SharedPreferencesUtils.getData(getBaseContext(),SharedPreferencesUtils.KEY_AUTHKEY))){
                    Intent intent=new Intent(getBaseContext(),LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else if((Boolean)SharedPreferencesUtils.getParam(getBaseContext(),SharedPreferencesUtils.KEY_GESTURE,false)){
                    Intent intent=new Intent(getBaseContext(), GestureLockerActivity.class);
                    intent.putExtra("unlock",true);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }
    };

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
                        String fileBaseUrl=jsonObj.getJSONObject("data").getString("FileBaseUrl");
                        SharedPreferencesUtils.setParam(getApplication(),SharedPreferencesUtils.KEY_FILE_BASE_URL,fileBaseUrl);
                        Constant.FILE_BASE_URL=fileBaseUrl;
                        handler.sendEmptyMessageDelayed(1,1000);
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
