package com.kaituocn.govstar.yunav;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kaituocn.govstar.set.ServiceVersionActivity;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Intent i = new Intent(context, TeamAVChatActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startActivity(i);
    }
}
