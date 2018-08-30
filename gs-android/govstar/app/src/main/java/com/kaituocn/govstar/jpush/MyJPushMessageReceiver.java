package com.kaituocn.govstar.jpush;

import android.content.Context;

import com.kaituocn.govstar.util.SharedPreferencesUtils;

import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.service.JPushMessageReceiver;

/**
 * 自定义JPush message 接收器,包括操作tag/alias的结果返回(仅仅包含tag/alias新接口部分)
 * */
public class MyJPushMessageReceiver extends JPushMessageReceiver {


    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        if (jPushMessage.getErrorCode()==0) {
            SharedPreferencesUtils.setParam(context,SharedPreferencesUtils.KEY_JPUSH_ALIAS,jPushMessage.getAlias()==null?"":jPushMessage.getAlias());
            System.out.println("KEY_JPUSH_ALIAS=============="+jPushMessage.getAlias());
        }
        System.out.println("jPushMessage================"+jPushMessage.toString());
    }

}
