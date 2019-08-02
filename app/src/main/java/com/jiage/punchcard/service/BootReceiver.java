package com.jiage.punchcard.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.jiage.destroy")) {
            Log.i("myReceiver","重新启动service");
            //在这里写重新启动service的相关操作
            context.startForegroundService(new Intent(context, TimeService.class));
        }
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.i("myReceiver","手机开机了....");
            //在这里写重新启动service的相关操作
            context.startForegroundService(new Intent(context, TimeService.class));
        }
    }
}
