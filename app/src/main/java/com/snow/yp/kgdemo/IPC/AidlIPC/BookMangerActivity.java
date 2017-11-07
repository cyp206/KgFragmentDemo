package com.snow.yp.kgdemo.IPC.AidlIPC;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.view.View;

import com.snow.commonlibrary.log.MyLog;
import com.snow.yp.kgdemo.IPC.aidl.Book;
import com.snow.yp.kgdemo.IPC.aidl.IBookManager;
import com.snow.yp.kgdemo.R;
import com.snow.yp.kgdemo.camera.MuLog;

import java.util.List;

import static android.R.attr.name;

/**
 * Created by y on 2017/11/6.
 */

public class BookMangerActivity extends Activity {
    public static final String ACTION_BIND_SERVICE = "com.snow.yp.kgdemo.IPC.AidlIPC";
    private IBookManager iBookManager;
    private INewBookListener listener = new INewBookListener.Stub() {

        @Override
        public void newBookArray(List<Book> books) throws RemoteException {
            MuLog.i(String.format("client:size[%s]___book1name[%s]___book1id[%s]", books.size(), books.get(0).name, books.get(0).id + ""));
        }
    };

    ServiceConnection serviceConnection = new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iBookManager = IBookManager.Stub.asInterface(service);
            try {
                iBookManager.register(listener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_ativity);
//        Intent intent = new Intent(ACTION_BIND_SERVICE);
//        intent.setPackage(ACTION_BIND_SERVICE);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent intent1 = new Intent(this, BookManagerService.class);
        boolean b = bindService(intent1, serviceConnection, BIND_AUTO_CREATE);
        MyLog.i("BookMangerActivity:bind" + b);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (iBookManager != null)
                        MyLog.i("BookMangerActivity:" + iBookManager.getBookList().size());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }
}
