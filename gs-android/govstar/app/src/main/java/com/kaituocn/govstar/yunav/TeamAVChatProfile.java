package com.kaituocn.govstar.yunav;

import android.content.Context;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.SharedPreferencesUtils;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.auth.constant.LoginSyncStatus;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.CustomNotification;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by hzchenkang on 2017/5/5.
 */

public class TeamAVChatProfile {

    private static final String KEY_ID = "id";
    private static final String KEY_MEMBER = "members";
    private static final String KEY_TID = "teamId";
    private static final String KEY_RID = "roomId";
    private static final String KEY_TNAME = "teamName";
    private static final String KEY_USERNAME = "userName";
    private static final String KEY_ICONURL = "iconUrl";

    private static final long OFFLINE_EXPIRY = 45 * 1000;

    private static final int ID = 3;

    private boolean isTeamAVChatting = false;
    private boolean isSyncComplete = true; // 未开始也算同步完成，可能存在不启动同步的情况

    public String buildContent(String roomId, String teamID, List<String> accounts, String teamName,String userName,String iconUrl) {
        JSONObject json = new JSONObject();
        json.put(KEY_ID, ID);
        JSONArray array = new JSONArray();
        array.add(getUserId());
        for (String account : accounts) {
            array.add(account);
        }
        json.put(KEY_MEMBER, array);
        json.put(KEY_TID, teamID);
        json.put(KEY_RID, roomId);
        json.put(KEY_TNAME, teamName);
        json.put(KEY_USERNAME, userName);
        json.put(KEY_ICONURL, iconUrl);
        return json.toString();
    }

    private JSONObject parseContentJson(CustomNotification notification) {
        if (notification != null) {
            String content = notification.getContent();
            return JSONObject.parseObject(content);
        }
        return null;
    }

    private boolean isTeamAVChatInvite(JSONObject json) {
        if (json != null) {
            int id = json.getInteger(KEY_ID);
            return id == ID;
        }
        return false;
    }

    /**
     * 监听自定义通知消息，id = 3 是群视频邀请
     *
     * @param register
     */

    private Observer<CustomNotification> customNotificationObserver = new Observer<CustomNotification>() {
        @Override
        public void onEvent(CustomNotification customNotification) {
            try {
                JSONObject jsonObject = parseContentJson(customNotification);
                System.out.println("customNotificationObserver============="+jsonObject);
                // 收到群视频邀请
                if (isTeamAVChatInvite(jsonObject)) {
                    final String roomId = jsonObject.getString(KEY_RID);
                    final String teamId = jsonObject.getString(KEY_TID);
                    JSONArray accountArray = jsonObject.getJSONArray(KEY_MEMBER);
                    final ArrayList<String> accounts = new ArrayList<>();
                    final String teamName = jsonObject.getString(KEY_TNAME);
                    final String userName = jsonObject.getString(KEY_USERNAME);
                    final String iconUrl = jsonObject.getString(KEY_ICONURL);
                    if (accountArray != null) {
                        for (Object o : accountArray) {
                            accounts.add((String) o);
                            System.out.println("accounts==============="+o);
                        }
                    }

                    // 接收到群视频邀请，启动来点界面
                    if (isTeamAVChatting ) {
                        Toast.makeText(getContext(), "正在进行视频通话", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (isSyncComplete || !checkOfflineOutTime(customNotification)) {
                        isTeamAVChatting = true;
                        launchActivity(teamId, roomId, accounts, teamName,userName,iconUrl);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    private void launchActivity(final String teamId, final String roomId, final ArrayList<String> accounts, final String teamName,final String userName,final String iconUrl) {

        TeamAVChatActivity.startActivity(getContext(), true,  roomId, accounts, teamId,teamName,userName,iconUrl);

    }

    private Observer<LoginSyncStatus> loginSyncStatusObserver = new Observer<LoginSyncStatus>() {
        @Override
        public void onEvent(LoginSyncStatus loginSyncStatus) {
            isSyncComplete = (loginSyncStatus == LoginSyncStatus.SYNC_COMPLETED ||
                    loginSyncStatus == LoginSyncStatus.NO_BEGIN);
        }
    };

    public boolean checkOfflineOutTime(CustomNotification notification) {
        // 时间差在45s内，考虑本地时间误差，条件适当放宽
        long time = System.currentTimeMillis() - notification.getTime();
        return time > OFFLINE_EXPIRY || time < -OFFLINE_EXPIRY;
    }

    public void setTeamAVChatting(boolean teamAVChatting) {
        isTeamAVChatting = teamAVChatting;
    }

    public boolean isTeamAVChatting() {
        return isTeamAVChatting;
    }

    public void registerObserver(boolean register) {
        NIMClient.getService(AuthServiceObserver.class).observeLoginSyncDataStatus(loginSyncStatusObserver, register);
        NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(customNotificationObserver, register);
    }

    public static TeamAVChatProfile sharedInstance() {
        return InstanceHolder.teamAVChatProfile;
    }

    private static class InstanceHolder {
        private final static TeamAVChatProfile teamAVChatProfile = new TeamAVChatProfile();
    }


    private static Context context;


    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        TeamAVChatProfile.context = context;
    }

    public static String getUserId() {

        return SharedPreferencesUtils.getParam(context,SharedPreferencesUtils.KEY_UID,"").toString();
    }

    public static void setUserId(String account) {
        SharedPreferencesUtils.setParam(context,SharedPreferencesUtils.KEY_UID,account);
    }

    public static String getToken() {
        return SharedPreferencesUtils.getParam(context,SharedPreferencesUtils.KEY_YUN_TOKEN,"").toString();
    }

    public static void setToken(String token) {
        SharedPreferencesUtils.setParam(context,SharedPreferencesUtils.KEY_YUN_TOKEN,token);
    }

    public static void login(String account,String token) {
        NIMClient.getService(AuthService.class).login(new LoginInfo(account,token))
                .setCallback(new RequestCallback<LoginInfo>() {

                    @Override
                    public void onSuccess(LoginInfo param) {
                        System.out.println("login onSuccess================" + param.getAccount());
                        TeamAVChatProfile.sharedInstance().registerObserver(true);
                    }

                    @Override
                    public void onFailed(int code) {
                        System.out.println("onFailed================" + code);
                    }

                    @Override
                    public void onException(Throwable exception) {
                        System.out.println("onException================" + exception.toString());
                    }
                });
    }

    public static void loginObserver(){
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(
                new Observer<StatusCode>() {
                    public void onEvent(StatusCode status) {
                        System.out.println("loginObserver status=============="+status.toString());
                        if (status== StatusCode.LOGINED) {
                            TeamAVChatProfile.sharedInstance().registerObserver(true);
                        }else if (status.wontAutoLogin()) {
                            // 被踢出、账号被禁用、密码错误等情况，自动登录失败，需要返回到登录界面进行重新登录操作
//                            login(getUserId(), getToken());
                            getYunToken();
                        }
                    }
                }, true);
    }

    public static void getYunToken(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/CloudMetting/getToken";
            }

            @Override
            public Map<String, String> getParams() {
                return null;
            }

            @Override
            public org.json.JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(org.json.JSONObject jsonObj) {
                System.out.println("jsonObj========="+jsonObj.toString());
                try {
                    int code = jsonObj.getInt("code");
                    if (code==1) {
                        org.json.JSONObject data = jsonObj.getJSONObject("data");
                        String token=data.getString("token");
                        System.out.println("token============="+token);
                        TeamAVChatProfile.setToken(token);

                        login(getUserId(),getToken());
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
