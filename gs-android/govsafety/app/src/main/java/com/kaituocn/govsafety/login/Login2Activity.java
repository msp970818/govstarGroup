package com.kaituocn.govsafety.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kaituocn.govsafety.MainActivity;
import com.kaituocn.govsafety.R;
import com.kaituocn.govsafety.entity.LoginEntity;
import com.kaituocn.govsafety.util.RequestListener;
import com.kaituocn.govsafety.util.RequestUtil;
import com.kaituocn.govsafety.util.SharedPreferencesUtils;
import com.kaituocn.govsafety.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class Login2Activity extends AppCompatActivity {

    TextView inputView;
    Button bindBtn, sendAgainBtn;

    AlertDialog dialog;

    String phone;
    LoginEntity entity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        TextView titleView = findViewById(R.id.titleView);
        titleView.setText("验证并绑定");
        View backView = findViewById(R.id.action1View);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        phone = getIntent().getStringExtra("phone");
        entity= (LoginEntity) getIntent().getSerializableExtra("entity");

        inputView = findViewById(R.id.editText3);
        bindBtn = findViewById(R.id.button2);
        bindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(inputView.getText().toString().trim())) {
                    entity.setCommonCode(inputView.getText().toString().trim());
                    login();
                }else{
                    Util.showToast(v.getContext(),"请输入验证码！");
                }
            }
        });
        sendAgainBtn = findViewById(R.id.button3);
        sendAgainBtn.setEnabled(false);
        sendAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.start();
                sendVerifyCode();
            }
        });

        timer.start();

        sendVerifyCode();

    }

    private CountDownTimer timer = new CountDownTimer(120 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            sendAgainBtn.setText("重新发送验证码(" + millisUntilFinished / 1000 + ")");
        }

        @Override
        public void onFinish() {
            sendAgainBtn.setText("重新发送验证码");
            sendAgainBtn.setEnabled(true);
        }
    };

    private View initDialogBindSuccess(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_1, null, false);
        TextView titleView = view.findViewById(R.id.titleView);
        titleView.setText("绑定成功");
        titleView.setTextColor(getResources().getColor(R.color.dilog_green));
        TextView infoView = view.findViewById(R.id.infoView);
        infoView.setText("您已成功绑定账号，请登录系统时打开安全宝，输入动态安全码。");
        Util.setBackgroundTint(view.findViewById(R.id.actionLayout), R.color.dilog_green);
        TextView btn1View = view.findViewById(R.id.btn1View);
        btn1View.setText("立即使用");
        btn1View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        view.findViewById(R.id.btn2View).setVisibility(View.GONE);


        return view;

    }

    private View initDialogVerifyError(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_1, null, false);
        TextView titleView = view.findViewById(R.id.titleView);
        titleView.setText("验证码输入超时");
//        titleView.setTextColor(getResources().getColor(R.color.dilog_gray));
        TextView infoView = view.findViewById(R.id.infoView);
        infoView.setText("您的验证码输入超时，请重新发送验证码！");
        Util.setBackgroundTint(view.findViewById(R.id.actionLayout), R.color.dilog_gray);
        TextView btn1View = view.findViewById(R.id.btn1View);
        btn1View.setText("知道了");
        btn1View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }

            }
        });
        view.findViewById(R.id.btn2View).setVisibility(View.GONE);


        return view;

    }
    private void sendVerifyCode() {
        RequestUtil.jsonObjectRequest(new RequestListener() {
            @Override
            public String getUrl() {
                return "/CommonController/getSendSmsVerifyCode";
            }

            @Override
            public Map<String, String> getParams() {
                return null;
            }

            @Override
            public JSONObject getJsonObj() {
                JSONObject object = new JSONObject();
                try {
                    object.putOpt("nickName", phone);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return object;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("jsonObj===========" + jsonObj.toString());
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1) {
//                        Util.showToast(getBaseContext(),"验证码已发出");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void login(){
        RequestUtil.jsonObjectRequest(new RequestListener() {
            @Override
            public String getUrl() {
                return "/GsSafe/safeLogin";
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
                System.out.println("login par=============="+jsonObject);
                return jsonObject;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("login==========" + jsonObj.toString());
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
                        JSONObject data = jsonObj.getJSONObject("data").getJSONObject("client").getJSONObject("user");
                        String authKey = data.getString("authKey");
                        String id = data.getString("id");
                        System.out.println("authKey=======" + authKey);
                        System.out.println("id=======" + id);
                        SharedPreferencesUtils.setData(getBaseContext(), SharedPreferencesUtils.KEY_AUTHKEY, authKey);
                        SharedPreferencesUtils.setData(getBaseContext(), SharedPreferencesUtils.KEY_USERID, id);
                        SharedPreferencesUtils.setData(getBaseContext(), SharedPreferencesUtils.KEY_ENTITY, data.toString());

                        dialog = Util.showDialog(initDialogBindSuccess(getBaseContext()));


                    } else {
//                        Util.showToast(getBaseContext(), jsonObj.getString("message"));
                        dialog = Util.showDialog(initDialogVerifyError(getBaseContext()));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
