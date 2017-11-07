package mu.snow.com.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Process;
import android.os.RemoteException;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by y on 2017/7/4.
 */

public class ServerService extends Service {


    private final IMyAidlInterface.Stub binder = new IMyAidlInterface.Stub() {

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {

            return super.onTransact(code, data, reply, flags);
        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public int getPid() throws RemoteException {
            return Process.myPid();
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    @Override
    public void onCreate() {
        Log.i("mu_zi","ServerService"+"onCreate()");
        super.onCreate();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void showNotification() {
        NotificationManager notificationManager = (NotificationManager) getApplication().getSystemService(NOTIFICATION_SERVICE);
//        PendingIntent intent =new PendingIntent(new Integer()MainActivity.class);
        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle("server title")
                .setContentText("server text")
                .build();
        notificationManager.notify(12, notification);

    }
}
