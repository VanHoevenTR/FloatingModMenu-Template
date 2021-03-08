package com.van.modmenu;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

public class MainActivity extends Activity {

    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2002;
    public String sGameActivity = "";

    static {
        System.loadLibrary("ModMenu");
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this)) {
            try {
                startActivity(new Intent(this, Class.forName(this.sGameActivity)));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            startService(new Intent(this, Menu.class));
        } else
            startActivityForResult(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + getPackageName())), CODE_DRAW_OVER_OTHER_APP_PERMISSION);
    }
}
