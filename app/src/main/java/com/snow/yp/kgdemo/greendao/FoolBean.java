package com.snow.yp.kgdemo.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by y on 2017/11/10.
 */

@Entity
public class FoolBean {

    @Id(autoincrement = true)
    private long id;
    private String name;
    @NotNull
    private String content;
    @Generated(hash = 916560894)
    public FoolBean(long id, String name, @NotNull String content) {
        this.id = id;
        this.name = name;
        this.content = content;
    }
    @Generated(hash = 1182515710)
    public FoolBean() {
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }


}
