package com.snow.yp.kgdemo.IPC.AidlIPC;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.snow.commonlibrary.log.MyLog;
import com.snow.yp.kgdemo.IPC.aidl.Book;
import com.snow.yp.kgdemo.IPC.aidl.IBookManager;
import com.snow.yp.kgdemo.R;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by y on 2017/11/6.
 */

public class BookManagerService extends Service {

    private CopyOnWriteArrayList<Book> bookList;
    private RemoteCallbackList<INewBookListener> remoteCallbackList = new RemoteCallbackList<>();
    private CopyOnWriteArrayList<INewBookListener> listeners;

    private IBinder binder = new IBookManager.Stub() {

        @Override
        public List<Book> getBookList() throws RemoteException {
            return bookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {

        }

        @Override
        public void register(INewBookListener iNewBookListener) throws RemoteException {

            if (iNewBookListener != null)
                remoteCallbackList.register(iNewBookListener);
//            listeners.add(iNewBookListener);

        }

        @Override
        public void unRegister(INewBookListener iNewBookListener) throws RemoteException {
            if (remoteCallbackList != null)
                remoteCallbackList.unregister(iNewBookListener);
        }

    };

    private AtomicBoolean mIsThreadAlive = new AtomicBoolean(true);

    @Override
    public void onCreate() {
        super.onCreate();
        MyLog.i("BookManagerService" + "onCreate");

        bookList = new CopyOnWriteArrayList<>();
        listeners = new CopyOnWriteArrayList<>();
        bookList.add(new Book("菊与刀", 001));
        bookList.add(new Book("三体", 002));

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mIsThreadAlive.get()) {
                    try {
                        Thread.currentThread().sleep(3000);
                        CopyOnWriteArrayList<Book> books = new CopyOnWriteArrayList<Book>();
                        Book newOne = new Book("山南海北" + bookList.size() + 1, bookList.size() + 1);
                        MyLog.i("server:" + newOne.name);
                        books.add(newOne);
                        bookList.add(newOne);
                        dispatchNewBookList(books);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        MyLog.i("BookManagerService" + "onBind");
        return binder;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showNotificcaiton();
        return super.onStartCommand(intent, flags, startId);
    }


    private void dispatchNewBookList(List<Book> books) {
        if (remoteCallbackList == null || remoteCallbackList.getRegisteredCallbackCount() == 0)
            return;

        int N = remoteCallbackList.beginBroadcast();
        for (int i = 0; i < N; i++) {
            try {
                INewBookListener listener = remoteCallbackList.getBroadcastItem(i);
                if (listener != null)
                    listener.newBookArray(books);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        remoteCallbackList.finishBroadcast();

    }


    @Override
    public void onDestroy() {
        mIsThreadAlive.set(false);
        super.onDestroy();
    }

    public void showNotificcaiton() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("heheda")
                .setContentText("contentext")
                .setContentTitle("contenttitle")
                .build();

        notificationManager.notify(12, notification);
    }

}
