LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := ModMenu

LOCAL_SRC_FILES := src/main.cpp \
src/KittyMemory/KittyMemory.cpp \
src/KittyMemory/MemoryPatch.cpp


include $(BUILD_SHARED_LIBRARY)