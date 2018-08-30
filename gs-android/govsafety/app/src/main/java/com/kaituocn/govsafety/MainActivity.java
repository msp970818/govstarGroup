package com.kaituocn.govsafety;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kaituocn.govsafety.entity.UserEntity;
import com.kaituocn.govsafety.util.CircleView;
import com.kaituocn.govsafety.util.RequestListener;
import com.kaituocn.govsafety.util.RequestUtil;
import com.kaituocn.govsafety.util.SharedPreferencesUtils;
import com.kaituocn.govsafety.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView action1View, action2View;
    CircleView circleView;
    TextView timeView;
    TextView numView;
    TextView showClientTV;

    UserEntity entity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView titleView = findViewById(R.id.titleView);
        titleView.setText("政务安全宝");

        String userinfo= SharedPreferencesUtils.getData(this,SharedPreferencesUtils.KEY_ENTITY);
        if (!TextUtils.isEmpty(userinfo)) {
            entity=new Gson().fromJson(userinfo,UserEntity.class);
        }

        System.out.println("========="+Util.getSerialNumber(this));

        action1View = findViewById(R.id.action1View);
        action2View = findViewById(R.id.action2View);
        action1View.setVisibility(View.VISIBLE);
        action2View.setVisibility(View.VISIBLE);
        action1View.setImageResource(R.drawable.icon_scan);
        action2View.setImageResource(R.drawable.icon_set);
        action1View.setOnClickListener(this);
        action2View.setOnClickListener(this);
//        action2View.setVisibility(View.GONE);

        initViewData(entity);

        showClientTV = findViewById(R.id.textView4);
        showClientTV.setOnClickListener(this);
        showClientTV.setVisibility(View.GONE);

        circleView = findViewById(R.id.circleView);
        timeView = findViewById(R.id.timeView);
//        timeView.setText("60");
//        circleView.setProgress(600);
        numView = findViewById(R.id.numView);

        getVerify();
    }

    private void initViewData(UserEntity entity){
        if (entity == null) {
            return;
        }
        ((TextView)findViewById(R.id.textView5)).setText("用户ID："+entity.getComName()+"-"+entity.getDepName()+"-"+entity.getName());
        ((TextView)findViewById(R.id.nameView)).setText("用户："+entity.getName());
        ((TextView)findViewById(R.id.textView9)).setText("机关："+entity.getComName()+" - "+entity.getDepName());
        ((TextView)findViewById(R.id.textView10)).setText("上次登录IP："+entity.getLastTimeLoginIp());

    }

    private CountDownTimer timer = new CountDownTimer(60 * 1000, 200) {
        @Override
        public void onTick(long millisUntilFinished) {
            circleView.setProgress((int) (millisUntilFinished / 100));
            timeView.setText("" + millisUntilFinished / 1000);
            if (millisUntilFinished / 1000 > 15) {
                numView.setTextColor(0xff666666);
            } else {
                numView.setTextColor(getResources().getColor(R.color.gs_red));
            }
        }

        @Override
        public void onFinish() {
            circleView.setProgress(0);
            timeView.setText("0");
            numView.setText("");
            getVerify();
        }
    };



    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.action1View:
                if (checkPermission(v.getContext())) {
                    intent = new Intent(v.getContext(), ScanActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.action2View:
                intent = new Intent(v.getContext(), SetActivity.class);
                startActivity(intent);
                break;
            case R.id.textView4:
                intent = new Intent(v.getContext(), ClientsActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoginDevicesInfo();
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String verifyVode=msg.obj.toString();
                    StringBuffer stringBuffer=new StringBuffer(verifyVode.substring(0,3)).append("  ").append(verifyVode.substring(3,6));
                    numView.setText(stringBuffer.toString());
                    timeView.setText("60");
                    circleView.setProgress(600);
                    timer.start();
                    getLoginDevicesInfo();
                    break;
            }
        }
    };


    private void getVerify() {
        if (Util.DEBUG) {
            handler.obtainMessage(1, "666666").sendToTarget();
            return;
        }
        RequestUtil.jsonObjectRequest(new RequestListener() {
            @Override
            public String getUrl() {
                return "/GsSafe/getVerifyCode";
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
                System.out.println("getVerify============" + jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
                        JSONObject data = jsonObj.getJSONObject("data");
                        String verifyCode = data.getString("verifyCode");
                        handler.obtainMessage(1, verifyCode).sendToTarget();
                    } else {
                        Util.showToast(getBaseContext(), jsonObj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void getLoginDevicesInfo(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/GsSafe/checkSystem";
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
                System.out.println("checkSystem================="+jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1){
                        JSONArray array1=jsonObj.getJSONObject("data").getJSONArray("mobileInformations");
                        JSONArray array2=jsonObj.getJSONObject("data").getJSONArray("pcInformations");
                        int devicesNum=array1.length()+array2.length();
                        showClientTV.setText(String.format("当前有%d个系统正在登录中…点击查看",devicesNum));
                        if (devicesNum==0) {
                            showClientTV.setVisibility(View.GONE);
                        }else{
                            showClientTV.setVisibility(View.VISIBLE);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean checkPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
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
                        Util.ShowToast(this, "请打开相机权限");
                    } else {
                        Intent intent = new Intent(this, ScanActivity.class);
                        startActivity(intent);
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
        super.onDestroy();
    }
}
