LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

# platform information are ignored because this can be assigned
# from gradle project, if not assigned or not using gradle, add
# these lines
 APP_PLATFORM := android-15
 TARGET_PLATFORM := android-25

# This can be assigned from the gradle ndk moduleName property.
# LOCAL_MODULE    := bspatch
LOCAL_SRC_FILES := bspatch.c

include $(PREBUILT_SHARED_LIBRARY)
include $(CLEAR_VARS)

LOCAL_LDLIBS    := -llog

include $(BUILD_SHARED_LIBRARY)