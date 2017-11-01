package com.snow.yp.kgdemo.Handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.snow.yp.kgdemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.snow.yp.kgdemo.R.id.tv_show;
import static java.lang.Thread.currentThread;

public class HandlerActivity extends AppCompatActivity {
    ThreadLocal<Boolean> booleanThreadLocal = new ThreadLocal<>();
    @BindView(tv_show)
    TextView tvShow;
    private StringBuffer content;
    private Handler handler;
    private Handler mainHandler= new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler);
        ButterKnife.bind(this);
        content = new StringBuffer();
        verificationThreadLocal();
        constractThreadHandler();

        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvShow.setText(content.toString());
            }
        }, 1000);

    }

    //验证threadLocal 资源独立
    private void verificationThreadLocal() {
        booleanThreadLocal.set(true);
        content.append("#main:state" + booleanThreadLocal.get() + "\n");

        new Thread(new Runnable() {
            @Override
            public void run() {
                booleanThreadLocal.set(false);
                content.append("#thread1:state" + booleanThreadLocal.get() + "\n");
                Log.i("mu_zi", "#thread1:state" + content.toString() + "\n");
                Log.i("mu_zi", "#thread1:state" + booleanThreadLocal.get() + "\n");

            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                content.append("#thread2:state" + booleanThreadLocal.get() + "\n");
                Log.i("mu_zi", "#thread2:state" + booleanThreadLocal.get() + "\n");
                content.append("验证：1、ThreadLocal不同线程中存储的数据不一样，相互隔离\n" +
                        "2、成员变量在不同的线程之间是共享的\n");

            }
        }).start();


    }


    //1、构建threadHandler
    private void constractThreadHandler() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                handler = new Handler();
                Looper.loop();
                Log.i("mu_zi", "threadHandler" + currentThread().getName() + "heheda");

            }
        }, "#thread11");
        Log.i("mu_zi", "constractThreadHandler");

        thread.start();

        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                       String threadName = Thread.currentThread().getName();
                        Log.i("mu_zi", "我从"+threadName+"来");
                    }
                });
            }
        }, 3000);
        getMainLooper();
    }

    /**
     *
     *  从上面子线程构建looper和handler的过程来看，
     *  首先，线程中调用Looper.prepare();进行初始化，其中相关的是threadlocal进行looper存储，如果多次创建的话会抛出异常，per thread per looper
     *  然后是 handler 的初始化，handler会去持有looper
     *  接着Looper.loop运行loop实现线程阻塞，无限读取消息。
     *  当前线程执行looper.loop之后，下面一行的log代码没有运行，说明当前线程已经阻塞了。
     *  q:主线程为什么没有阻塞掉  a:
     *  Looper 中有消息队列，queue。handler的运行就是想queue中插入message，looper根据next的方法查询是否存在message，有就执行，没有就阻塞直到关闭或者新消息的到来
     *
     *
     */

}
