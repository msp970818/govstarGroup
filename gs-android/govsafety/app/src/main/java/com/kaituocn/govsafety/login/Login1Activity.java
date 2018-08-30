package com.kaituocn.govsafety.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kaituocn.govsafety.MainActivity;
import com.kaituocn.govsafety.R;
import com.kaituocn.govsafety.ScanActivity;
import com.kaituocn.govsafety.entity.LoginEntity;
import com.kaituocn.govsafety.util.CodeUtils;
import com.kaituocn.govsafety.util.RequestListener;
import com.kaituocn.govsafety.util.RequestUtil;
import com.kaituocn.govsafety.util.SharedPreferencesUtils;
import com.kaituocn.govsafety.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import static android.os.Build.FINGERPRINT;

public class Login1Activity extends AppCompatActivity {

    EditText accountView, pswView;
    Button nextBtn;
    AlertDialog dialog;

    String verifyCode;

    LoginEntity entity=new LoginEntity();

    int accountErrorNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);
        TextView titleView = findViewById(R.id.titleView);
        titleView.setText("输入用户名与密码");



        accountView = findViewById(R.id.editText);
        pswView = findViewById(R.id.editText2);

        nextBtn = findViewById(R.id.button);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    login_1();
                }
            }
        });

        if (checkPermission(this)) {

            if ((Boolean) SharedPreferencesUtils.getParam(this, SharedPreferencesUtils.KEY_FIRST, true)) {
                dialog = Util.showDialog(initDialogFirstUse(this));
            }
        }


    }


    private boolean checkInput() {
        if (TextUtils.isEmpty(accountView.getText().toString().trim())) {
            Util.showToast(this, "请输入用户账号");
            return false;
        }
        if (!Util.checkPhoneNum(accountView.getText().toString().trim())) {
            Util.showToast(this, "手机格式错误");
            return false;
        }
        if (TextUtils.isEmpty(pswView.getText().toString().trim())) {
            Util.showToast(this, "请输入密码");
            return false;
        }
        return true;
    }



    private View initDialogFirstUse(final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_1, null, false);
        TextView titleView = view.findViewById(R.id.titleView);
        titleView.setText("首次使用请先绑定设备");
        titleView.setTextColor(getResources().getColor(R.color.dilog_blue));
        TextView infoView = view.findViewById(R.id.infoView);
        infoView.setText("第一次下载使用政务安全宝请先绑定您的系统账号！");
        TextView btn1View = view.findViewById(R.id.btn1View);
        btn1View.setText("立即绑定");
        btn1View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                SharedPreferencesUtils.setParam(context, SharedPreferencesUtils.KEY_FIRST, false);
            }
        });
        view.findViewById(R.id.btn2View).setVisibility(View.GONE);


        return view;

    }

    private View initDialogSendVerifyCode(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_1, null, false);
        TextView titleView = view.findViewById(R.id.titleView);
        titleView.setText("发送验证码");
        titleView.setTextColor(getResources().getColor(R.color.dilog_blue));
        final TextView infoView = view.findViewById(R.id.infoView);
        infoView.setText("您好，您确定要将验证码发送到" + accountView.getText().toString().trim() + "该号码上？");
        TextView btn1View = view.findViewById(R.id.btn1View);
        btn1View.setText("暂不发送");
        btn1View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        TextView btn2View = view.findViewById(R.id.btn2View);
        btn2View.setText("发送");
        btn2View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                Intent intent = new Intent(v.getContext(), Login2Activity.class);
                intent.putExtra("phone", accountView.getText().toString().trim());
                intent.putExtra("entity",entity);
                startActivity(intent);
                finish();
            }
        });

        return view;

    }


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

    private View initDialogAccountError(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_1, null, false);
        TextView titleView = view.findViewById(R.id.titleView);
        titleView.setText("账号不存在");
