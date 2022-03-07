LOCAL_PATH:= $(call my-dir)
#make jar
include $(CLEAR_VARS)
LOCAL_SRC_FILES := $(call all-subdir-java-files)
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE := samplewindow
LOCAL_PRIVATE_PLATFORM_APIS := true
include $(BUILD_JAVA_LIBRARY)