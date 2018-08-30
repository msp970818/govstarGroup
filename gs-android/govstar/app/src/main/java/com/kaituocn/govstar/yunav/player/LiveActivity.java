package com.kaituocn.govstar.yunav.player;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kaituocn.govstar.R;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;
import com.netease.neliveplayer.playerkit.sdk.LivePlayer;
import com.netease.neliveplayer.playerkit.sdk.LivePlayerObserver;
import com.netease.neliveplayer.playerkit.sdk.PlayerManager;
import com.netease.neliveplayer.playerkit.sdk.constant.CauseCode;
import com.netease.neliveplayer.playerkit.sdk.model.MediaInfo;
import com.netease.neliveplayer.playerkit.sdk.model.StateInfo;
import com.netease.neliveplayer.playerkit.sdk.model.VideoBufferStrategy;
import com.netease.neliveplayer.playerkit.sdk.model.VideoOptions;
import com.netease.neliveplayer.playerkit.sdk.model.VideoScaleMode;
import com.netease.neliveplayer.playerkit.sdk.view.AdvanceSingleTextureView;
import com.netease.neliveplayer.playerkit.sdk.view.AdvanceSurfaceView;
import com.netease.neliveplayer.sdk.NEDefinitionData;
import com.netease.neliveplayer.sdk.NELivePlayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class LiveActivity extends Activity {
    public final static String TAG = LiveActivity.class.getSimpleName();
    private static final int SHOW_PROGRESS = 0x01;


    private ImageView mPlayBack;
    private View mBuffer; //用于指示缓冲状态
    private ImageView mPauseButton;
    private ImageView mSetPlayerScaleButton;
    private SeekBar mProgress;
    private TextView mEndTime;
    private TextView mCurrentTime;

    private AdvanceSurfaceView surfaceView;
    private AdvanceSingleTextureView textureView;

    private LivePlayer player;
    private MediaInfo mediaInfo;

    private String mVideoPath; //文件路径
    private String mDecodeType;//解码类型，硬解或软解
    private String mMediaType; //媒体类型
    private boolean mHardware = true;
    private boolean mBackPressed;
    private boolean mPaused = false;
    private boolean isMute = false;
    private boolean mIsFullScreen = false;
    protected boolean isPauseInBackgroud;




    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            long position;
            switch (msg.what) {
                case SHOW_PROGRESS:
                    position = setProgress();
                    msg = obtainMessage(SHOW_PROGRESS);
                    sendMessageDelayed(msg, 1000 - (position % 1000));
                    break;
            }
        }
    };

    private long setProgress() {
        if (player == null)
            return 0;

        int position = (int) player.getCurrentPosition();
        if (mCurrentTime != null) {
            mCurrentTime.setText(stringForTime(position));
        }
        return position;
    }


    private View.OnClickListener mPlayBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(TAG, "player_exit");
            mBackPressed = true;
            finish();
            releasePlayer();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN|WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_player);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //保持屏幕常亮

        PhoneCallStateObserver.getInstance().observeLocalPhoneObserver(localPhoneObserver, true);

        parseIntent();

        initView();


        initPlayer();
    }

    private void parseIntent() {
        //接收MainActivity传过来的参数
//        mMediaType = getIntent().getStringExtra("media_type");
//        mDecodeType = getIntent().getStringExtra("decode_type");
        mMediaType = "livestream";
        mDecodeType="hardware";

        mVideoPath = getIntent().getStringExtra("videoPath");
        System.out.println("Live mVideoPath==============="+mVideoPath);

        if (mMediaType != null && mMediaType.equals("localaudio")) { //本地音频文件采用软件解码
            mDecodeType = "software";
        }

        if (mDecodeType != null && mDecodeType.equals("hardware")) {
            mHardware = true;
        } else {
            mHardware = false;
        }

    }

    private void initView() {
        //这里支持使用SurfaceView和TextureView
        //surfaceView = findViewById(R.id.live_surface);
        textureView = findViewById(R.id.live_texture);

        mPlayBack = findViewById(R.id.player_exit);//退出播放

        mBuffer = findViewById(R.id.buffering_prompt);

        mPauseButton =  findViewById(R.id.mediacontroller_play_pause); //播放暂停按钮
        mPauseButton.setImageResource(R.drawable.nemediacontroller_play);
        mPauseButton.setOnClickListener(mPauseListener);

        mPlayBack.setOnClickListener(mPlayBackOnClickListener); //监听退出播放的事件响应
        mSetPlayerScaleButton =  findViewById(R.id.video_player_scale);  //画面显示模式按钮


        mProgress =  findViewById(R.id.mediacontroller_seekbar);  //进度条
        mProgress.setEnabled(false);

        mEndTime =  findViewById(R.id.mediacontroller_time_total); //总时长
        mEndTime.setText("--:--:--");
        mCurrentTime =  findViewById(R.id.mediacontroller_time_current); //当前播放位置
        mCurrentTime.setText("--:--:--");
        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        mSetPlayerScaleButton =  findViewById(R.id.video_player_scale);  //画面显示模式按钮
        mSetPlayerScaleButton.setOnClickListener(mSetPlayerScaleListener);

        findViewById(R.id.player_control_layout).setVisibility(View.GONE);

        findViewById(R.id.saveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMeetingRecord();
            }
        });
    }


    private View.OnClickListener mPauseListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (player.isPlaying()) {
                mPauseButton.setImageResource(R.drawable.nemediacontroller_pause);
                showToast("销毁播放");
                player.stop();
            } else {
                mPauseButton.setImageResource(R.drawable.nemediacontroller_play);
                showToast("重新播放");
                player.start(); // 播放
            }
        }
    };




    private void initPlayer() {
        VideoOptions options = new VideoOptions();
        options.autoSwitchDefinition = false;
        options.hardwareDecode = mHardware;

        /**
         * isPlayLongTimeBackground 控制退到后台或者锁屏时是否继续播放，开发者可根据实际情况灵活开发,我们的示例逻辑如下：
         * 使用软件解码：
         * isPlayLongTimeBackground 为 false 时，直播进入后台停止播放，进入前台重新拉流播放
         * isPlayLongTimeBackground 为 true 时，直播进入后台不做处理，继续播放,
         *
         * 使用硬件解码：
         * 直播进入后台停止播放，进入前台重新拉流播放
         */
        options.isPlayLongTimeBackground = !isPauseInBackgroud;

//        if (isTimestampEnable) {
//            options.isSyncOpen = true;
//        }

        options.bufferStrategy = VideoBufferStrategy.LOW_LATENCY;
        player = PlayerManager.buildLivePlayer(this, mVideoPath, options);

        intentToStartBackgroundPlay();

        start();

        if (surfaceView == null) {
            player.setupRenderView(textureView, VideoScaleMode.FIT);
        } else {
            player.setupRenderView(surfaceView, VideoScaleMode.FIT);
        }

    }



    private void start() {
        player.registerPlayerObserver(playerObserver, true);

//        player.registerPlayerCurrentSyncTimestampListener(pullIntervalTime, mOnCurrentSyncTimestampListener, true);
//        player.registerPlayerCurrentSyncContentListener(mOnCurrentSyncContentListener, true);

        player.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        if (player != null) {
            player.onActivityResume(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");

        enterBackgroundPlay();
        if (player != null) {
            player.onActivityStop(true);
        }
    }


    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed");
        mBackPressed = true;
        finish();
        releasePlayer();

        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        releasePlayer();

    }

    private void releasePlayer() {
        if (player == null) {
            return;
        }

        Log.i(TAG, "releasePlayer");

        player.registerPlayerObserver(playerObserver, false);

//        player.registerPlayerCurrentSyncTimestampListener(0, null, false);
//        player.registerPlayerCurrentSyncContentListener(null, false);

        PhoneCallStateObserver.getInstance().observeLocalPhoneObserver(localPhoneObserver, false);
        player.setupRenderView(null, VideoScaleMode.NONE);
        textureView.releaseSurface();
        textureView = null;
        player.stop();
        player = null;
        intentToStopBackgroundPlay();
        mHandler.removeCallbacksAndMessages(null);

    }

    //这里直播可以用 LivePlayerObserver 点播用 VodPlayerObserver
    private LivePlayerObserver playerObserver = new LivePlayerObserver() {

        @Override
        public void onPreparing() {

        }

        @Override
        public void onPrepared(MediaInfo info) {
            mediaInfo = info;
        }

        @Override
        public void onError(int code, int extra) {
            mBuffer.setVisibility(View.INVISIBLE);
            if(code == CauseCode.CODE_VIDEO_PARSER_ERROR) {
                showToast("视频解析出错");
            }else {
                AlertDialog.Builder build = new AlertDialog.Builder(LiveActivity.this);
//                build.setTitle("播放错误").setMessage("错误码：" + code)
//                        .setPositiveButton("确定", null)
//                        .setCancelable(false)
//                        .show();
                build.setTitle("提示").setMessage("播放已结束：" + code)
                        .setPositiveButton("确定", null)
                        .setCancelable(false)
                        .show();
            }

        }

        @Override
        public void onFirstVideoRendered() {
//            showToast("视频第一帧已解析");

        }

        @Override
        public void onFirstAudioRendered() {
//            showToast("音频第一帧已解析");

        }

        @Override
        public void onBufferingStart() {
            mBuffer.setVisibility(View.VISIBLE);
        }

        @Override
        public void onBufferingEnd() {
            mBuffer.setVisibility(View.GONE);
        }

        @Override
        public void onBuffering(int percent) {
            Log.d(TAG, "缓冲中..." + percent + "%");
        }

        @Override
        public void onHardwareDecoderOpen() {

        }

        @Override
        public void onStateChanged(StateInfo stateInfo) {

        }

        @Override
        public void onParseDefinition(List<NEDefinitionData> data) {
            showToast("解析到多个清晰度");

        }

        @Override
        public void onAutoSwitchDefinition(NEDefinitionData.DefinitionType definitionType) {
            showToast("自动切换到清晰度" + definitionType);

        }

        @Override
        public void onVideoFrameFilter(NELivePlayer.NEVideoRawData videoRawData) {

        }

        @Override
        public void onAudioFrameFilter(NELivePlayer.NEAudioRawData audioRawData) {

        }

        @Override
        public void onHttpResponseInfo(int code, String header) {
            Log.i(TAG, "onHttpResponseInfo,code:" + code + " header:" + header);
        }
    };


    private void showToast(String msg) {
        Log.d(TAG, "showToast" + msg);
        try {
            Toast.makeText(LiveActivity.this, msg, Toast.LENGTH_SHORT).show();
        } catch (Throwable th) {
            th.printStackTrace(); // fuck oppo
        }
    }

    private static String stringForTime(long position) {
        int totalSeconds = (int) ((position / 1000.0) + 0.5);

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds).toString();
    }





    private View.OnClickListener mSetPlayerScaleListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            player.setupRenderView(null, VideoScaleMode.NONE);
            if (mIsFullScreen) {
                mSetPlayerScaleButton.setImageResource(R.drawable.nemediacontroller_scale01);
                mIsFullScreen = false;
                player.setupRenderView(textureView, VideoScaleMode.FIT);

            } else {
                mSetPlayerScaleButton.setImageResource(R.drawable.nemediacontroller_scale02);
                mIsFullScreen = true;
                player.setupRenderView(textureView, VideoScaleMode.FULL);
            }
        }
    };


    /**
     * 时间戳回调
     */
    private NELivePlayer.OnCurrentSyncTimestampListener mOnCurrentSyncTimestampListener = new NELivePlayer.OnCurrentSyncTimestampListener() {
        @Override
        public void onCurrentSyncTimestamp(long timestamp) {
            Log.v(TAG, "OnCurrentSyncTimestampListener,onCurrentSyncTimestamp:" + timestamp);

        }
    };

    private NELivePlayer.OnCurrentSyncContentListener mOnCurrentSyncContentListener = new NELivePlayer.OnCurrentSyncContentListener() {
        @Override
        public void onCurrentSyncContent(List<String> content) {
            StringBuffer sb = new StringBuffer();
            for (String str : content) {
                sb.append(str + "\r\n");
            }
            showToast("onCurrentSyncContent,收到同步信息:" + sb.toString());
            Log.v(TAG, "onCurrentSyncContent,收到同步信息:" + sb.toString());
        }
    };

    /**
     * 处理service后台播放逻辑
     */
    private void intentToStartBackgroundPlay() {
        if (!mHardware && !isPauseInBackgroud) {
            PlayerService.intentToStart(this);
        }
    }

    private void intentToStopBackgroundPlay() {
        if (!mHardware && !isPauseInBackgroud) {
            PlayerService.intentToStop(this);
            player = null;
        }
    }


    private void enterBackgroundPlay() {
        if (!mHardware && !isPauseInBackgroud) {
            PlayerService.setMediaPlayer(player);
        }
    }

    private void stopBackgroundPlay() {
        if (!mHardware && !isPauseInBackgroud) {
            PlayerService.setMediaPlayer(null);
        }
    }

    //处理与电话逻辑
    private Observer<Integer> localPhoneObserver = new Observer<Integer>() {
        @Override
        public void onEvent(Integer phoneState) {
            if (phoneState == TelephonyManager.CALL_STATE_IDLE) {
                player.start();
            } else if (phoneState == TelephonyManager.CALL_STATE_RINGING) {
                player.stop();
            } else {
                Log.i(TAG, "localPhoneObserver onEvent " + phoneState);
            }

        }
    };

    private void saveMeetingRecord(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/CloudMetting/addMeetingMinutes";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String,String> map=new HashMap<>();
                map.put("mettingId",getIntent().getStringExtra("meetingId"));
                map.put("text",((EditText)findViewById(R.id.editText)).getText().toString());
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
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1) {
                        Util.showToast(getApplication(),"记录已保存！");
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