//        titleView.setTextColor(getResources().getColor(R.color.dilog_gray));
        TextView infoView = view.findViewById(R.id.infoView);
        infoView.setText("您输入的用户账号或密码不存在，请确认后重新输入！");
        Util.setBackgroundTint(view.findViewById(R.id.actionLayout), R.color.dilog_gray);
        TextView btn1View = view.findViewById(R.id.btn1View);
        btn1View.setText("知道了");
        btn1View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                accountView.setText("");
                accountView.requestFocus();
                pswView.setText("");
            }
        });
        view.findViewById(R.id.btn2View).setVisibility(View.GONE);


        return view;

    }


    private View initDialogAccountError2(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_2, null, false);
        final EditText editText = view.findViewById(R.id.editText);
        final ImageView codeView = view.findViewById(R.id.imageView);
        Bitmap bitmap = CodeUtils.getInstance().createBitmap(CodeUtils.getInstance().createCode(), Util.dp2px(context, 64), Util.dp2px(context, 30));
        codeView.setImageBitmap(bitmap);

        ImageView refreshView = view.findViewById(R.id.refreshView);
        refreshView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = CodeUtils.getInstance().createBitmap(CodeUtils.getInstance().createCode(), Util.dp2px(v.getContext(), 64), Util.dp2px(v.getContext(), 30));
                codeView.setImageBitmap(bitmap);
            }
        });

        view.findViewById(R.id.btn1View).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CodeUtils.getInstance().getCode().toLowerCase().equals(editText.getText().toString().trim().toLowerCase())) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    accountView.setText("");
                    accountView.requestFocus();
                    pswView.setText("");

                } else {
                    Toast.makeText(v.getContext(), "验证码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private View initDialogAccountError3(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_3, null, false);
        TextView deviceView=view.findViewById(R.id.deviceView);
        TextView phoneView=view.findViewById(R.id.phoneView);
        deviceView.setText(Util.getSerialNumber(getBaseContext()));
        phoneView.setText(accountView.getText().toString().trim());

        TextView btn1View = view.findViewById(R.id.btn1View);
        btn1View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                finish();
            }
        });


        return view;

    }


    private void login() {

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
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.putOpt("loginIsSave", "0")
                            .putOpt("nickName", accountView.getText().toString().trim())
                            .putOpt("passport", pswView.getText().toString().trim())
                            .putOpt("platform", "Android")
                            .putOpt("appType", "SAFE")
                            .putOpt("uuid", Util.getSerialNumber(getBaseContext()))
                            .putOpt("verifyCode", verifyCode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonObject;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("login==========" + jsonObj.toString());
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
                        JSONObject data = jsonObj.getJSONObject("data").getJSONObject("personInfo");
                        String authKey = data.getString("authKey");
                        String id = data.getString("id");
                        System.out.println("authKey=======" + authKey);
                        System.out.println("authKey=======" + id);
                        SharedPreferencesUtils.setData(getBaseContext(), SharedPreferencesUtils.KEY_AUTHKEY, authKey);
                        SharedPreferencesUtils.setData(getBaseContext(), SharedPreferencesUtils.KEY_USERID, id);
                        SharedPreferencesUtils.setData(getBaseContext(), SharedPreferencesUtils.KEY_ENTITY, data.toString());

                        dialog = Util.showDialog(initDialogBindSuccess(getBaseContext()));


                    } else {
//                        Util.showToast(getBaseContext(), jsonObj.getString("message"));
                        dialog = Util.showDialog(initDialogAccountError(getBaseContext()));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void login_1() {

        RequestUtil.jsonObjectRequest(new RequestListener() {
            @Override
            public String getUrl() {
                return "/GsSafe/login";
            }

            @Override
            public Map<String, String> getParams() {
                return null;
            }

            @Override
            public JSONObject getJsonObj() {
                entity.setAppType("SAFE");
                entity.setBrand(Util.getDeviceBrand());
                entity.setImei( Util.getSerialNumber(getBaseContext()));
                entity.setName( Util.getSystemModel());
                entity.setNickName(accountView.getText().toString().trim());
                entity.setPassport(pswView.getText().toString().trim());
                entity.setPlatform("Android");
                entity.setUuid(Util.getSerialNumber(getBaseContext()));

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
                        dialog = Util.showDialog(initDialogSendVerifyCode(getBaseContext()));

                    } else {
//                        Util.showToast(getBaseContext(), jsonObj.getString("message"));
                        accountErrorNum++;
                        if (accountErrorNum==1) {
                            dialog = Util.showDialog(initDialogAccountError(getBaseContext()));
                        }else if(accountErrorNum==2){
                            dialog = Util.showDialog(initDialogAccountError2(getBaseContext()));
                        }else if(accountErrorNum==3){
                            dialog = Util.showDialog(initDialogAccountError3(getBaseContext()));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                        if ((Boolean) SharedPreferencesUtils.getParam(this, SharedPreferencesUtils.KEY_FIRST, true)) {
                            dialog = Util.showDialog(initDialogFirstUse(this));
                        }
                    }
                }
                break;
        }
    }

    public static void startLoginActivity(Activity activity){
        Intent intent=new Intent(activity,Login1Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

}
