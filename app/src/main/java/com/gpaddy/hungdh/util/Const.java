package com.gpaddy.hungdh.util;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import static com.joshuabutton.queenscanner.PresenterScanner.FOLDER_NAME;

/**
 * Created by Phí Văn Tuấn on 30/11/2018.
 */

public class Const {
    public static final String FOLDER_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + FOLDER_NAME;
    public static final String FOLDER_DOC = FOLDER_PATH + "Document/";
    public static final String FOLDER_SIGN = "sign/";
    public static final String FOLDER_ORIGIN = "origin/";

    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5F);
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
