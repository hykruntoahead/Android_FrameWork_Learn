#include "IMyService.h"
using namespace android;
#include <binder/IPCThreadState.h>
#include <binder/ProcessState.h>
#include <binder/IServiceManager.h>

int main(){
    //获取service manager 引用
    sp<IServiceManager> sm = defaultServiceManager();
    //获取名为"service.myservice"的binder ref
    sp<IBinder> binder = sm->getService(String16("service.myservice"));
    //将binder对象转换为强引用类型的IMyService
    sp<IMyService> cs = interface_cast <IMyService> (binder);
    //利用binder引用调用远程sayHello()方法
    cs->sayHello();
    return 0;
}