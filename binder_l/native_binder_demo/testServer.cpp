#include "IMyService.h"
using namespace android;
#include <binder/IPCThreadState.h>
#include <binder/ProcessState.h>
#include <binder/IServiceManager.h>

//将名为”service.myservice”的BnMyService服务添加到ServiceManager，并启动服务
int main(){

    //获取service manager引用
    sp<IServiceManager> sm = defaultServiceManager();
    //向service manager　注册名为"service.myservice＂服务
    sm->addService(String16("service.myservice"),new BnMyService());

    ProcessState::self()->startThreadPool();
    IPCThreadState::self()->joinThreadPool();
    return 0;
}