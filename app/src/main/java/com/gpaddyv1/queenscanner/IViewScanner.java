package com.joshuabutton.queenscanner;

import android.graphics.Bitmap;

/**
 * Created by Phí Văn Tuấn on 24/11/2018.
 */

public interface IViewScanner {

    void onResult(Bitmap bitmap);

    void chooseImage();

    void editImage(String path);
}
