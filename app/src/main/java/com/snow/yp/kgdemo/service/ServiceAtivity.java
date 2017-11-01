package com.snow.yp.kgdemo.service;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.snow.yp.kgdemo.R;

import mu.snow.com.myapplication.IMyAidlInterface;

public class ServiceAtivity extends AppCompatActivity {
    private final String ACTION_BIND_SERVICE = "mu.snow.com.myapplication";

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("MyService", "onServiceConnected: ");
//             service
//            IMyAidInter

            IMyAidlInterface iMyAidlInterface = IMyAidlInterface.Stub.asInterface(service);
            if (iMyAidlInterface != null) {
                try {
                    int pid =iMyAidlInterface.getPid();
                    Log.i("snow__Client", pid + "");
                    Log.i("snow_server", "" + Process.myPid());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            if (service instanceof MyService.MyBinder) {
                ((MyService.MyBinder) service).getService().showNotificcaiton();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("MyService", "onServiceDisconnected: ");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_ativity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ACTION_BIND_SERVICE);
                intent.setPackage(ACTION_BIND_SERVICE);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                bindService(new Intent(ServiceAtivity.this, MyService.class), serviceConnection, BIND_AUTO_CREATE);
                bindService(intent, serviceConnection, BIND_AUTO_CREATE);
//                startService(new Intent(getApplicationContext(), MyService.class));
            }
        });

    }

}
