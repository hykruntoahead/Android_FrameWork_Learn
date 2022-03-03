package com.ykhe.binder;

import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;

/**
 * author: ykhe
 * date: 22-2-11
 * email: ykhe@grandstream.cn
 * description:
 */
public class ClientDemo {
    public static void main(String[] args) throws RemoteException {
        System.out.println("Client Start");
        //获取名为"MyService"的服务
        IBinder binder = ServiceManager.getService("MyService");
        IMyService myService = new MyServiceProxy(binder);//创建proxy对象
        myService.sayHello("binder binder get it ");
        System.out.println("Client End");
    }
}
