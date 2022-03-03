package com.ykhe.binder;

import android.os.Looper;
import android.os.Process;
import android.os.ServiceManager;

public class ServerDemo{
    public static void main(String[] args) {
        System.out.println("MyService Start");

        //准备Looper循环执行
        Looper.prepareMainLooper();
        //设置前台优先级
        Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND);
        //注册服务
        ServiceManager.addService("MyService",new MyService());
        //loop
        Looper.loop();
    }
}