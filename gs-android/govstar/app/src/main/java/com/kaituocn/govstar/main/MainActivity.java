package com.kaituocn.govstar.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.UserEntity;
import com.kaituocn.govstar.event.ChangeHeadEvent;
import com.kaituocn.govstar.util.AsteriskPasswordTransformationMethod;
import com.kaituocn.govstar.util.Constant;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.SharedPreferencesUtils;
import com.kaituocn.govstar.util.Util;
import com.kaituocn.govstar.yunav.TeamAVChatProfile;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener,View.OnClickListener{

    DrawerLayout drawer;
    ViewPager viewPager;
    RadioGroup radioGroup;
    EditText passwordView1,passwordView2;
    
    ImageView headView,bgView;
    Point point;

    TimePickerView pvTime;
    OptionsPickerView pvOptions;

    TextView birthdayView,sexView,workTypeView,saveView;
    CheckBox checkBox;

    Dialog dialog;
    int workType;
    List<String> workTypeItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_main);

        checkPermission(this);

//        String brand = android.os.Build.BRAND;
//        String model=android.os.Build.MODEL;

        EventBus.getDefault().register(this);

        String userinfo= SharedPreferencesUtils.getData(this,SharedPreferencesUtils.KEY_USER);
        System.out.println("MainActivity userinfo============="+userinfo);
        if (!TextUtils.isEmpty(userinfo)) {
            Constant.userEntity=new Gson().fromJson(userinfo,UserEntity.class);
        }

        //极光推送
        String alias=SharedPreferencesUtils.getData(this,SharedPreferencesUtils.KEY_JPUSH_ALIAS);
        if (TextUtils.isEmpty(alias)||(!TeamAVChatProfile.getUserId().equals(alias))) {
            JPushInterface.setAlias(this,1,TeamAVChatProfile.getUserId());
        }

        //网易云登录
        if (TextUtils.isEmpty(SharedPreferencesUtils.getData(this,SharedPreferencesUtils.KEY_YUN_TOKEN))) {
            TeamAVChatProfile.getYunToken();
        }else {
            TeamAVChatProfile.loginObserver();
        }

        Util.sendShortcut(this,0);

        drawer =  findViewById(R.id.drawer_layout);

        bgView=drawer.findViewById(R.id.bgView);
        bgView.setOnClickListener(this);
        String uri= (String) SharedPreferencesUtils.getParam(this,SharedPreferencesUtils.KEY_BACKGROUD_URI,"");
        if (!TextUtils.isEmpty(uri)) {
            bgView.setImageURI(Uri.parse(uri));
        }
        headView=drawer.findViewById(R.id.headView);
        headView.setOnClickListener(this);


        birthdayView=findViewById(R.id.textView13);
        birthdayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTimePicker();
            }
        });
        sexView=findViewById(R.id.textView14);
        sexView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> optionsItems = new ArrayList<>();
                optionsItems.add("男");
                optionsItems.add("女");
                initOptionsPicker(optionsItems,v,1);
            }
        });

        initWorkTypeItems();
        workTypeView=findViewById(R.id.textView15);
        workTypeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initOptionsPicker(workTypeItems,v,2);
            }
        });

        checkBox=findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkBox.setText("已开启");
                }else{
                    checkBox.setText("已关闭");

                }
            }
        });

        passwordView1=findViewById(R.id.editText2);
        passwordView2=findViewById(R.id.editText3);
        passwordView1.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        passwordView2.setTransformationMethod(new AsteriskPasswordTransformationMethod());

        saveView=findViewById(R.id.saveView);
        saveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (passwordView1.getText().toString().equals(passwordView2.getText().toString())) {
                    dialog=Util.showDialog(initDialogView_SaveInfo(v.getContext()));

                }else{
                    Util.showToast(v.getContext(),"密码不一致！");
                }

            }
        });

        initDrawer(drawer,Constant.userEntity);



        viewPager= findViewById(R.id.viewPager);
        viewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        radioGroup.check(R.id.radioButton1);
                        break;
                    case 1:
                        radioGroup.check(R.id.radioButton2);
                        break;
                    case 2:
                        radioGroup.check(R.id.radioButton3);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        radioGroup=findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioButton1:
                        viewPager.setCurrentItem(0,false);
                        break;
                    case R.id.radioButton2:
                        viewPager.setCurrentItem(1,false);
                        break;
                    case R.id.radioButton3:
                        viewPager.setCurrentItem(2,false);
                        break;
                }
            }
        });

    }
    private void  initWorkTypeItems(){
        workTypeItems.clear();
        workTypeItems.add("公务员编制");
        workTypeItems.add("事业单位编制");
        workTypeItems.add("合同制");
        workTypeItems.add("借调");
    }

    private void initDrawer(View view,UserEntity entity){
        if (entity == null) {
            return;
        }
        String headUrl=Util.getFileBaseUrl(getApplication()) +Constant.userEntity.getPersonInfo().getAvatarUrl();
        System.out.println("headUrl=========="+headUrl);
        Glide.with(this).load(headUrl)
                .error(R.drawable.t2).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE ).into(headView);

        ((TextView)view.findViewById(R.id.nameView)).setText(entity.getPersonInfo().getDepName()+"—"+entity.getPersonInfo().getName());
        ((TextView)view.findViewById(R.id.infoView)).setText(entity.getPersonInfo().getComName());
        ((TextView)view.findViewById(R.id.textView12)).setText(entity.getPersonInfo().getMobile());
        ((TextView)view.findViewById(R.id.textView13)).setText(entity.getPersonInfo().getAge());
        ((TextView)view.findViewById(R.id.textView14)).setText(entity.getPersonInfo().getSex());
        workType=entity.getPersonInfo().getWorkType();
        ((TextView)view.findViewById(R.id.textView15)).setText(workTypeItems.get(entity.getPersonInfo().getWorkType()-1));
        ((TextView)view.findViewById(R.id.textView16)).setText(entity.getPersonInfo().getRole());
        ((CheckBox)view.findViewById(R.id.checkBox)).setChecked(entity.getPersonInfo().getIsSafe()==1?true:false);

    }

    private View initDialogView_SaveInfo(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_3, null, false);
        ((TextView)view.findViewById(R.id.titleView)).setText("是否确定修改？");
        ((TextView)view.findViewById(R.id.infoView)).setText("您确定要修改信息吗？");
        view.findViewById(R.id.btn1View).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        view.findViewById(R.id.btn2View).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                updateUserInfo();
            }
        });
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(ChangeHeadEvent event) {
        Util.setAvatar(this,Constant.userEntity.getPersonInfo().getAvatarUrl(),headView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bgView:
//                CalendarReminderUtils.addCalendarEvent(this,"aaa","bbb",System.currentTimeMillis()+1000*60*60,1);
                if (checkPermission(v.getContext())) {
                    point=new Point(800,450);
                    showPopupWindow(v,0);
                }
                break;
            case R.id.headView:
                if (checkPermission(v.getContext())) {
                    point=new Point(400,400);
                    showPopupWindow(v,1);
                }
                break;
            case R.id.sortAView:
                if (window != null) {
                    window.dismiss();
                }
                //拍照
                File  file=new File(getExternalCacheDir().getAbsolutePath()+"/capture.jpg");
                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(this,"com.kaituocn.govstar.fileProvider",file);
                } else {
                    uri = Uri.fromFile(file);
                }
//                imageUri = FileProvider.getUriForFile(this,"com.kaituocn.govstar.fileProvider",file);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,uri );
                startActivityForResult(intent, 2);
                break;
            case R.id.sortBView:
                if (window != null) {
                    window.dismiss();
                }
                //图库选取照片
                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent1, 1);
                break;
            case R.id.sortCView:
                if (window != null) {
                    window.dismiss();
                }
                break;
        }
    }


    private boolean checkPermission(Context context){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                ) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return false;
        }else{
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length == 3) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Util.showToast(this, "请打开相机权限");
                    } else if (grantResults[1] == PackageManager.PERMISSION_DENIED) {
                        Util.showToast(this, "请打开读存储器权限");
                    } else if (grantResults[2] == PackageManager.PERMISSION_DENIED) {
                        Util.showToast(this, "请打开写存储器权限");
                    } else {
//                        showHeadViewDialog();
                    }
                }
                break;
        }
    }

    Uri imageUri;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        File file;
        if (resultCode==RESULT_OK) {
            switch (requestCode) {
                case 1://图库返回
                    if (point.x==point.y) {//头像
                        file=new File(getExternalCacheDir().getAbsolutePath()+"/head.jpg");
                    }else{
                        file=new File(getExternalCacheDir().getAbsolutePath()+"/background.jpg");
                    }
                    imageUri = Uri.fromFile(file);
                    cropPhoto(data.getData(),point.x,point.y, imageUri);
                    break;
                case 2://拍照返回

                    file=new File(getExternalCacheDir().getAbsolutePath()+"/head.jpg");
                    imageUri = Uri.fromFile(file);

                    File  fileSrc=new File(getExternalCacheDir().getAbsolutePath()+"/capture.jpg");
                    Uri uri=Util.getImageContentUri(this,fileSrc);
                    cropPhoto(uri,point.x,point.y, imageUri);
                    break;
                case 3://剪裁返回
                    if (point.x==point.y){//头像
//                        headView.setImageURI(null);
//                        headView.setImageURI(imageUri);
//                        headView.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                headView.setImageURI(imageUri);
//                            }
//                        },1000);
//                        try {
//                            Bitmap bmp=MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
//                            headView.setImageBitmap(bmp);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                        uploadHeadImage(Util.getFileByUri(imageUri,getBaseContext()));
                    }else{
                        bgView.setImageURI(null);
                        bgView.setImageURI(imageUri);
//                        try {
//                            Bitmap bmp=MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
//                            bgView.setImageBitmap(bmp);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                        SharedPreferencesUtils.setParam(this,SharedPreferencesUtils.KEY_BACKGROUD_URI,imageUri.toString());
                    }
                    break;
              

            }
        }
    }

    private void cropPhoto(Uri uri, int outputX, int outputY, Uri desUri){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", outputX);
        intent.putExtra("aspectY", outputY);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        intent.putExtra("circleCrop", false);//圆形剪裁---跟系统有关，不一定有效
//        intent.putExtra("noFaceDetection", true);// 是否去除面部检测
        intent.putExtra(MediaStore.EXTRA_OUTPUT, desUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, 3);
    }

    @Override
    public void onFragmentInteraction() {
        drawer.openDrawer(GravityCompat.START);
    }


    PopupWindow window;
    private void showPopupWindow(View anchor,int type) {
        View contentView = LayoutInflater.from(anchor.getContext()).inflate(R.layout.popup_picker, null, false);
        window = new PopupWindow(initPopupView(contentView,type), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setOutsideTouchable(true);
        window.setTouchable(true);
//        window.setAnimationStyle(R.style.dialog_animstyle);
//        window.showAsDropDown(anchor,  0,0);
        window.showAtLocation(anchor, Gravity.BOTTOM ,0,0);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);

        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
            }
        });
    }

    private View initPopupView(View view,int type) {
        view.findViewById(R.id.sortAView).setOnClickListener(this);
        view.findViewById(R.id.sortBView).setOnClickListener(this);
        view.findViewById(R.id.sortCView).setOnClickListener(this);
        if (type==0) {
            ((TextView)view.findViewById(R.id.sortBView)).setText("更换背景");
            view.findViewById(R.id.sortAView).setVisibility(View.GONE);

        }
        return view;
    }


    private void initTimePicker() {
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            public void onTimeSelect(Date date, View v) {
                birthdayView.setText(Util.getDateStr(date, "yyyy"));
            }
        }).setType(new boolean[]{true, false, false, false, false, false})
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
        if (!TextUtils.isEmpty(birthdayView.getText().toString().trim())) {
            calendar.set(Calendar.YEAR,Integer.parseInt(birthdayView.getText().toString().trim()));
        }
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
                if (type==2) {
                    workType=options1+1;
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
        pvOptions.setPicker(optionsItems);
        if (type==1) {
            if(sexView.getText().toString().equals("男")){
                pvOptions.setSelectOptions(0);
            }else{
                pvOptions.setSelectOptions(1);
            }
        }else if(type==2){
            pvOptions.setSelectOptions(workType-1);

        }
        pvOptions.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.addCategory(Intent.CATEGORY_HOME);
//            startActivity(intent);
        }
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

    private void uploadHeadImage(File file){
        dialog=Util.showLoadingDialog(this,"头像上传中…");
        RequestUtil.uploadFile(file, new RequestListener() {
            @Override
            public String getUrl() {
                return "/supervision/avatar/fileUpload";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String,String> map=new HashMap<>();
                map.put("userId",Constant.userEntity.getPersonInfo().getId()+"");
                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("uploadHeadImage================"+jsonObj);
                if (dialog != null) {
                    dialog.dismiss();
                }
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1){
                        Constant.userEntity.getPersonInfo().setAvatarUrl(jsonObj.getJSONObject("data").getString("fileUrl"));
                        SharedPreferencesUtils.setData(getBaseContext(),SharedPreferencesUtils.KEY_USER,new Gson().toJson(Constant.userEntity));
                        EventBus.getDefault().post(new ChangeHeadEvent());
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

    private void updateUserInfo(){
        dialog=Util.showLoadingDialog(this,"信息保存中…");
        RequestUtil.jsonObjectRequest(new RequestListener() {
            @Override
            public String getUrl() {
                return "/AccountManage/updateUserInfo";
            }

            @Override
            public Map<String, String> getParams() {
                return null;
            }

            @Override
            public JSONObject getJsonObj() {
                JSONObject object=new JSONObject();
                try {
                    object.putOpt("sex",sexView.getText().toString());
                    object.putOpt("age",birthdayView.getText().toString());
                    object.putOpt("isSafe",checkBox.isChecked()?1:0);
                    object.putOpt("workType",workType);
                    if (!TextUtils.isEmpty(passwordView1.getText().toString())) {
                        object.putOpt("passport",passwordView1.getText().toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return object;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("updateUserInfo=============="+jsonObj);
                if (dialog != null) {
                    dialog.dismiss();
                }
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1){
                        passwordView1.setText("");
                        passwordView2.setText("");

                        drawer.closeDrawer(GravityCompat.START);
                        Constant.userEntity.getPersonInfo().setSex(sexView.getText().toString());
                        Constant.userEntity.getPersonInfo().setAge(birthdayView.getText().toString());
                        Constant.userEntity.getPersonInfo().setIsSafe(checkBox.isChecked()?1:0);
                        Constant.userEntity.getPersonInfo().setWorkType(workType);

                        SharedPreferencesUtils.setData(getBaseContext(),SharedPreferencesUtils.KEY_USER,new Gson().toJson(Constant.userEntity));
                        handler.obtainMessage(1,"信息修改完成！").sendToTarget();
                    }else {
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
            }
        });
    }


}
