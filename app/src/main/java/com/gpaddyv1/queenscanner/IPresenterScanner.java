package com.joshuabutton.queenscanner;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.net.Uri;
import android.widget.ImageView;

import com.gpaddy.hungdh.libary.PolygonView;

import java.util.Map;

/**
 * Created by Phí Văn Tuấn on 24/11/2018.
 */

public interface IPresenterScanner {
    void onResult(Bitmap bitmap);

    Bitmap onResult(Uri uri);

    Map<Integer, PointF> getEdgePoint(Bitmap bitmap, PolygonView polygonView);

    void getDataFromIntent(String path);

    void onCropBitMap(Map<Integer, PointF> points, Bitmap selectedImageBitmap, ImageView imageView);

    Bitmap getBitMapSelected();

    void setBitMapSelected(Bitmap bitMapSelected);
}
