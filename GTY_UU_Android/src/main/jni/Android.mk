LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE    :=token
LOCAL_SRC_FILES :=getToken.c md5c.c
#liblog.so libGLESv2.so
LOCAL_LDLIBS += -llog
include $(BUILD_SHARED_LIBRARY)
