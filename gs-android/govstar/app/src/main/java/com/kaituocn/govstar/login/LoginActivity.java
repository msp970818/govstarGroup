package com.kaituocn.govstar.login;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kaituocn.govstar.R;
import com.kaituocn.govstar.set.OperationInfoActivity;
import com.kaituocn.govstar.set.ServiceClauseActivity;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.SharedPreferencesUtils;
import com.kaituocn.govstar.util.Util;
import com.kaituocn.govstar.work.WebViewActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    Button loginBtn;
    TextView agreementView,forgetView,serviceView;
    EditText accountView,pswView;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_login);

        loginBtn=findViewById(R.id.button);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkInput()) {
                    Intent intent=new Intent(getBaseContext(), VerificationActivity.class);
                    startActivityForResult(intent,1);
                }

            }
        });

        accountView=findViewById(R.id.accountView);
        pswView=findViewById(R.id.pswView);

        agreementView=findViewById(R.id.agreementView);
        agreementView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),ServiceClauseActivity.class);
                startActivity(intent);
            }
        });
        forgetView=findViewById(R.id.forgetView);
        serviceView=findViewById(R.id.serviceView);
        forgetView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        serviceView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        forgetView.getPaint().setAntiAlias(true);
        serviceView.getPaint().setAntiAlias(true);

        forgetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });

        serviceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(v);
            }
        });

        checkPermission(this);
    }

    private boolean checkInput(){
        if (TextUtils.isEmpty(accountView.getText().toString().trim())) {
            Util.showToast(this,"请输入用户账号");
            return false;
        }
        if (TextUtils.isEmpty(pswView.getText().toString().trim())) {
            Util.showToast(this,"请输入密码");
            return false;
        }
        return true;
    }

    private void showPopupWindow(View anchor){
        View contentView= LayoutInflater.from(anchor.getContext()).inflate(R.layout.popup_service, null, false);
        contentView.findViewById(R.id.textView1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),WebViewActivity.class);
                intent.putExtra("res",R.drawable.service_page);
                startActivity(intent);
            }
        });
        contentView.findViewById(R.id.textView2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),OperationInfoActivity.class);
                startActivity(intent);
            }
        });
        PopupWindow window=new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT,  ViewGroup.LayoutParams.WRAP_CONTENT, true);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setOutsideTouchable(true);
        window.setTouchable(true);
        window.showAsDropDown(anchor,-Util.dp2px(this,160),-Util.dp2px(this,130));
//        window.showAtLocation(anchor,Gravity.BOTTOM| Gravity.RIGHT,Util.dp2px(this,16),Util.dp2px(this,40));

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1) {
            if (resultCode==RESULT_OK) {
                String verifyCode=data.getStringExtra("verifyCode");
                login(verifyCode);
            }
        }
    }



    private void login(final String verifyCode){
        dialog=Util.showLoadingDialog(this,"进入系统中，请等待…");
        RequestUtil.jsonObjectRequest(new RequestListener() {
            @Override
            public String getUrl() {
                return "/GsMobileUser/login";
            }

            @Override
            public Map<String, String> getParams() {
                return null;
            }

            @Override
            public JSONObject getJsonObj() {
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.putOpt("loginIsSave","0")
                            .putOpt("nickName",accountView.getText().toString().trim())
                            .putOpt("passport",pswView.getText().toString().trim())
                            .putOpt("platform","Android")
                            .putOpt("appType","GS")
                            .putOpt("uuid",Util.getSerialNumber(getBaseContext()))
                            .putOpt("name", Util.getSystemModel())
                            .putOpt("nameb","")
                            .putOpt("brand",Util.getDeviceBrand())
                            .putOpt("imei",Util.getSerialNumber(getBaseContext()))
                            .putOpt("verifyCode",verifyCode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonObject;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("======="+jsonObj.toString());
                dialog.dismiss();
                try {
                    int code = jsonObj.getInt("code");
                    if (code==1) {
                        JSONObject data = jsonObj.getJSONObject("data");
                        SharedPreferencesUtils.setData(getBaseContext(),SharedPreferencesUtils.KEY_AUTHKEY,data.getJSONObject("personInfo").getString("authKey"));
                        SharedPreferencesUtils.setData(getBaseContext(),SharedPreferencesUtils.KEY_UID,data.getJSONObject("personInfo").getString("id"));
                        SharedPreferencesUtils.setData(getBaseContext(),SharedPreferencesUtils.KEY_USER,data.toString());

                        Intent intent=new Intent(getBaseContext(), GestureLockerActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Util.showToast(getBaseContext(),jsonObj.getString("message"));
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

    private void loginCheck(){
        RequestUtil.jsonObjectRequest(new RequestListener() {
            @Override
            public String getUrl() {
                return "/GsMobileUser/loginIsOutTime";
            }

            @Override
            public Map<String, String> getParams() {
                return null;
            }

            @Override
            public JSONObject getJsonObj() {
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.putOpt("loginIsSave","0")
                            .putOpt("passport","l123465")
                            .putOpt("platform","Android")
                            .putOpt("uuid",Util.getSerialNumber(getBaseContext()))
                            .putOpt("authKey",SharedPreferencesUtils.getData(getBaseContext(),SharedPreferencesUtils.KEY_AUTHKEY));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonObject;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("loginCheck======="+jsonObj.toString());
                try {
                    int code = jsonObj.getInt("code");
                    if (code==1) {
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

                    }
                }
                break;
        }
    }


    public static void startLoginActivity(Activity activity){
        Intent intent=new Intent(activity,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.activity_enter_next, R.anim.activity_exit_next);
    }

}
