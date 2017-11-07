package com.snow.yp.kgdemo.IPC.Serializable;

import java.io.Serializable;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by y on 2017/11/5.
 */

public class BeanFooSerial implements Serializable {

    /**
     * 生成原则，一般由java代码根据包名结构生成hashcode ，也可以任意指定 入1l；
     * 作用，序列化的时候会将serialVersionUID写入序列化文件中，当进行反序列化的时候，会去匹配UID，如果不一致，则抛出异常
     * 如果UID一直，bean类发生了改变，反序列会最大限度的还原数据。
     */
    private static final long serialVersionUID =38384738;

    String name;
    String pwd;


    public BeanFooSerial(String name, String pwd) {
        this.name = name;
        this.pwd = pwd;
    }
}
