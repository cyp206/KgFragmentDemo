package com.snow.yp.kgdemo.adapter.holder;

/**
 * Created by y on 2017/11/1.
 */

public class JMBean {

    //主题
    public String topic;
    //对应class
    public Class<?> cls;

    public JMBean(String topic, Class<?> cls) {
        this.topic = topic;
        this.cls = cls;
    }
}
