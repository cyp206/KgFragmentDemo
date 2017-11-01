package com.snow.yp.kgdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.snow.commonlibrary.recycleview.BaseAdapter;
import com.snow.yp.kgdemo.Handler.HandlerActivity;
import com.snow.yp.kgdemo.adapter.MainAdapter;
import com.snow.yp.kgdemo.adapter.holder.JMBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_rl)
    RecyclerView recyclerView;
    private MainAdapter mainAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
        mainAdapter = new MainAdapter(this);
        mainAdapter.setData(getDatas());
        mainAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView.Adapter adapter, Object data, int position) {
                Intent intent = new Intent(MainActivity.this, ((JMBean) data).cls);
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mainAdapter);
    }

    public List<JMBean> getDatas() {
        List<JMBean> datas = new ArrayList<>();
        datas.add(new JMBean("10、Handler", HandlerActivity.class));
        return datas;
    }
}
