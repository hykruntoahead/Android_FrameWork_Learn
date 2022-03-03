package com.ykhe.binder;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * author: ykhe
 * date: 22-2-11
 * email: ykhe@grandstream.cn
 * description:
 */
public class MyServiceProxy implements IMyService {
    private final IBinder mRemote; // BpBinder
    public MyServiceProxy(IBinder remote) {
        mRemote = remote;
    }

    @Override
    public void sayHello(String str) throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        try {
            _data.writeInterfaceToken(DESCRIPTOR);
            _data.writeString(str);
            mRemote.transact(TRANSACTION_say,_data,_reply,0);
            _reply.readException();
        }finally {
            _reply.recycle();
            _data.recycle();
        }


    }

    @Override
    public IBinder asBinder() {
        return mRemote;
    }
}
