#include <pthread.h>
#include "KittyMemory/MemoryPatch.h"
#include "KittyMemory/Logger.h"
#include "main.h"

// fancy struct for patches
struct My_Patches {
    // let's assume we have patches for these functions for whatever game
    // like show in miniMap boolean function
    MemoryPatch canShowInMinimap;
    // etc...
} my_cool_Patches;

bool feature1 = false;
bool feature2 = false;
bool feature3 = false;

// we will run our patches in a new thread so "sleep" doesn't block process main thread
void *config_thread(void *) {
    LOGD("I have been loaded...");

    // loop until our target library is found
    ProcMap il2cppMap;
    do {
        il2cppMap = KittyMemory::getLibraryMap("libil2cpp.so");
        sleep(1);
    } while (!il2cppMap.isValid());

    // now here we do our stuff
    // let's say our patches are meant for an arm library

    // http://shell-storm.org/online/Online-Assembler-and-Disassembler/
    /*
    * mov r0, #1
    * bx lr
    */
    // address = 0x4074D50
    // bytes len = 8
    my_cool_Patches.canShowInMinimap = MemoryPatch("libil2cpp.so", 0x4074D50,
                                                   "\x01\x00\xA0\xE3\x1E\xFF\x2F\xE1", 8);

    /*
    * my_cool_Patches.OneHit = MemoryPatch("libil2cpp.so", 0x4074D50,
    *                                      "\x01\x00\xA0\xE3\x1E\xFF\x2F\xE1", 8);
    */

    /*
    * my_cool_Patches.GodMode = MemoryPatch("libil2cpp.so", 0x4074D50,
    *                                      "\x01\x00\xA0\xE3\x1E\xFF\x2F\xE1", 8);
    */
    return NULL;
}

extern "C" {
JNIEXPORT jobjectArray JNICALL Java_com_van_modmenu_Menu_getListFT(JNIEnv *env, jobject jobj) {
    jobjectArray ret;
    int i;
    int Total_Feature = 1;
    char *features[] = {"God Mode"};
    /*
    * int Total_Feature = 2;
    * char *features[]= {"Mini Map Hack", "One HIT"};
    */

    /*
    * int Total_Feature = 3;
    * char *features[]= {"Mini Map Hack", "One HIT", "God Mode"};
    */

    ret = (jobjectArray) env->NewObjectArray(Total_Feature,
                                             env->FindClass("java/lang/String"),
                                             env->NewStringUTF(""));

    for (i = 0; i < Total_Feature; i++) {
        env->SetObjectArrayElement(
                ret, i, env->NewStringUTF(features[i]));
    }
    return (ret);
}

JNIEXPORT void JNICALL
Java_com_van_modmenu_Menu_changeToggle(JNIEnv *env, jobject thisObj, jint number) {
    int i = (int) number;
    switch (i) {
        case 0:
            feature1 = !feature1;
            if (feature1) {
                my_cool_Patches.canShowInMinimap.Modify();
            } else {
                my_cool_Patches.canShowInMinimap.Restore();
            }
            break;
        case 1:
            feature2 = !feature2;
            if (feature2) {
                //my_cool_Patches.OneHit.Modify();
            } else {
                //my_cool_Patches.OneHit.Restore()
            }
            break;
        case 2:
            feature3 = !feature3;
            if (feature3) {
                //my_cool_Patches.GodMode.Modify();
            } else {
                //my_cool_Patches.GodMode.Restore()
            }
            break;
    }
    return;
}
}

__attribute__((constructor))
void van_hoeven() {
    pthread_t ptid;
    pthread_create(&ptid, NULL, config_thread, NULL);
}
