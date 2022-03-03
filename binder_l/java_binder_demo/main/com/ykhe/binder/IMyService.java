package com.ykhe.binder;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IMyService extends IInterface {
    static final String DESCRIPTOR = "com.ykhe.binder.MyServer";
    public void sayHello(String str) throws RemoteException;
    static final int TRANSACTION_say = IBinder.FIRST_CALL_TRANSACTION;
}