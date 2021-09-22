package com;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;

import ly.img.android.ImgLySdk;

/**
 * Created by HUNGDH on 12/27/2016.
 */

public class MainApplication extends Application {



    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ImgLySdk.init(this);
    }
}
