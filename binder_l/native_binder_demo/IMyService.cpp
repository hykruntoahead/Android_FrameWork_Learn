#include "IMyService.h"


namespace android
{
    //使用宏,定义MyService
    IMPLEMENT_META_INTERFACE(MyService, "android.demo.IMyService");

    //客户端
    BpMyService::BpMyService(const sp<IBinder>& impl):BpInterface<IMyService>(impl){}

    //客户端sayHello方法
    void  BpMyService::sayHello(){
        printf("BpMyService::sayHello\n");
        Parcel data,reply;
        data.writeInterfaceToken(IMyService::getInterfaceDescriptor());
        remote()->transact(HELLO,data,&reply);
        printf("get num from BnMyService: %d\n", reply.readInt32());
    }

    //服务端,接受远程消息,处理onTransact方法
    status_t BnMyService::onTransact(uint32_t code,const Parcel& data,
            Parcel* reply,uint32_t flags){
            switch(code){
                case HELLO:{
                    printf("BnMyService:: got the client hello\n");
                    CHECK_INTERFACE(IMyService, data, reply);
                    sayHello();
                    reply->writeInt32(2020);
                    return NO_ERROR;
                    }
                    break;
                default:
                    break;

            }
            return NO_ERROR;
        }
    //实现服务端sayHello方法
    void BnMyService::sayHello(){
        printf("BnMyService::sayHello\n");
    }    
}