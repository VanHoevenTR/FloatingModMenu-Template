LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := ModMenu

LOCAL_C_INCLUDES        += $(LOCAL_PATH)/src/Includes

LOCAL_SRC_FILES := src/main.cpp \
src/KittyMemory/KittyMemory.cpp \
src/KittyMemory/KittyUtils.cpp \
src/KittyMemory/MemoryBackup.cpp \
src/KittyMemory/MemoryPatch.cpp \
src/Substrate/hde64.c \
src/Substrate/SymbolFinder.cpp \
src/Substrate/SubstrateDebug.cpp \
src/Substrate/SubstrateHook.cpp \
src/Substrate/SubstratePosixMemory.cpp \

# Code optimization by LGL Team
# -std=c++17 is required to support AIDE app with NDK
LOCAL_CFLAGS := -Wno-error=format-security -fvisibility=hidden -ffunction-sections -fdata-sections -w
LOCAL_CFLAGS += -fno-rtti -fno-exceptions -fpermissive
LOCAL_CPPFLAGS := -Wno-error=format-security -fvisibility=hidden -ffunction-sections -fdata-sections -w -Werror -s -std=c++17
LOCAL_CPPFLAGS += -Wno-error=c++11-narrowing -fms-extensions -fno-rtti -fno-exceptions -fpermissive
LOCAL_LDFLAGS += -Wl,--gc-sections,--strip-all, -llog
LOCAL_ARM_MODE := arm

LOCAL_LDLIBS := -llog -landroid -lGLESv2

include $(BUILD_SHARED_LIBRARY)