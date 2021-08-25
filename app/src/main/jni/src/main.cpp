#include <pthread.h>
#include "src/KittyMemory/MemoryPatch.h"
#include "src/Includes/Logger.h"
#include "Includes/obfuscate.h"
#include "main.h"

#define targetLibName OBFUSCATE("libFileA.so")

#include <Macros.h>

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
int damage;

void (*old_FunctionExample)(void *instance);
void FunctionExample(void *instance) {

    return old_FunctionExample(instance);
}

// we will run our patches in a new thread so "sleep" doesn't block process main thread
void *config_thread(void *) {
    LOGD("I have been loaded...");

#if defined(__aarch64__)

#else if defined(__arm__)

    // loop until our target library is found
    ProcMap il2cppMap;
    do {
        il2cppMap = KittyMemory::getLibraryMap("libil2cpp.so");
        sleep(1);
    } while (!il2cppMap.isValid());

    // Hook example. Comment out if you don't use hook
    // Strings in macros are automatically obfuscated. No need to obfuscate!
    HOOK("str", FunctionExample, old_FunctionExample);
    HOOK_LIB("libFileB.so", "0x123456", FunctionExample, old_FunctionExample);
    HOOK_NO_ORIG("0x123456", FunctionExample);
    HOOK_LIB_NO_ORIG("libFileC.so", "0x123456", FunctionExample);
    HOOKSYM("__SymbolNameExample", FunctionExample, old_FunctionExample);
    HOOKSYM_LIB("libFileB.so", "__SymbolNameExample", FunctionExample, old_FunctionExample);
    HOOKSYM_NO_ORIG("__SymbolNameExample", FunctionExample);
    HOOKSYM_LIB_NO_ORIG("libFileB.so", "__SymbolNameExample", FunctionExample);

    // Patching offsets directly. Strings are automatically obfuscated too!
    PATCHOFFSET("0x20D3A8", "00 00 A0 E3 1E FF 2F E1");
    PATCHOFFSET_LIB("libFileB.so", "0x20D3A8", "00 00 A0 E3 1E FF 2F E1");
#endif

    return NULL;
}

void changes(jint featNum, bool boolean, int num, float fnum, const char *str) {
    //http://www.cplusplus.com/reference/cstdio/printf/
    LOGD(OBFUSCATE("Feature number: %d | Booolean: %d | Number: %d | Float: %f"), featNum, boolean,
         num, fnum);
    switch (featNum) {
        case 0:
            feature1 = boolean;
            if (feature1) {
                my_cool_Patches.canShowInMinimap.Modify();
            } else {
                my_cool_Patches.canShowInMinimap.Restore();
            }
            break;
        case 1:
            feature2 = boolean;
            if (feature2) {
                //my_cool_Patches.OneHit.Modify();
            } else {
                //my_cool_Patches.OneHit.Restore()
            }
            break;
        case 2:
            LOGD(OBFUSCATE("Btn called"));
            break;
        case 3:
            damage = num;
            break;
        case 4:
            LOGD(OBFUSCATE("Text input: %s"), str);
            break;
        case 5:
            LOGD(OBFUSCATE("Num input: %d"), (int)fnum); //Convert to int
            LOGD(OBFUSCATE("Float num input: %f"), fnum);
            break;
    }
}

jobjectArray getListFT(JNIEnv *env, jobject jobj) {
    jobjectArray ret;
    int i;

    char *features[] = {
            "Switch_God Mode",
            "Switch_Map hack",
            "Button_Add money",
            "SeekBar_DMG Multiply_1_100",
            "TextField_Set text_Tip: Write text here",
            "TextFieldNum_Set number_Tip: Write number here",
    };

    int Total_Feature = (sizeof features / sizeof features[0]);
    ret = (jobjectArray) env->NewObjectArray(Total_Feature,
                                             env->FindClass("java/lang/String"),
                                             env->NewStringUTF(""));

    for (i = 0; i < Total_Feature; i++) {
        env->SetObjectArrayElement(
                ret, i, env->NewStringUTF(features[i]));
    }
    return (ret);
}

void changeMod(JNIEnv *env, jobject thisObj, jint featNum) {
    changes(featNum, false, 0, 0, "");
}

void changeModBool(JNIEnv *env, jobject thisObj, jint featNum, jboolean boolean) {
    changes(featNum, boolean, 0, 0, "");
}

void changeModInt(JNIEnv *env, jobject thisObj, jint featNum, jint num) {
    changes(featNum, false, (int) featNum, 0, "");
}

void changeModFloat(JNIEnv *env, jobject thisObj, jint featNum, jfloat fnum) {
    changes(featNum, false, 0, (float) fnum, "");
}

void changeModString(JNIEnv *env, jobject thisObj, jint featNum, jstring str) {
    changes(featNum, false, 0, 0, str != NULL ? env->GetStringUTFChars(str, 0) : "");
}

jstring getString(JNIEnv *env, jobject thiz, jstring jstr) {
    const char *str = env->GetStringUTFChars(jstr, 0);

    if (str == std::string(OBFUSCATE("menu_text_title")))
        return env->NewStringUTF(
                OBFUSCATE("<font face='fantasy'><b><font color='#57c4aa'>MENU MOD</b></font>"));
    else if (str == std::string(OBFUSCATE("menu_text_scrolling")))
        return env->NewStringUTF(
                OBFUSCATE(
                        "<html>"
                        "<head>"
                        "<style>"
                        "body{"
                        "color: #f3c930;"
                        "font-weight:bold;"
                        "font-family:Courier, monospace;"
                        "}"
                        "</style>"
                        "</head>"
                        "<body>"
                        "<marquee class=\"GeneratedMarquee\" direction=\"left\" scrollamount=\"4\" behavior=\"scroll\">"
                        "[Platinmods.com] If Game Update Visit Platinmods.com"
                        "</marquee>"
                        "</body>"
                        "</html>"));

    return NULL;
}

__attribute__((constructor))
void launcher() {
    pthread_t ptid;
    pthread_create(&ptid, NULL, config_thread, NULL);
}

extern "C"
JNIEXPORT jint
JNI_OnLoad(JavaVM
           *vm,
           void *reserved
) {
    JNIEnv *env;
    vm->GetEnv((void **) &env, JNI_VERSION_1_6);

//Your menu class
    jclass c = env->FindClass("com/Menu");
    if (c == nullptr)
        return
                JNI_ERR;

// Register your class native methods. You need some basic smali code to know the signature.
// If you are lazy, just decompile the app and see the signature
// See more: https://developer.android.com/training/articles/perf-jni
    static const JNINativeMethod methods[] = {
            {"changeMod", "(I)V",                                   reinterpret_cast<void *>(changeMod)},
            {"changeMod", "(IZ)V",                                  reinterpret_cast<void *>(changeModBool)},
            {"changeMod", "(II)V",                                  reinterpret_cast<void *>(changeModInt)},
            {"changeMod", "(IF)V",                                  reinterpret_cast<void *>(changeModFloat)},
            {"changeMod", "(ILjava/lang/String;)V",                 reinterpret_cast<void *>(changeModString)},
            {"getListFT", "()[Ljava/lang/String;",                  reinterpret_cast<void *>(getListFT)},
            {"getString", "(Ljava/lang/String;)Ljava/lang/String;", reinterpret_cast<void *>(getString)},
    };

    int rc = env->RegisterNatives(c, methods, sizeof(methods) / sizeof(JNINativeMethod));
    if (rc != JNI_OK)
        return
                rc;

    return
            JNI_VERSION_1_6;
}