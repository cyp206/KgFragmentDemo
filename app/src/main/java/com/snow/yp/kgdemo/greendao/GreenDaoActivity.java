package com.snow.yp.kgdemo.greendao;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.snow.commonlibrary.log.MyLog;
import com.snow.yp.kgdemo.R;
import com.snow.yp.kgdemo.greendao.manager.DaoManager;
import com.snow.yp.kgdemo.greendao.manager.FoolDaoUtils;

import java.util.List;
import java.util.Random;

public class GreenDaoActivity extends AppCompatActivity {
    private static final String TAG = GreenDaoActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_green_dao);

        long id = new Random().nextLong();
        FoolDaoUtils.insertFool(new FoolBean(id, "muzi" + id, "Content" + id));

        List<FoolBean> foolBeen = FoolDaoUtils.sqlAll();
        StringBuilder content = new StringBuilder();

        for (FoolBean bean : foolBeen) {
            content.append(TAG + ":" + bean.getName() + "_____" + bean.getId() + "\n");
        }

        ((TextView) findViewById(R.id.tv_show)).setText(content);
    }

    @Override
    protected void onDestroy() {
        DaoManager.getInstance(GreenDaoActivity.this).closeConnection();
        super.onDestroy();

    }
}
