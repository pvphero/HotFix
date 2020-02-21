package com.enjoy.hotfix;

import android.app.Application;
import android.content.Context;

import java.io.File;


public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        Hotfix.installPatch(this,new File("/sdcard/patch.jar"));
    }
}
