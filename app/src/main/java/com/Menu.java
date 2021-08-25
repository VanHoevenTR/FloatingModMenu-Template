package com;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.IBinder;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class Menu extends Service {
    public View mFloatingView;
    private LinearLayout modBody;
    public WindowManager windowManager;

    //Ignore the reds if you are registering native calls via c++
    private native void changeMod(int featNum);

    private native void changeMod(int featNum, boolean b);

    private native void changeMod(int featNum, int i);

    private native void changeMod(int featNum, float f);

    private native void changeMod(int featNum, String f);

    private native String[] getListFT();

    private native String getString(String str);

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        InstallMenu();
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            public void run() {
                Menu.this.onCheckGameRunning();
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void InstallMenu() {
        int i = VERSION.SDK_INT >= 26 ? 2038 : 2002;
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setLayoutParams(new LayoutParams(-2, -2));
        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new RelativeLayout.LayoutParams(convertDipToPixels(50.0f), convertDipToPixels(50.0f)));
        InputStream open = null;
        try {
            open = getAssets().open("Icon.png");
            imageView.setImageDrawable(Drawable.createFromStream(open, null));
            open.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        relativeLayout.addView(imageView);
        this.mFloatingView = relativeLayout;
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        linearLayout.setBackgroundColor(Style.MENU_BG);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        textView.setGravity(1);
        textView.setText(Html.fromHtml(getString("menu_text_title")));
        textView.setTextSize(20.0f);

        WebView webView = new WebView(this);
        webView.setLayoutParams(new LinearLayout.LayoutParams(-2, convertDipToPixels(25.0f)));
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) webView.getLayoutParams();
        layoutParams.gravity = 17;
        layoutParams.bottomMargin = 10;
        webView.setBackgroundColor(0); //Transparent
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.loadData(getString("menu_text_scrolling"), "text/html", "utf-8");

        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(-1, convertDipToPixels(250.0f)));
        scrollView.setScrollBarSize(convertDipToPixels(5.0f));

        this.modBody = new LinearLayout(this);
        this.modBody.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        this.modBody.setOrientation(LinearLayout.VERTICAL);
        //this.modBody.setBackgroundColor(Style.BOX_COLOR);

        scrollView.addView(this.modBody);
        RelativeLayout relativeLayout2 = new RelativeLayout(this);
        relativeLayout2.setLayoutParams(new RelativeLayout.LayoutParams(-2, -1));
        relativeLayout2.setPadding(10, 10, 10, 10);
        relativeLayout2.setVerticalGravity(16);

        Button button = new Button(this);
        button.setBackgroundColor(Style.BTN_COLOR_KILL);
        button.setText("HIDE MENU");
        button.setTextColor(Style.TXT_COLOR);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams2.addRule(11);
        button.setLayoutParams(layoutParams2);
        Button button2 = new Button(this);
        button2.setBackgroundColor(Style.BTN_COLOR_HIDE);
        button2.setText("KILL MENU");
        button2.setTextColor(Style.TXT_COLOR);

        relativeLayout2.addView(button);
        relativeLayout2.addView(button2);
        linearLayout.addView(textView);
        linearLayout.addView(webView);
        linearLayout.addView(scrollView);
        linearLayout.addView(relativeLayout2);
        frameLayout.addView(linearLayout);

        final AlertDialog create = new Builder(this, 2).create();
        create.getWindow().setType(i);
        create.setView(frameLayout);

        final WindowManager.LayoutParams layoutParams3 = new WindowManager.LayoutParams(-2, -2, i, 8, -3);
        layoutParams3.gravity = 51;
        layoutParams3.x = 0;
        layoutParams3.y = 100;

        this.windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        this.windowManager.addView(this.mFloatingView, layoutParams3);
        this.mFloatingView.setOnTouchListener(new OnTouchListener() {
            private float initialTouchX;
            private float initialTouchY;
            private int initialX;
            private int initialY;

            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case 0:
                        this.initialX = layoutParams3.x;
                        this.initialY = layoutParams3.y;
                        this.initialTouchX = motionEvent.getRawX();
                        this.initialTouchY = motionEvent.getRawY();
                        return true;
                    case 1:
                        create.show();
                        return true;
                    case 2:
                        float round = (float) Math.round(motionEvent.getRawX() - this.initialTouchX);
                        float round2 = (float) Math.round(motionEvent.getRawY() - this.initialTouchY);
                        layoutParams3.x = this.initialX + ((int) round);
                        layoutParams3.y = this.initialY + ((int) round2);
                        Menu.this.windowManager.updateViewLayout(Menu.this.mFloatingView, layoutParams3);
                        return true;
                    default:
                        return false;
                }
            }
        });
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                create.hide();
            }
        });
        button2.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                create.hide();
                Menu.this.stopSelf();
            }
        });

        String[] listFT = getListFT();
        for (int i2 = 0; i2 < listFT.length; i2++) {
            final int finalI = i2;

            String[] strSplit = listFT[i2].split("_");
            switch (strSplit[0]) {
                case "Switch":
                    addSwitch(finalI, strSplit[1]);
                    break;
                case "Button":
                    addButton(finalI, strSplit[1]);
                    break;
                case "SeekBar":
                    addSeekBar(finalI, strSplit[1], Integer.parseInt(strSplit[2]), Integer.parseInt(strSplit[3]));
                    break;
                case "TextField":
                    if (strSplit.length == 3)
                        addTextField(finalI, strSplit[1], strSplit[2], false);
                    else
                        addTextField(finalI, strSplit[1], "", false);
                    break;
                case "TextFieldNum":
                    if (strSplit.length == 3)
                        addTextField(finalI, strSplit[1], strSplit[2], true);
                    else
                        addTextField(finalI, strSplit[1], "", true);
                    break;
            }
        }
    }

    private void addSwitch(final int featNum, String featName) {
        Switch swi = new Switch(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
        layoutParams.setMargins(0, 2, 0, 0);
        swi.setLayoutParams(layoutParams);
        swi.setBackgroundColor(Style.BOX_COLOR);
        swi.setPadding(10, 5, 10, 5);
        swi.setText(Html.fromHtml("<font face='fantasy' color='yellow'>" + featName + "</font>"));
        swi.setTextSize(Style.TXT_SIZE);
        swi.setTypeface(swi.getTypeface(), Typeface.BOLD);
        swi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean bool) {
                changeMod(featNum, bool);
            }
        });
        this.modBody.addView(swi);
    }

    private void addButton(final int featNum, String featName) {
        final Button button = new Button(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParams.setMargins(0, 2, 0, 0);
        button.setLayoutParams(layoutParams);
        button.setBackgroundColor(Style.BOX_COLOR);
        button.setAllCaps(false); //Disable caps to support html
        button.setText(Html.fromHtml("<font face='fantasy' color='yellow'>" + featName + "</font>"));
        button.setTextSize(Style.TXT_SIZE);
        button.setTypeface(button.getTypeface(), Typeface.BOLD);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeMod(featNum);
            }
        });
        this.modBody.addView(button);
    }

    private void addSeekBar(final int featNum, final String featName, final int min, int max) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParams.setMargins(0, 2, 0, 0);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setPadding(10, 5, 10, 5);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setBackgroundColor(Style.BOX_COLOR);

        final TextView textView = new TextView(this);
        textView.setText(Html.fromHtml("<font face='fantasy' color='yellow'>" + featName + ":</font> <font face='fantasy' color='green'>" + min + "</font>"));
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setTextSize(Style.TXT_SIZE);

        SeekBar seekBar = new SeekBar(this);
        seekBar.setPadding(25, 10, 35, 10);
        seekBar.setMax(max);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            seekBar.setMin(min); //setMin for Oreo and above
        seekBar.setProgress(min);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                changeMod(featNum, i);
                //if progress is greater than minimum, don't go below. Else, set progress
                seekBar.setProgress(i < min ? min : i);
                textView.setText(Html.fromHtml("<font face='fantasy' color='yellow'>" + featName + ":</font> <font face='fantasy' font color='green'>" + (i < min ? min : i) + "</font>"));
            }
        });
        linearLayout.addView(textView);
        linearLayout.addView(seekBar);
        this.modBody.addView(linearLayout);
    }

    private void addTextField(final int featNum, String featName, String hint, boolean useDecimal) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParams.setMargins(0, 2, 0, 0);

        LinearLayout txtViewLayout = new LinearLayout(this);
        txtViewLayout.setOrientation(LinearLayout.VERTICAL);
        txtViewLayout.setPadding(10, 5, 10, 5);
        txtViewLayout.setBackgroundColor(Style.BOX_COLOR);
        txtViewLayout.setLayoutParams(layoutParams);

        LinearLayout editTextLayout = new LinearLayout(this);
        editTextLayout.setOrientation(LinearLayout.VERTICAL);

        final TextView textView = new TextView(this);
        textView.setText(Html.fromHtml("<font face='fantasy' color='yellow'>" + featName + ":</font>"));
        textView.setTextSize(Style.TXT_SIZE);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);

        RelativeLayout relativeLayout2 = new RelativeLayout(this);
        relativeLayout2.setPadding(10, 5, 5, 10);
        relativeLayout2.setVerticalGravity(16);

        final EditText editText = new EditText(this);
        editText.setHint(hint);
        editText.setMaxLines(1);
        editText.setWidth(convertDipToPixels(300));
        //editText.setEms(13);
       // editText.setEms(13);
        editText.setTextColor(Style.TXT_COLOR);
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        //    editText.setBackgroundTintList(ColorStateList.valueOf(Style.TXT_COLOR_WHITE));
        editText.setHintTextColor(Style.TXT_COLOR_TIP);
        if (useDecimal) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(30);
            editText.setFilters(FilterArray);
        }

        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        Button button2 = new Button(this);
        button2.setLayoutParams(layoutParams2);
        button2.setBackgroundColor(Style.BTN_COLOR_APPLY);
        button2.setText("Apply");
        button2.setTextColor(Style.TXT_COLOR);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (useDecimal)
                    changeMod(featNum, Float.parseFloat(editText.getText().toString()));
                else
                    changeMod(featNum, editText.getText().toString());
            }
        });
        editTextLayout.addView(editText);
        relativeLayout2.addView(editTextLayout);
        relativeLayout2.addView(button2);
        txtViewLayout.addView(textView);
        txtViewLayout.addView(relativeLayout2);
        this.modBody.addView(txtViewLayout);
    }

    private int convertDipToPixels(float f) {
        return (int) ((f * getResources().getDisplayMetrics().density) + 0.5f);
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.mFloatingView != null) {
            this.windowManager.removeView(this.mFloatingView);
        }
    }

    private boolean isGameRunning() {
        RunningAppProcessInfo runningAppProcessInfo = new RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(runningAppProcessInfo);
        return runningAppProcessInfo.importance != 100;
    }

    public void onCheckGameRunning() {
        if (this.mFloatingView == null) {
            return;
        }
        if (isGameRunning()) {
            this.mFloatingView.setVisibility(View.INVISIBLE);
        } else {
            this.mFloatingView.setVisibility(View.VISIBLE);
        }
    }
}