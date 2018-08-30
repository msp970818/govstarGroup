package com.kaituocn.govstar.yunav;

import android.content.Context;

import com.kaituocn.govstar.util.Constant;
import com.kaituocn.govstar.work.cloudmeeting.MeetingSignUpActivity;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.model.AVChatChannelInfo;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.CustomNotificationConfig;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.constant.VerifyTypeEnum;
import com.netease.nimlib.sdk.team.model.CreateTeamResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Control {

    public static void login(String account,String token) {
        NIMClient.getService(AuthService.class).login(new LoginInfo(account,token)).setCallback(new RequestCallbackWrapper() {
            @Override
            public void onResult(int code, Object result, Throwable exception) {

            }
        });
    }
    public static void createTeam() {
        TeamTypeEnum type = TeamTypeEnum.Normal;
        // 创建时可以预设群组的一些相关属性，如果是普通群，仅群名有效。
        // fields 中，key 为数据字段，value 对对应的值，该值类型必须和 field 中定义的 fieldType 一致
        HashMap<TeamFieldEnum, Serializable> fields = new HashMap<TeamFieldEnum, Serializable>();
        fields.put(TeamFieldEnum.Name, "测试群1");
        fields.put(TeamFieldEnum.Introduce, "简介群简介");
        fields.put(TeamFieldEnum.VerifyType, VerifyTypeEnum.Free);
        List<String> accounts = new ArrayList<>();
        accounts.add("a1");
        accounts.add("a2");
        accounts.add("a3");
        accounts.add("a4");
        NIMClient.getService(TeamService.class).createTeam(fields, type, "", accounts).setCallback(new RequestCallback<CreateTeamResult>() {
            @Override
            public void onSuccess(CreateTeamResult param) {
                System.out.println("createTeam onSuccess===============" + param.getTeam().getId() + "   " + param.getTeam().getName());
            }

            @Override
            public void onFailed(int code) {
                System.out.println("createTeam onFailed===========" + code);
            }

            @Override
            public void onException(Throwable exception) {
                System.out.println("onException===========" + exception.toString());
            }
        });
    }

    public static void addMembersToTeam() {
        List<String> accounts = new ArrayList<>();
        accounts.add("a1");
        accounts.add("a2");
        accounts.add("a3");
        accounts.add("a4");
        NIMClient.getService(TeamService.class).addMembers("618710967", accounts).setCallback(new RequestCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> param) {
                System.out.println("onSuccess============" + param.toString());
            }

            @Override
            public void onFailed(int code) {
                System.out.println("onFailed================" + code);
            }

            @Override
            public void onException(Throwable exception) {

            }
        });
    }

    public static void createRoom() {
        System.out.println("createRoom sss=============");
        AVChatManager.getInstance().createRoom("123456", "avchat test", new AVChatCallback<AVChatChannelInfo>() {
            @Override
            public void onSuccess(AVChatChannelInfo avChatChannelInfo) {
                System.out.println("createRoom onSuccess==================" + avChatChannelInfo.toString());
            }

            @Override
            public void onFailed(int code) {
                System.out.println("createRoom  code=============" + code);
            }

            @Override
            public void onException(Throwable exception) {
                System.out.println("=======================" + exception.toString());
            }
        });
    }

    public static void createChatRoom(final Context context, final ArrayList<String> list, final String teamName, final String meetingId,final String iconUrl) {
        final String roomId = UUID.randomUUID().toString().replaceAll("-", "");
        System.out.println("createChatRoom================================" + roomId);
        AVChatManager.getInstance().createRoom(roomId, null, new AVChatCallback<AVChatChannelInfo>() {
            @Override
            public void onSuccess(AVChatChannelInfo avChatChannelInfo) {
                System.out.println("onSuccess================" + avChatChannelInfo.toString());
                TeamAVChatProfile.sharedInstance().setTeamAVChatting(true);
                for (int i = 0; i < list.size(); i++) {
                    System.out.println("list id===================="+list.get(i));
                }
                notificationMembers(roomId,meetingId,list,Constant.userEntity.getPersonInfo().getName(),teamName,iconUrl);
                TeamAVChatActivity.startActivity(context, false, roomId, list, meetingId, teamName,Constant.userEntity.getPersonInfo().getName(),iconUrl);
                if (context instanceof MeetingSignUpActivity) {
                    if(((MeetingSignUpActivity) context).actionView1!=null){
                        ((MeetingSignUpActivity) context).actionView1.setEnabled(false);
                    }
                }

            }

            @Override
            public void onFailed(int code) {
                System.out.println("createChatRoom onFailed================" + code);
            }

            @Override
            public void onException(Throwable exception) {
                System.out.println("createChatRoom onFailed================" + exception.toString());
            }
        });
    }

    private static void notificationMembers(String roomId,String teamId,List<String> accounts,String userName,String teamName,String iconUrl){
        String content = TeamAVChatProfile.sharedInstance().buildContent(roomId, teamId, accounts, teamName,userName,iconUrl);
        CustomNotificationConfig config = new CustomNotificationConfig();
        config.enablePush = true;
        config.enablePushNick = false;
        config.enableUnreadCount = true;

        for (String account : accounts) {
            if (account.equals(TeamAVChatProfile.getUserId())) {
                continue;
            }
            CustomNotification command = new CustomNotification();
            command.setSessionId(account);
            command.setSessionType(SessionTypeEnum.P2P);
            command.setConfig(config);
            command.setContent(content);
            command.setApnsText("网络会议");

            command.setSendToOnlineUserOnly(false);
            NIMClient.getService(MsgService.class).sendCustomNotification(command);
        }
    }

}
