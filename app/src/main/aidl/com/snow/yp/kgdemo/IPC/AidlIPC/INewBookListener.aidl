// INewBookListener.aidl
package com.snow.yp.kgdemo.IPC.AidlIPC;

// Declare any non-default types here with import statements

import com.snow.yp.kgdemo.IPC.aidl.Book;

interface INewBookListener {
    void  newBookArray(in List<Book>  books);
}
