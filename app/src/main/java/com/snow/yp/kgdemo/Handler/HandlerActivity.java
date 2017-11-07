package com.snow.yp.kgdemo.Handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.snow.commonlibrary.log.MyLog;
import com.snow.yp.kgdemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.snow.yp.kgdemo.R.id.tv_show;
import static java.lang.Thread.currentThread;


/**
 * 10、Handler
 */
public class HandlerActivity extends AppCompatActivity {
    ThreadLocal<Boolean> booleanThreadLocal = new ThreadLocal<>();
    @BindView(tv_show)
    TextView tvShow;
    private StringBuffer content;
    private Handler handler;
    private Handler mainHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler);
        ButterKnife.bind(this);
        content = new StringBuffer();
        verificationThreadLocal();
        constractThreadHandler();
        verificationLoopBlock();
        verificationHandlerCallback();
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
                MyLog.i("#thread1:state" + content.toString() + "\n");
                MyLog.i("#thread1:state" + booleanThreadLocal.get() + "\n");

            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                content.append("#thread2:state" + booleanThreadLocal.get() + "\n");
                MyLog.i("#thread2:state" + booleanThreadLocal.get() + "\n");
                content.append("验证：\n1、ThreadLocal不同线程中存储的数据不一样，相互隔离\n" +
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
                MyLog.i("threadHandler" + currentThread().getName() + "heheda");

            }
        }, "#thread11");
        MyLog.i("constractThreadHandler");

        thread.start();

        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String threadName = Thread.currentThread().getName();
                        MyLog.i("我从" + threadName + "来");
                    }
                });
            }
        }, 3000);
        getMainLooper();
    }


    //验证阻塞问题
    private void verificationLoopBlock() {


//        Looper mainLooper = Looper.getMainLooper();

        MyLog.i("天空下着雪");

//        mainLooper.loop();
        MyLog.i("雪停了");

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Thread mianHandlerThread = Thread.currentThread();
                MyLog.i("verificationLoopBlock" + mianHandlerThread.getName() + "_" + mianHandlerThread.getId());
            }
        });
        MyLog.i("verificationLoopBlock" + Thread.currentThread().getName() + "_" + Thread.currentThread().getId());

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
    }


    //验证handler回掉流程
    private void verificationHandlerCallback() {

        Handler handler = new Handler(
                new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        MyLog.i("verificationHandlerCallback:Handler==>>  Callback()==>handleMessage");

                        return false;
                    }
                }
        ) {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                MyLog.i("verificationHandlerCallback: Handler Override==>> handleMessage");
//
//            }
        };


        handler.post(new Runnable() {
            @Override
            public void run() {
                MyLog.i("verificationHandlerCallback: handler==>>post==>> Runnable");

            }
        });


        Message message = Message.obtain(handler, new Runnable() {
            @Override
            public void run() {
                MyLog.i("verificationHandlerCallback: Message.obtain==>> Runnable");
            }
        });
        handler.sendMessage(new Message());

        handler.sendMessage(message);
        handler.post(new Runnable() {
            @Override
            public void run() {
                MyLog.i("verificationHandlerCallback: handler==>>post==>>[2]==>> Runnable");

            }
        });


    }


    /**
     *
     *  从上面子线程构建looper和handler的过程来看，
     *  首先，线程中调用Looper.prepare();进行初始化，其中相关的是threadlocal进行looper存储，如果多次创建的话会抛出异常，per thread per looper
     *  然后是 handler 的初始化，handler会去持有looper
     *  接着Looper.loop运行loop实现线程阻塞，无限读取消息。
     *  当前线程执行looper.loop之后，下面一行的log代码没有运行，说明当前线程已经阻塞了。
     *  q:主线程为什么没有阻塞掉
     *  a:activitythread 本来就是阻塞的，
     *  之后的交互操作由 ApplicationThread 和AMS进程间进行通信通过ams和binder 实现组件生命周期及其他
     *  AMS 通过进程间通信的方式完成activitythread 的请求并且回调ApplicationThread中的binder,
     *  接着Application会调用ActivityThread 的H，从Application的线程切换到ActivityThread 执行Activity相关的一系列操作
     *
     * Looper 中有消息队列，queue。handler的运行就是想queue中插入message，looper根据next的方法查询是否存在message，有就执行，没有就阻塞直到关闭或者新消息的到来
     *
     *
     */


    /**
     * handler 的相互关系
     * Looper 是单独的，但是可以通过thread获得，前提是此线程进行了Looper初始化。Looper中会初始MessageQueue
     * Handler  Handler 初始化的时候拿到Looper 前提是Looper初始化。通过Looper拿到MessageQueue
     * 插入：Handler通过向MessageQueue 插入消息
     * 执行：Looper 无限查询MessageQueue队列中的next是否为null进行执行操作。
     *
     */




    /**
     *
     * handler msg分发
     public void dispatchMessage(Message msg) {
     if (msg.callback != null) {
     handleCallback(msg);
     } else {
     if (mCallback != null) {
     if (mCallback.handleMessage(msg)) {
     return;
     }
     }
     handleMessage(msg);
     }
     }
     */



    // TODO: 2017/11/3   AMS ApplicationThread Binder 相关的知识，及流程





}
