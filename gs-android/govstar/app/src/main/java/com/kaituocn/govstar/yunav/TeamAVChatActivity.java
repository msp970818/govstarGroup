package com.kaituocn.govstar.yunav;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.MeetingEntity;
import com.kaituocn.govstar.entity.MeetingJoinPersonEntity;
import com.kaituocn.govstar.util.Constant;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;
import com.kaituocn.govstar.yunav.permission.MPermission;
import com.kaituocn.govstar.yunav.permission.annotation.OnMPermissionDenied;
import com.kaituocn.govstar.yunav.permission.annotation.OnMPermissionGranted;
import com.kaituocn.govstar.yunav.permission.annotation.OnMPermissionNeverAskAgain;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.AVChatStateObserver;
import com.netease.nimlib.sdk.avchat.constant.AVChatControlCommand;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.constant.AVChatUserRole;
import com.netease.nimlib.sdk.avchat.constant.AVChatVideoCropRatio;
import com.netease.nimlib.sdk.avchat.constant.AVChatVideoScalingType;
import com.netease.nimlib.sdk.avchat.model.AVChatCameraCapturer;
import com.netease.nimlib.sdk.avchat.model.AVChatControlEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatParameters;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoCapturerFactory;
import com.netease.nrtc.video.render.IVideoRender;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamAVChatActivity extends AppCompatActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener{
    private static final String TAG = "TeamAVChatActivity";

    private static final String KEY_RECEIVED_CALL = "call";
    private static final String KEY_ROOM_ID = "roomId";
    private static final String KEY_ACCOUNTS = "accounts";
    private static final String KEY_TEAM_ID = "teamid";
    private static final String KEY_TNAME = "teamName";
    private static final String KEY_USERNAME = "userName";
    private static final String KEY_ICONURL = "iconUrl";
    private static final int AUTO_REJECT_CALL_TIMEOUT = 60 * 1000;
    private static final int BASIC_PERMISSION_REQUEST_CODE = 0x100;

    private AVChatCameraCapturer mVideoCapturer;
    private AVChatStateObserver stateObserver;
    private Observer<AVChatControlEvent> notificationObserver;

    private String roomId;
    private ArrayList<String> accounts;
    private boolean receivedCall;
    private String meetingId;
    private String teamName;
    private String userName;
    private String iconUrl;
    private long chatId;
    private boolean destroyRTC;

    // LAYOUT
    private View callLayout;
    private View surfaceLayout;
    private View personsLayout;

    private Handler mainHandler;
    private Runnable autoRejectTask;

    List<TeamAVChatItem> items;

    IVideoRender topSurfaceView;



    public static void  startActivity(Context context, boolean receivedCall, String roomId, ArrayList<String> accounts,String teamId,String teamName,String userName,String iconUrl){

        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setClass(context, TeamAVChatActivity.class);
        intent.putExtra(KEY_RECEIVED_CALL, receivedCall);
        intent.putExtra(KEY_ROOM_ID, roomId);
        intent.putExtra(KEY_ACCOUNTS, accounts);
        intent.putExtra(KEY_TEAM_ID, teamId);
        intent.putExtra(KEY_TNAME, teamName);
        intent.putExtra(KEY_USERNAME, userName);
        intent.putExtra(KEY_ICONURL, iconUrl);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN|WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_team_avchat);

        mainHandler = new Handler();

        dismissKeyguard();
        onIntent();
        if (TextUtils.isEmpty(roomId)) {
            finish();
        }else{

            initItems();
            findLayouts();
            showViews();
            setChatting(true);
            NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(userStatusObserver, true);
        }

        getMeetingInfo(meetingId);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        System.out.println("onNewIntent====================");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.surfaceLayout11:
                if (((View)items.get(0).getSurfaceView()).getVisibility()==View.VISIBLE) {
                    TeamAVChatItem itemOnTop=findItemOnTop();
                    if (itemOnTop != null) {
                        itemOnTop.showOnTop=false;
                        if ((itemOnTop.muteVideo)) {
                            ((View)itemOnTop.surfaceView).setVisibility(View.INVISIBLE);
                        }else{
                            ((View)itemOnTop.surfaceView).setVisibility(View.VISIBLE);
                        }
                        AVChatManager.getInstance().setupRemoteVideoRender(itemOnTop.account, itemOnTop.surfaceView, false, AVChatVideoScalingType.SCALE_ASPECT_FIT);
                    }
                    items.get(0).showOnTop=true;
                    ((View)items.get(0).getSurfaceView()).setVisibility(View.INVISIBLE);
                    ((View) topSurfaceView).setVisibility(View.VISIBLE);
                    AVChatManager.getInstance().setupLocalVideoRender(null, false, 0);
                    AVChatManager.getInstance().setupLocalVideoRender(topSurfaceView, false, AVChatVideoScalingType.SCALE_ASPECT_FIT);
                }else{
                    if (!(items.get(0).muteVideo)) {

                        items.get(0).showOnTop=false;
                        ((View) topSurfaceView).setVisibility(View.INVISIBLE);
                        ((View)items.get(0).getSurfaceView()).setVisibility(View.VISIBLE);
                        AVChatManager.getInstance().setupLocalVideoRender(null, false, 0);
                        AVChatManager.getInstance().setupLocalVideoRender(items.get(0).surfaceView, false, AVChatVideoScalingType.SCALE_ASPECT_FIT);
                    }
                }
                break;
            case R.id.surfaceLayout12:
            case R.id.surfaceLayout13:
            case R.id.surfaceLayout21:
            case R.id.surfaceLayout22:
            case R.id.surfaceLayout23:
                switchSurfaceView(v.getId());
                break;
            case R.id.textView11:
                personsLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.textView12:
                if(checkOverlayPermission(this)){
                    Util.creatFloatLayout(getApplication(),Util.dp2px(this,60),Util.dp2px(this,60));
                    moveTaskToBack(true);
                }

                break;
            case R.id.textView13:
                dialog=Util.showDialog(initDialogView_End(v.getContext()));

                break;
        }

    }
    private boolean checkOverlayPermission(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (!Settings.canDrawOverlays(context)){
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                Util.showToast(getBaseContext(),"请打开系统悬浮窗权限");
                return false;
            }else{
                return true;
            }
        }else{
            return true;
        }
    }

    Dialog dialog;
    private View initDialogView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_2, null, false);
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
                AVChatSoundPlayer.instance(v.getContext()).stop();
                cancelAutoRejectTask();
                finish();

            }
        });
        return view;
    }
    private View initDialogView_End(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_2, null, false);
        ((TextView)view.findViewById(R.id.titleView)).setText("退出视频会议？");
        ((TextView)view.findViewById(R.id.infoView)).setText("您确定要退出视频会议吗？");
        ((TextView)view.findViewById(R.id.btn2View)).setText("确定");
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
                if (!receivedCall) {
                    endMeeting();
                }
                hangup();
                finish();

            }
        });
        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.checkBox1:
                AVChatManager.getInstance().muteLocalAudio(!isChecked);
                if (isChecked) {
                    ((TextView)findViewById(R.id.textView1)).setText("未静音");
                }else{
                    ((TextView)findViewById(R.id.textView1)).setText("已静音");
                }
                break;
            case R.id.checkBox2:
                AVChatManager.getInstance().muteLocalVideo(!isChecked);
                if (isChecked) {
                    ((TextView)findViewById(R.id.textView2)).setText("开启摄像");
                }else{
                    ((TextView)findViewById(R.id.textView2)).setText("关闭摄像");
                }
                // 发送控制指令
                byte command = isChecked ? AVChatControlCommand.NOTIFY_VIDEO_ON :AVChatControlCommand.NOTIFY_VIDEO_OFF ;
                AVChatManager.getInstance().sendControlCommand(chatId, command, null);
                updateSelfItemVideoState(isChecked);
                break;
            case R.id.checkBox3:
                AVChatManager.getInstance().setSpeaker(isChecked);
                if (isChecked) {
                    ((TextView)findViewById(R.id.textView3)).setText("开启扬声器");
                }else{
                    ((TextView)findViewById(R.id.textView3)).setText("关闭扬声器");
                }
                break;
            case R.id.checkBox4:
                mVideoCapturer.switchCamera();
                if (isChecked) {
                    ((TextView)findViewById(R.id.textView4)).setText("前置摄像头");
                }else{
                    ((TextView)findViewById(R.id.textView4)).setText("后置摄像头");
                }
                break;
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (stateObserver != null) {
            AVChatManager.getInstance().observeAVChatState(stateObserver, false);
        }


        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }
        hangup(); // 页面销毁的时候要保证离开房间，rtc释放。
        setChatting(false);
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(userStatusObserver, false);

        AVChatSoundPlayer.instance(this).stop();

    }


    private void dismissKeyguard() {
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );
    }

    private void onIntent() {
        Intent intent = getIntent();
        receivedCall = intent.getBooleanExtra(KEY_RECEIVED_CALL, false);
        roomId = intent.getStringExtra(KEY_ROOM_ID);
        accounts = (ArrayList<String>) intent.getSerializableExtra(KEY_ACCOUNTS);
        meetingId = intent.getStringExtra(KEY_TEAM_ID);
        teamName = intent.getStringExtra(KEY_TNAME);
        userName = intent.getStringExtra(KEY_USERNAME);
        iconUrl = intent.getStringExtra(KEY_ICONURL);

    }

    private void initItems(){
        items=new ArrayList<>();
        for (int i = 0; i <accounts.size() ; i++) {
            items.add(new TeamAVChatItem());
        }
        items.get(0).isLocal=true;
        switch (accounts.size()>6?6:accounts.size()){
            case 1:
                items.get(0).setSurfaceView((IVideoRender) findViewById(R.id.surface11));
                items.get(0).setId(R.id.surfaceLayout11);
                break;
            case 2:
                items.get(0).setSurfaceView((IVideoRender) findViewById(R.id.surface11));
                items.get(1).setSurfaceView((IVideoRender) findViewById(R.id.surface12));
                items.get(0).setId(R.id.surfaceLayout11);
                items.get(1).setId(R.id.surfaceLayout12);
                break;
            case 3:
                items.get(0).setSurfaceView((IVideoRender) findViewById(R.id.surface11));
                items.get(1).setSurfaceView((IVideoRender) findViewById(R.id.surface12));
                items.get(2).setSurfaceView((IVideoRender) findViewById(R.id.surface13));
                findViewById(R.id.surfaceLayout13).setVisibility(View.VISIBLE);
                items.get(0).setId(R.id.surfaceLayout11);
                items.get(1).setId(R.id.surfaceLayout12);
                items.get(2).setId(R.id.surfaceLayout13);
                break;
            case 4:
                items.get(0).setSurfaceView((IVideoRender) findViewById(R.id.surface11));
                items.get(1).setSurfaceView((IVideoRender) findViewById(R.id.surface12));
                items.get(2).setSurfaceView((IVideoRender) findViewById(R.id.surface21));
                items.get(3).setSurfaceView((IVideoRender) findViewById(R.id.surface22));
                findViewById(R.id.surfaceLayout21).setVisibility(View.VISIBLE);
                findViewById(R.id.surfaceLayout22).setVisibility(View.VISIBLE);
                items.get(0).setId(R.id.surfaceLayout11);
                items.get(1).setId(R.id.surfaceLayout12);
                items.get(2).setId(R.id.surfaceLayout21);
                items.get(3).setId(R.id.surfaceLayout22);
                break;
            case 5:
                items.get(0).setSurfaceView((IVideoRender) findViewById(R.id.surface11));
                items.get(1).setSurfaceView((IVideoRender) findViewById(R.id.surface12));
                items.get(2).setSurfaceView((IVideoRender) findViewById(R.id.surface13));
                items.get(3).setSurfaceView((IVideoRender) findViewById(R.id.surface21));
                items.get(4).setSurfaceView((IVideoRender) findViewById(R.id.surface22));
                findViewById(R.id.surfaceLayout13).setVisibility(View.VISIBLE);
                findViewById(R.id.surfaceLayout21).setVisibility(View.VISIBLE);
                findViewById(R.id.surfaceLayout22).setVisibility(View.VISIBLE);
                items.get(0).setId(R.id.surfaceLayout11);
                items.get(1).setId(R.id.surfaceLayout12);
                items.get(2).setId(R.id.surfaceLayout13);
                items.get(3).setId(R.id.surfaceLayout21);
                items.get(4).setId(R.id.surfaceLayout22);
                break;
            case 6:
                items.get(0).setSurfaceView((IVideoRender) findViewById(R.id.surface11));
                items.get(1).setSurfaceView((IVideoRender) findViewById(R.id.surface12));
                items.get(2).setSurfaceView((IVideoRender) findViewById(R.id.surface13));
                items.get(3).setSurfaceView((IVideoRender) findViewById(R.id.surface21));
                items.get(4).setSurfaceView((IVideoRender) findViewById(R.id.surface22));
                items.get(5).setSurfaceView((IVideoRender) findViewById(R.id.surface23));
                findViewById(R.id.surfaceLayout13).setVisibility(View.VISIBLE);
                findViewById(R.id.surfaceLayout21).setVisibility(View.VISIBLE);
                findViewById(R.id.surfaceLayout22).setVisibility(View.VISIBLE);
                findViewById(R.id.surfaceLayout23).setVisibility(View.VISIBLE);
                items.get(0).setId(R.id.surfaceLayout11);
                items.get(1).setId(R.id.surfaceLayout12);
                items.get(2).setId(R.id.surfaceLayout13);
                items.get(3).setId(R.id.surfaceLayout21);
                items.get(4).setId(R.id.surfaceLayout22);
                items.get(5).setId(R.id.surfaceLayout23);
                break;
        }

    }

    private TeamAVChatItem findItem(int surfaceId){
        for (TeamAVChatItem item : items) {
            if (item.getId()==surfaceId){
                return item;
            }
        }
        return null;
    }
    private TeamAVChatItem findItemOnTop(){
        for (TeamAVChatItem item : items) {
            if (item.showOnTop){
                return item;
            }
        }
        return null;
    }
    private void switchSurfaceView(int surfaceId){
        TeamAVChatItem item;
        item=findItem(surfaceId);
        if (item==null||item.getSurfaceView()==null) {
            return;
        }
        if (((View)item.getSurfaceView()).getVisibility()==View.VISIBLE) {
            TeamAVChatItem itemOnTop=findItemOnTop();
            if (itemOnTop != null) {
                itemOnTop.showOnTop=false;
                if ((itemOnTop.muteVideo)) {
                    ((View)itemOnTop.surfaceView).setVisibility(View.INVISIBLE);
                }else{
                    ((View)itemOnTop.surfaceView).setVisibility(View.VISIBLE);
                }
                if (itemOnTop.isLocal) {
                    AVChatManager.getInstance().setupLocalVideoRender(itemOnTop.surfaceView, false, AVChatVideoScalingType.SCALE_ASPECT_FIT);
                }else{
                    AVChatManager.getInstance().setupRemoteVideoRender(itemOnTop.account, itemOnTop.surfaceView, false, AVChatVideoScalingType.SCALE_ASPECT_FIT);
                }
            }
            item.showOnTop=true;
            ((View) topSurfaceView).setVisibility(View.VISIBLE);
            ((View)item.surfaceView).setVisibility(View.INVISIBLE);
            AVChatManager.getInstance().setupRemoteVideoRender(item.account, null, false, AVChatVideoScalingType.SCALE_ASPECT_FIT);
            AVChatManager.getInstance().setupRemoteVideoRender(item.account, topSurfaceView, false, AVChatVideoScalingType.SCALE_ASPECT_FIT);

        }else{
            if (item.status==1) {
                if(!item.muteVideo){
                    item.showOnTop=false;
                    ((View)item.surfaceView).setVisibility(View.VISIBLE);
                    ((View) topSurfaceView).setVisibility(View.INVISIBLE);
                    AVChatManager.getInstance().setupRemoteVideoRender(item.account, null, false, AVChatVideoScalingType.SCALE_ASPECT_FIT);
                    AVChatManager.getInstance().setupRemoteVideoRender(item.account, item.surfaceView, false, AVChatVideoScalingType.SCALE_ASPECT_FIT);
                }

            }
        }

    }

    private void findLayouts() {
        personsLayout=findViewById(R.id.team_avchat_persons);
        callLayout = findViewById(R.id.team_avchat_call_layout);
        surfaceLayout = findViewById(R.id.team_avchat_surface_layout);
        callLayout.setVisibility(View.GONE);
        surfaceLayout.setVisibility(View.GONE);

    }

    private void showViews() {
        if (receivedCall) {
            initReceivedCallLayout();
        } else {
            initSurfaceLayout();
        }
    }



    private void initPersonsLayout(){
        personsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
            }
        });

        TagFlowLayout tagFlowLayout=personsLayout.findViewById(R.id.tagFlowLayout1);
        tagFlowLayout.setAdapter(new TagAdapter<MeetingJoinPersonEntity>(joinList) {
            @Override
            public View getView(FlowLayout parent, int position, MeetingJoinPersonEntity entity) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_avchat_person,parent, false);
                TextView nameView=view.findViewById(R.id.nameView);
                nameView.setText(entity.getJoinName());
                ImageView headView=view.findViewById(R.id.headView);
                Util.setAvatar(TeamAVChatActivity.this,entity.getAvatarUrl(),headView);
                return view;
            }
        });
    }


    private void initReceivedCallLayout() {
        callLayout.setVisibility(View.VISIBLE);
        // 提示
        if (!TextUtils.isEmpty(userName)) {
            ((TextView)callLayout.findViewById(R.id.nameView)).setText(userName);
        }
        TextView textView = callLayout.findViewById(R.id.received_call_tip);
        textView.setText(teamName + " 的视频会议");

        // 播放铃声
        AVChatSoundPlayer.instance(this).play(AVChatSoundPlayer.RingerTypeEnum.RING);

        // 拒绝
        callLayout.findViewById(R.id.refuse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AVChatSoundPlayer.instance(v.getContext()).stop();
//                cancelAutoRejectTask();
//                finish();

                dialog = Util.showDialog(initDialogView(v.getContext()));
            }
        });

        // 接听
        callLayout.findViewById(R.id.receive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AVChatSoundPlayer.instance(v.getContext()).stop();
                cancelAutoRejectTask();
                callLayout.setVisibility(View.GONE);
                initSurfaceLayout();
            }
        });

        startAutoRejectTask();
    }



    private void initSurfaceLayout(){
        surfaceLayout.setVisibility(View.VISIBLE);
        findViewById(R.id.textView11).setOnClickListener(this);
        findViewById(R.id.textView12).setOnClickListener(this);
        findViewById(R.id.textView13).setOnClickListener(this);

        ((CheckBox)findViewById(R.id.checkBox1)).setOnCheckedChangeListener(this);
        ((CheckBox)findViewById(R.id.checkBox2)).setOnCheckedChangeListener(this);
        ((CheckBox)findViewById(R.id.checkBox3)).setOnCheckedChangeListener(this);
        ((CheckBox)findViewById(R.id.checkBox4)).setOnCheckedChangeListener(this);

        findViewById(R.id.surfaceLayout11).setOnClickListener(this);
        findViewById(R.id.surfaceLayout12).setOnClickListener(this);
        findViewById(R.id.surfaceLayout13).setOnClickListener(this);
        findViewById(R.id.surfaceLayout21).setOnClickListener(this);
        findViewById(R.id.surfaceLayout22).setOnClickListener(this);
        findViewById(R.id.surfaceLayout23).setOnClickListener(this);

        topSurfaceView =findViewById(R.id.surface0);

        checkPermission();
    }

    private void startAutoRejectTask() {
        if (autoRejectTask == null) {
            autoRejectTask = new Runnable() {
                @Override
                public void run() {
                    AVChatSoundPlayer.instance(TeamAVChatActivity.this).stop();
                    finish();
                }
            };
        }

        mainHandler.postDelayed(autoRejectTask, AUTO_REJECT_CALL_TIMEOUT);
    }

    private void cancelAutoRejectTask() {
        if (autoRejectTask != null) {
            mainHandler.removeCallbacks(autoRejectTask);
        }
    }

    /*
     * 设置通话状态
     */
    private void setChatting(boolean isChatting) {
        TeamAVChatProfile.sharedInstance().setTeamAVChatting(isChatting);
    }

    private void onPermissionChecked() {
        startRtc(); // 启动音视频
    }

    /**
     * ************************************ 音视频事件 ***************************************
     */
    private void startRtc() {
        // rtc init
        AVChatManager.getInstance().enableRtc();
        AVChatManager.getInstance().enableVideo();

        mVideoCapturer = AVChatVideoCapturerFactory.createCameraCapturer();
        AVChatManager.getInstance().setupVideoCapturer(mVideoCapturer);

        // state observer
        if (stateObserver != null) {
            AVChatManager.getInstance().observeAVChatState(stateObserver, false);
        }
        stateObserver = new SimpleAVChatStateObserver() {
            @Override
            public void onJoinedChannel(int code, String audioFile, String videoFile, int i) {
                if (code == 200) {
                    onJoinRoomSuccess();
                } else {
                    onJoinRoomFailed(code, null);
                }
            }

            @Override
            public void onUserJoined(String account) {
                onAVChatUserJoined(account);
            }

            @Override
            public void onUserLeave(String account, int event) {
                onAVChatUserLeave(account);
            }

            @Override
            public void onReportSpeaker(Map<String, Integer> speakers, int mixedEnergy) {

            }
        };

        AVChatManager.getInstance().observeAVChatState(stateObserver, true);

        // notification observer
        if (notificationObserver != null) {
            AVChatManager.getInstance().observeControlNotification(notificationObserver, false);
        }
        notificationObserver = new Observer<AVChatControlEvent>() {

            @Override
            public void onEvent(AVChatControlEvent event) {
                final String account = event.getAccount();
                if (AVChatControlCommand.NOTIFY_VIDEO_ON == event.getControlCommand()) {
                    onVideoLive(account);
                } else if (AVChatControlCommand.NOTIFY_VIDEO_OFF == event.getControlCommand()) {
                    onVideoLiveEnd(account);
                }
            }
        };
        AVChatManager.getInstance().observeControlNotification(notificationObserver, true);

        // join
        AVChatManager.getInstance().setParameter(AVChatParameters.KEY_SESSION_MULTI_MODE_USER_ROLE, AVChatUserRole.NORMAL);
        AVChatManager.getInstance().setParameter(AVChatParameters.KEY_AUDIO_REPORT_SPEAKER, true);
        AVChatManager.getInstance().setParameter(AVChatParameters.KEY_VIDEO_FIXED_CROP_RATIO, AVChatVideoCropRatio.CROP_RATIO_1_1);
        AVChatManager.getInstance().joinRoom2(roomId, AVChatType.VIDEO, new AVChatCallback<AVChatData>() {
            @Override
            public void onSuccess(AVChatData data) {
                chatId = data.getChatId();
                Log.i(TAG, "join room success, roomId=" + roomId + ", chatId=" + chatId);
            }

            @Override
            public void onFailed(int code) {
                onJoinRoomFailed(code, null);
                Log.i(TAG, "join room failed, code=" + code + ", roomId=" + roomId);
            }

            @Override
            public void onException(Throwable exception) {
                onJoinRoomFailed(-1, exception);
                Log.i(TAG, "join room failed, e=" + exception.getMessage() + ", roomId=" + roomId);
            }
        });
    }

    private void onJoinRoomSuccess() {
        startLocalPreview();
        Log.i(TAG, "team avchat running..." + ", roomId=" + roomId);
    }

    private void onJoinRoomFailed(int code, Throwable e) {
        if (code == ResponseCode.RES_ENONEXIST) {
            Util.showToast(this,"加入房间失败，房间不存在");
        } else {
            Util.showToast(this,"join room failed, code=" + code + ", e=" + (e == null ? "" : e.getMessage()));
        }
    }

    public void onAVChatUserJoined(String account) {
        TeamAVChatItem item =null;
        for (TeamAVChatItem teamAVChatItem : items) {
            if (teamAVChatItem.getStatus()==0) {
                item=teamAVChatItem;
                break;
            }
        }
        if (item!=null) {
            item.setStatus(1);
            item.setAccount(account);
            ((View)item.getSurfaceView()).setVisibility(View.VISIBLE);
            AVChatManager.getInstance().setupRemoteVideoRender(account, item.surfaceView, false, AVChatVideoScalingType.SCALE_ASPECT_FIT);
        }
    }
    public void onAVChatUserLeave(String account) {
        for (TeamAVChatItem item : items) {
            if (account.equals(item.account)) {
                item.setStatus(0);
                item.setAccount("");
                ((View)item.getSurfaceView()).setVisibility(View.INVISIBLE);
                AVChatManager.getInstance().setupRemoteVideoRender(item.account, null, false, 0);
                break;
            }
        }
    }

    private void startLocalPreview() {
        items.get(0).setStatus(1);
        ((View)items.get(0).getSurfaceView()).setVisibility(View.VISIBLE);
        AVChatManager.getInstance().setupLocalVideoRender(items.get(0).surfaceView, false, AVChatVideoScalingType.SCALE_ASPECT_FIT);
        AVChatManager.getInstance().startVideoPreview();

        findViewById(R.id.surfaceLayout11).performClick();
    }

    /**
     * ************************************ 音视频状态 ***************************************
     */


    private void updateSelfItemVideoState(boolean live) {

        items.get(0).muteVideo=!live;
        if ((items.get(0).showOnTop)) {
            ((View)topSurfaceView).setVisibility(live?View.VISIBLE:View.INVISIBLE);
        }else{
            ((View)items.get(0).surfaceView).setVisibility(live?View.VISIBLE:View.INVISIBLE);

        }
    }

    private void onVideoLive(String account) {
        if (account.equals(TeamAVChatProfile.getUserId())) {
            return;
        }
        for (TeamAVChatItem item : items){
            if (account.equals(item.account)){
                item.muteVideo=false;
                if ((item.showOnTop)) {
                    ((View) topSurfaceView).setVisibility(View.VISIBLE);
                }else {
                    ((View)item.getSurfaceView()).setVisibility(View.VISIBLE);
                }
                break;
            }
        }

    }

    private void onVideoLiveEnd(String account) {
        if (account.equals(TeamAVChatProfile.getUserId())) {
            return;
        }

        for (TeamAVChatItem item : items){
            if (account.equals(item.account)){
                item.muteVideo=true;
                if ((item.showOnTop)) {
                    ((View) topSurfaceView).setVisibility(View.INVISIBLE);
                }else{
                    ((View)item.getSurfaceView()).setVisibility(View.INVISIBLE);
                }
                break;
            }
        }
    }

    /**
     * 在线状态观察者
     */
    private Observer<StatusCode> userStatusObserver = new Observer<StatusCode>() {

        @Override
        public void onEvent(StatusCode code) {
            if (code.wontAutoLogin()) {
                AVChatSoundPlayer.instance(TeamAVChatActivity.this).stop();
                hangup();
                finish();
            }
        }
    };

    private void hangup() {
        if (destroyRTC) {
            return;
        }

        try {
            AVChatManager.getInstance().stopVideoPreview();
            AVChatManager.getInstance().leaveRoom2(roomId, null);
            AVChatManager.getInstance().disableRtc();
        } catch (Exception e) {
            e.printStackTrace();
        }

        destroyRTC = true;
        Log.i(TAG, "destroy rtc & leave room, roomId=" + roomId);
    }

    /**
     * ************************************ 权限检查 ***************************************
     */
    private void checkPermission() {
        List<String> lackPermissions = AVChatManager.getInstance().checkPermission(TeamAVChatActivity.this);
        if (lackPermissions.isEmpty()) {
            onBasicPermissionSuccess();
        } else {
            String[] permissions = new String[lackPermissions.size()];
            for (int i = 0; i < lackPermissions.size(); i++) {
                permissions[i] = lackPermissions.get(i);
            }
            MPermission.with(TeamAVChatActivity.this)
                    .setRequestCode(BASIC_PERMISSION_REQUEST_CODE)
                    .permissions(permissions)
                    .request();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);

    }

    @OnMPermissionGranted(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionSuccess() {
        onPermissionChecked();
    }

    @OnMPermissionDenied(BASIC_PERMISSION_REQUEST_CODE)
    @OnMPermissionNeverAskAgain(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionFailed() {
        Toast.makeText(this, "音视频通话所需权限未全部授权，部分功能可能无法正常运行！", Toast.LENGTH_SHORT).show();
        onPermissionChecked();
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    static class TeamAVChatItem{
       private int id;
       private String account;
       private int status;
       private IVideoRender surfaceView;
       boolean showOnTop;
       boolean isLocal;
       boolean muteVideo;

       public String getAccount() {
           return account;
       }

       public void setAccount(String account) {
           this.account = account;
       }

       public int getStatus() {
           return status;
       }

       public void setStatus(int status) {
           this.status = status;
       }

       public IVideoRender getSurfaceView() {
           return surfaceView;
       }

       public void setSurfaceView(IVideoRender surfaceView) {
           this.surfaceView = surfaceView;
       }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    private void endMeeting(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/CloudMetting/endMetting";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String,String> map=new HashMap<>();
                map.put("mettingId",meetingId);
                map.put("mettingText","");
                System.out.println("map========="+map.toString());
                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("endMeeting==============="+jsonObj);

            }

            @Override
            public void onError(String error) {

            }
        });
    }

    List<MeetingJoinPersonEntity> joinList;
    private void getMeetingInfo(final String meetingId){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/CloudMetting/getEndMettingInfo";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String,String> map=new HashMap<>();
                map.put("mettingId",meetingId);
                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("getMeetingInfo================"+jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code==1) {
                        joinList= new Gson().fromJson(jsonObj.getString("joinPersonInfo"), new TypeToken<List<MeetingJoinPersonEntity>>(){}.getType());
                        initPersonsLayout();
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
}
