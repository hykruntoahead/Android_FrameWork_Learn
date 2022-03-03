LOCAL_PATH:= $(call my-dir)
#make jar
include $(CLEAR_VARS)
LOCAL_SRC_FILES := $(call all-subdir-java-files)
LOCAL_SDK_VERSION := current
LOCAL_PRIVATE_PLATFORM_APIS := true
LOCAL_MODULE := testBinder
include $(BUILD_STATIC_JAVA_LIBRARY)

#include $(BUILD_STATIC_JAVA_LIBRARY)
