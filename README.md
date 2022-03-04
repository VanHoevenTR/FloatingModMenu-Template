## **This project has been discontined. Use LGL mod menu for up-to-date project https://github.com/LGLTeam/Android-Mod-Menu**

# FloatingModMenu

![Screenshot](/images/ImageScr.png)

Simple floating mod menu to il2cpp and other native Android games.

Arm64hook is not tested, i don't have arm64 phone

**This project is for professional modders and programmers only. Do not use if you don't know how to
use it**

# Features

Some codes are adopted from LGL Team

* Menu types
    * Switch
    * Seekbar
    * Button

* Kitty Memory
* Substrate (ARMv7)
* And64InlineHook (ARM64)
* AY obfuscator for C++ string
* Package name just called `com` to avoid detection
* Macros for hooks and patches
* Hide JNI calls

# Usage

### Menu features:

### Menu layout:

You can customize the menu layout in `Menu.java` and `Style.java`.

Too much to explain here. You can learn and figure out yourself

### Hooking and Patching macros:

Strings in macros are automatically obfuscated. No need to obfuscate manually!

**Hooking**

```cpp
HOOK("str", FunctionExample, old_FunctionExample);
HOOK_LIB("libFileB.so", "0x123456", FunctionExample, old_FunctionExample);
HOOK_NO_ORIG("0x123456", FunctionExample);
HOOK_LIB_NO_ORIG("libFileC.so", "0x123456", FunctionExample);
HOOKSYM("__SymbolNameExample", FunctionExample, old_FunctionExample);
HOOKSYM_LIB("libFileB.so", "__SymbolNameExample", FunctionExample, old_FunctionExample);
HOOKSYM_NO_ORIG("__SymbolNameExample", FunctionExample);
HOOKSYM_LIB_NO_ORIG("libFileB.so", "__SymbolNameExample", FunctionExample);
```

**Patching**

```cpp
PATCHOFFSET("0x20D3A8", "00 00 A0 E3 1E FF 2F E1");
PATCHOFFSET_LIB("libFileB.so", "0x20D3A8", "00 00 A0 E3 1E FF 2F E1");
```

**JNI signatures**

These info can be obtained from smali file

- `V` = Void
- `Z` = Boolean
- `F` = Float
- `I` = Integer
- `Ljava/lang/String;` = String
  
- `(I)V` = `void methodExample(int i)`
- `(IZ)V` = `void methodExample(int i, boolean b)`
- `(II)V` = `void methodExample(int i, int i2)`
- `(IF)V` = `void methodExample(int i, float f)`
- `(ILjava/lang/String;)V` = `void methodExample(int i, String s)`
- `()[Ljava/lang/String;` = `String[] strArrayExample()`
- `(Ljava/lang/String;)Ljava/lang/String;` = `String stringExample(String s)`

# How to inject menu to game

1. Compile this project: Build > Build bundle(s) / APK(s) > Build APK(s). app-debug.apk is created
   on \app\build\outputs\apk\debug
2. Decompile app-debug.apk
3. Decompile the game using apktool
4. Copy the needed assets from our app to the game
5. Copy the lib to the game, our armeabi-v7a lib to armeabi-v7a folder and/or arm64-v8a lib to
   arm64-v8a folder. Work only on armeabi-v7a as possible and delete arm64-v8a folder If the game
   has both armeabi-v7a and arm64-v8a available, to save your time.
6. Add `com` folder to the game. If the game has multidex, it's best you add to the last
   smali_classes to avoid compile error. it doesn't really matter what smali_classes you are adding
   too. Don't worry if `com` exists
7. Add overlay permission to AndroidManifest.xml. We only need one permission

```xml

<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
```

8. Add service to AndroidManifest.xml

```xml

<service android:name="com.Menu" android:enabled="true" android:exported="false"
    android:stopWithTask="true" />
```

9. Determine the launch activity by looking at the application’s manifest. The launch activity will
   always have the following MAIN and LAUNCHER intents listed.

```xml

<activity android:name="com.example.MainActivity">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

So in the target game, it looks like this. We use Among Us as an example:

```xml

<activity
    android:configChanges="density|fontScale|keyboard|keyboardHidden|layoutDirection|locale|mcc|mnc|navigation|orientation|screenLayout|screenSize|smallestScreenSize|touchscreen|uiMode"
    android:hardwareAccelerated="false" android:label="@string/app_name"
    android:launchMode="singleTask" android:name="com.innersloth.spacemafia.EosUnityPlayerActivity"
    android:screenOrientation="sensorLandscape">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
    <meta-data android:name="unityplayer.UnityActivity" android:value="true" />
    <intent-filter>
        <action android:name="com.discord.intent.action.SDK" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:host="innersloth.com" android:pathPattern=".*" android:scheme="https" />
    </intent-filter>
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="amongus" />
    </intent-filter>
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:host="innersloth.com" android:pathPattern=".*" android:scheme="https" />
    </intent-filter>
</activity>
```

As you can see, the launch activity is `com.innersloth.spacemafia.EosUnityPlayerActivity`. It works
like a path, like `com/innersloth/spacemafia/EosUnityPlayerActivity.smali`

10. Navigate though the path `smali/com/innersloth/spacemafia/` and open the
    file `EosUnityPlayerActivity.smali`. Some games comes with multidex which means it will come
    with multiple smali folders, search though them and find the launch activity file

11. Find the onCreate function and place the static call below `.locals`

```
invoke-static {p0}, Lcom/MainActivity;->Start(Landroid/content/Context;)V
```

Like this

```
# virtual methods
    .method protected onCreate(Landroid/os/Bundle;)V
    .locals 1
    
    invoke-static {p0}, Lcom/MainActivity;->Start(Landroid/content/Context;)V
```

Now you can compile the game with apktool, sign and zipalign it and install it on your device or
emulator.

# Credits

- VanHoevenTR (Redesigned layout)
- MrIkso https://github.com/MrIkso/FloatingModMenu (His first mod menu)
- MJx0 https://github.com/MJx0/KittyMemory
- LGLTeam https://github.com/LGLTeam/Android-Mod-Menu
- Rprop https://github.com/Rprop/And64InlineHook
- Obfuscate https://github.com/adamyaxley/Obfuscate

Note: This is not Octowolve's menu, this is mine and sources is based of MrIkso, I posted
on [Platinmods](https://platinmods.com/threads/template-menu-free-for-mod-menu-il2cpp-and-other-native-games.67429/)
since 2019 _before_ he uploaded in 2020, but he declined, refused to credit and claims his own 100%,
but there is nothing I can do now. If you believe I took his work, that's fine, but the fact i didn't.
