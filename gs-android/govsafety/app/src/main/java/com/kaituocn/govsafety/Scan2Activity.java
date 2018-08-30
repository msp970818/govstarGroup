package com.kaituocn.govsafety;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.kaituocn.govsafety.util.RequestListener;
import com.kaituocn.govsafety.util.RequestUtil;
import com.kaituocn.govsafety.util.SharedPreferencesUtils;
import com.kaituocn.govsafety.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Scan2Activity extends AppCompatActivity {

    String code;
    Button loginBtn,CancelBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_scan2);

        code=getIntent().getStringExtra("code");

        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("扫描完成立即登录");
        View backView=findViewById(R.id.action1View);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loginBtn=findViewById(R.id.button5);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanLogin();
            }
        });
        CancelBtn=findViewById(R.id.button4);
        CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void scanLogin(){
        RequestUtil.jsonObjectRequest(new RequestListener() {
            @Override
            public String getUrl() {
                return "/GsSafe/scavengingLogin";
            }

            @Override
            public Map<String, String> getParams() {

                return null;
            }

            @Override
            public JSONObject getJsonObj() {
                JSONObject object=new JSONObject();
                try {
                    object.putOpt("loginId",SharedPreferencesUtils.getData(getBaseContext(),SharedPreferencesUtils.KEY_USERID));
                    object.putOpt("code",code);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return object;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("scanLogin==============="+jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
                        finish();
                    }else{
                        Util.showToast(getBaseContext(),jsonObj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
