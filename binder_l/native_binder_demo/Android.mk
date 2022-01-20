#此变量表示源文件在开发树中的位置。
LOCAL_PATH:= $(call my-dir)

#为您清除许多 LOCAL_XXX 变量
include $(CLEAR_VARS)

#此变量包含构建系统生成模块时所用的源文件列表
LOCAL_SRC_FILES:= \
    testServer.cpp IMyService.cpp \

LOCAL_SHARED_LIBRARIES := \
	libcutils \
	libutils \
	liblog \
	libbinder \

#此变量用于存储模块名称
LOCAL_MODULE := testServer

LOCAL_CFLAGS += -DBINDER_IPC_32BIT=1

#指该模块在所有版本下都编译
LOCAL_MODULE_TAGS := optional

LOCAL_CFLAGS += -Wno-unused-variable
LOCAL_CFLAGS += -Wno-sign-compare
LOCAL_CFLAGS += -Wno-pointer-sign
LOCAL_CFLAGS += -Wno-unused-function
LOCAL_CFLAGS += -Wno-unused-parameter
LOCAL_CFLAGS += -Wno-unused-variable
LOCAL_CFLAGS += -Wno-implicit-function-declaration
LOCAL_CFLAGS += -Wno-unused-result

#构建目标可执行文件
include $(BUILD_EXECUTABLE)


include $(CLEAR_VARS)
LOCAL_SRC_FILES:= \
    testClient.cpp IMyService.cpp \

LOCAL_SHARED_LIBRARIES := \
	libcutils \
	libutils \
	liblog \
	libbinder \

#此变量用于存储模块名称
LOCAL_MODULE := testClient

LOCAL_CFLAGS += -DBINDER_IPC_32BIT=1

#指该模块在所有版本下都编译
LOCAL_MODULE_TAGS := optional

LOCAL_CFLAGS += -Wno-unused-variable
LOCAL_CFLAGS += -Wno-sign-compare
LOCAL_CFLAGS += -Wno-pointer-sign
LOCAL_CFLAGS += -Wno-unused-function
LOCAL_CFLAGS += -Wno-unused-parameter
LOCAL_CFLAGS += -Wno-unused-variable
LOCAL_CFLAGS += -Wno-implicit-function-declaration
LOCAL_CFLAGS += -Wno-unused-result

#构建目标可执行文件
include $(BUILD_EXECUTABLE)
