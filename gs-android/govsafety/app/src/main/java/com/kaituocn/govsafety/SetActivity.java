package com.kaituocn.govsafety;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kaituocn.govsafety.entity.SetEntity;
import com.kaituocn.govsafety.login.Login1Activity;
import com.kaituocn.govsafety.login.Login2Activity;
import com.kaituocn.govsafety.util.RequestListener;
import com.kaituocn.govsafety.util.RequestUtil;
import com.kaituocn.govsafety.util.SharedPreferencesUtils;
import com.kaituocn.govsafety.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SetActivity extends AppCompatActivity {

    SetEntity entity;

    CheckBox checkBox1,checkBox2;
    Button logoutBtn;
    boolean authorityError,verifyError;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("设置账号安全");
        View backView=findViewById(R.id.action1View);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        checkBox1=findViewById(R.id.checkbox1);
        checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (authorityError) {
                    authorityError=false;
                }else{
                    authority(isChecked);
                }
            }
        });
        checkBox2=findViewById(R.id.checkbox2);
        checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (verifyError) {
                    verifyError=false;
                }else{
                    verify(isChecked);
                }
            }
        });

        logoutBtn=findViewById(R.id.button6);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        getDetailInfo();
    }

    private void initView(SetEntity entity){
        checkBox1.setChecked(entity.getOpenTemporary()==1);
        checkBox2.setChecked(entity.getVerification()==1);
        ((TextView)findViewById(R.id.textView4)).setText("当前绑定账号："+(entity.getUserName()==null?"":entity.getUserName()));
        ((TextView)findViewById(R.id.textView5)).setText("初次绑定日期："+(entity.getFirstDate()==null?"":entity.getFirstDate()));
        ((TextView)findViewById(R.id.textView6)).setText("绑定设备："+(entity.getName()==null?"":entity.getName()));
        ((TextView)findViewById(R.id.textView7)).setText("IMEI："+(entity.getImei()==null?"":entity.getImei()));
        ((TextView)findViewById(R.id.textView8)).setText("品牌："+(entity.getBrand()==null?"":entity.getBrand()));
    }


    private void getDetailInfo(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/GsSafe/setInformation";
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
                System.out.println("============="+jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
                        entity=new Gson().fromJson(jsonObj.getJSONObject("data").getString("information"),SetEntity.class);
                        if(jsonObj.getJSONObject("data").has("openTemporary")){
                            entity.setOpenTemporary(jsonObj.getJSONObject("data").getInt("openTemporary"));
                        }
                        if(jsonObj.getJSONObject("data").has("verification")){
                            entity.setVerification(jsonObj.getJSONObject("data").getInt("verification"));
                        }
                        initView(entity);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

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

                    }else {
                        authorityError=true;
                        checkBox1.setChecked(!isChecked);
                        Util.showToast(getApplication(),jsonObj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void verify(final boolean isChecked){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/GsSafe/safeVerify";
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
                System.out.println("verify============="+jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code==1) {

                    }else {
                        verifyError=true;
                        checkBox2.setChecked(!isChecked);
                        Util.showToast(getApplication(),jsonObj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void logout(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/GsSafe/safeLogOut";
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
                System.out.println("safeLogOut================"+jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code==1) {
                        SharedPreferencesUtils.setParam(getApplication(),SharedPreferencesUtils.KEY_AUTHKEY,"");
                        Login1Activity.startLoginActivity(SetActivity.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
