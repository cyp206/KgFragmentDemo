package com.snow.yp.kgdemo.IPC;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.snow.yp.kgdemo.xfomade.ActionBarActivity;
import com.snow.yp.kgdemo.R;

/**
 * Created by y on 2017/7/3.
 */

public class MyService extends Service {
    private static final String TAG = "MyService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        return new MyBinder();
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        super.onCreate();


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        showNotificcaiton();
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();

    }


    public void showNotificcaiton() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, ActionBarActivity.class), 0);

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("heheda")
                .setContentText("contentext")
                .setContentTitle("contenttitle")
                //  .setColor(Color.BLUE)
                .build();

        notificationManager.notify(12, notification);
    }


   public class MyBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }
}
