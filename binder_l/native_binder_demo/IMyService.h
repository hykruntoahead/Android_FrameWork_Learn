#include <utils/Errors.h>  // for status_t
#include <utils/KeyedVector.h>
#include <utils/RefBase.h>
#include <utils/String8.h>
#include <binder/IInterface.h>
#include <binder/Parcel.h>

namespace android
{
    class IMyService : public IInterface
    { 
    public:
        DECLARE_META_INTERFACE(MyService);//使用宏，申明MyService
        virtual void sayHello()=0;
    };

    //定义命令字段
    enum
    {
        HELLO = 1,
    };

     //申明客户端BpMyService
     class BpMyService:public BpInterface<IMyService>{
         public:
            BpMyService(const sp<IBinder>& impl);
            virtual void sayHello();
     };

     //申明服务端BnMyService
    class BnMyService:public BnInterface<IMyService>{
        public:
            virtual status_t onTransact(uint32_t code,const Parcel& data,
            Parcel* reply,uint32_t flags=0);

            virtual void sayHello();

    };  
}