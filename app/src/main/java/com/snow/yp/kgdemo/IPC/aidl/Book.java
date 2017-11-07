package com.snow.yp.kgdemo.IPC.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by y on 2017/11/5.
 */

public class Book implements Parcelable {
    public String name;
    public int id;

    public Book(String name, int id) {
        this.name = name;
        this.id = id;
    }

    protected Book(Parcel in) {
        name = in.readString();
        id = in.readInt();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(id);

    }
}
