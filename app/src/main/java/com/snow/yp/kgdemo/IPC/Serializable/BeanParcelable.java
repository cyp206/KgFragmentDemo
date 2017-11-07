package com.snow.yp.kgdemo.IPC.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by y on 2017/11/5.
 */

public class BeanParcelable implements Parcelable {
    public int id;
    public String desc;
    public SubParcelable subParcelable;


    /**
     * 生成原则
     * 1、Parcel参数构造函数为从序列化后的对象创建原始对象，当读取的是一个自定bean时，需要传入线程的上下文加载器
     * 2、writeToParcel 写入序列化结构中
     *
     */
    public BeanParcelable(int id, String desc, SubParcelable subParcelable) {
        this.id = id;
        this.desc = desc;
        this.subParcelable = subParcelable;
    }

    protected BeanParcelable(Parcel in) {
        id = in.readInt();
        desc = in.readString();
        subParcelable = in.readParcelable(Thread.currentThread().getContextClassLoader());
    }

    public static final Creator<BeanParcelable> CREATOR = new Creator<BeanParcelable>() {
        @Override
        public BeanParcelable createFromParcel(Parcel in) {
            return new BeanParcelable(in);
        }

        @Override
        public BeanParcelable[] newArray(int size) {
            return new BeanParcelable[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(desc);
        dest.writeParcelable(subParcelable, 0);

    }


    public static class SubParcelable implements Parcelable {

        String content;


        public SubParcelable(String content) {
            this.content = content;
        }

        protected SubParcelable(Parcel in) {
            content = in.readString();
        }

        public static final Creator<SubParcelable> CREATOR = new Creator<SubParcelable>() {
            @Override
            public SubParcelable createFromParcel(Parcel in) {
                return new SubParcelable(in);
            }

            @Override
            public SubParcelable[] newArray(int size) {
                return new SubParcelable[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(content);
        }
    }
}
