package com.snow.yp.kgdemo.IPC.aidl;

import com.snow.yp.kgdemo.IPC.aidl.Book;
import com.snow.yp.kgdemo.IPC.AidlIPC.INewBookListener;


interface IBookManager{

List<Book> getBookList();
void addBook(in Book book);


void register(in INewBookListener iNewBookListener);

void unRegister(in INewBookListener iNewBookListener);

}