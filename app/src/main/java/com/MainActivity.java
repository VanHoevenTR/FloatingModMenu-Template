package com;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Toast;

public class MainActivity extends Activity {

    public String sGameActivity = "";
    public boolean hasLaunched = false;

    static {
        System.loadLibrary("ModMenu");
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Start(this);
        //To launch game activity
        if (!hasLaunched) {
            try {
                //Start service
                MainActivity.this.startActivity(new Intent(MainActivity.this, Class.forName(MainActivity.this.sGameActivity)));
                hasLaunched = true;
            } catch (ClassNotFoundException e) {
                //Uncomment this if you are following METHOD 2 of CHANGING FILES
                //Toast.makeText(MainActivity.this, "Error. Game's main activity does not exist", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                return;
            }
        }
    }

    public static void Start(final Context context) {
        //Check if overlay permission is enabled or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
            Toast.makeText(context.getApplicationContext(), "Overlay permission is required in order to show mod menu. Restart the game after you allow permission", Toast.LENGTH_LONG).show();
            Toast.makeText(context.getApplicationContext(), "Overlay permission is required in order to show mod menu. Restart the game after you allow permission", Toast.LENGTH_LONG).show();
            context.startActivity(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION",
                    Uri.parse("package:" + context.getPackageName())));
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    System.exit(1);
                }
            }, 5000);
            return;
        } else {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    context.startService(new Intent(context, Menu.class));
                }
            }, 500);
        }
    }
}