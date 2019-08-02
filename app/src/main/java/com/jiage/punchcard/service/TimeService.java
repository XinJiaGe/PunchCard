package com.jiage.punchcard.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.jiage.punchcard.MainActivity;
import com.jiage.punchcard.R;
import com.jiage.punchcard.sharedPreference.SharedPreference;
import com.jiage.punchcard.sharedPreference.SharedPreferenceConfig;
import com.jiage.punchcard.util.SDDateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Notification.VISIBILITY_SECRET;

public class TimeService extends Service {
    private IntentFilter intentFilter;
    private String CHANNEL_ID = "jiage";
    private TimeChangeReceiver timeChangeReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("myService","onCreate");

        notification();

        timeChangeReceiver = new TimeChangeReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);//每分钟变化
        registerReceiver(timeChangeReceiver,intentFilter);
    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("myService","onStartCommand");
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i("myService","onDestroy");
        unregisterReceiver(timeChangeReceiver);
        stopForeground(true);
        //发送重新启动的广播
        Intent intent = new Intent("com.jiage.destroy");
        sendBroadcast(intent);
        super.onDestroy();
    }

    class TimeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_TIME_TICK:
                    //每过一分钟 触发
                    Log.i("myReceiver","过了一分钟");
                    notification();
                    break;
            }
        }
    }

    private void notification(){
        //现在
        String newhh = SDDateUtil.getNow_HH();
        String newmm = SDDateUtil.getNow_mm();
        SharedPreferenceConfig sp = SharedPreference.getSharedPreference(this);
        String top = sp.getTOP();
        String bot = sp.getBOT();
        //早上
        String[] toptimes = top.split("-");
        String tophh = toptimes[0];
        String topmm = toptimes[1];
        //晚上
        String[] bottimes = bot.split("-");
        String bothh = bottimes[0];
        String botmm = bottimes[1];

        String text = "下次打卡时间：";
        if(Integer.parseInt(newhh)>Integer.parseInt(tophh)&&Integer.parseInt(newhh)<Integer.parseInt(bothh)){//二阶段
            text += bothh+"时"+botmm+"分";
        }else if(Integer.parseInt(newhh)>=0&&Integer.parseInt(newhh)<Integer.parseInt(tophh)){//一阶段
            text += tophh+"时"+topmm+"分";
        }else{
            text += "明日"+tophh+"时"+topmm+"分";
        }
        if(Integer.parseInt(newhh) == Integer.parseInt(tophh)&&Integer.parseInt(newmm) == Integer.parseInt(topmm)){
            PackageManager packageManager = this.getPackageManager();
            Intent intent= packageManager.getLaunchIntentForPackage("com.tencent.wework");
            startActivity(intent);
        }
        if(Integer.parseInt(newhh) == Integer.parseInt(bothh)&&Integer.parseInt(newmm) == Integer.parseInt(botmm)){
            PackageManager packageManager = this.getPackageManager();
            Intent intent= packageManager.getLaunchIntentForPackage("com.tencent.wework");
            startActivity(intent);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder notification = getNotificationBuilder(this, notificationManager, "主服务", text, CHANNEL_ID);
        notification.setOnlyAlertOnce(true);
        notification.setDefaults(Notification.FLAG_ONLY_ALERT_ONCE);
        notification.setSound(null,null);
        notification.setOnlyAlertOnce(true);
        startForeground(1,notification.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static Notification.Builder getNotificationBuilder(Context context,NotificationManager notificationManager, String title, String content, String channelId) {
        //大于8.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //id随便指定
            NotificationChannel channel = new NotificationChannel(channelId, context.getPackageName(), NotificationManager.IMPORTANCE_DEFAULT);
            channel.canBypassDnd();//可否绕过，请勿打扰模式
//            channel.enableLights(true);//闪光
            channel.setLockscreenVisibility(VISIBILITY_SECRET);//锁屏显示通知
//            channel.setLightColor(Color.RED);//指定闪光是的灯光颜色
            channel.canShowBadge();//桌面laucher消息角标
//            channel.enableVibration(true);//是否允许震动
//            channel.setSound(null, null);
            //channel.getAudioAttributes();//获取系统通知响铃声音配置
            channel.getGroup();//获取通知渠道组
//            channel.setBypassDnd(true);//设置可以绕过，请勿打扰模式
//            channel.setVibrationPattern(new long[]{100, 100, 200});//震动的模式，震3次，第一次100，第二次100，第三次200毫秒
//            channel.shouldShowLights();//是否会闪光
            //通知管理者创建的渠道
            notificationManager.createNotificationChannel(channel);

        }
        return new Notification.Builder(context)
                .setAutoCancel(true)
                .setChannelId(channelId)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_launcher);
    }
}
