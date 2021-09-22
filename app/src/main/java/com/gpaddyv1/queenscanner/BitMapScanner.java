package com.joshuabutton.queenscanner;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.widget.ImageView;

import com.gpaddy.hungdh.libary.NativeClass;
import com.gpaddy.hungdh.util.RectFinder;

import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Phí Văn Tuấn on 24/11/2018.
 */

public class BitMapScanner {
    private RectFinder finder;
    private NativeClass nativeClass;
    public BitMapScanner() {
        finder=new RectFinder(0.05,0.95);
        nativeClass=new NativeClass();
    }
    public Bitmap scaledBitmap(Bitmap bitmap, int width, int height) {
        Log.v("aashari-tag", "scaledBitmap");
        Log.v("aashari-tag", width + " " + height);
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, width, height), Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }
    public Map<Integer, PointF> getOutlinePoints(Bitmap tempBitmap) {
        Log.v("aashari-tag", "getOutlinePoints");
        Map<Integer, PointF> outlinePoints = new HashMap<>();
        outlinePoints.put(0, new PointF(0, 0));
        outlinePoints.put(1, new PointF(tempBitmap.getWidth(), 0));
        outlinePoints.put(2, new PointF(0, tempBitmap.getHeight()));
        outlinePoints.put(3, new PointF(tempBitmap.getWidth(), tempBitmap.getHeight()));
        return outlinePoints;
    }
    public List<PointF> getContourEdgePoints(Bitmap tempBitmap) {
        Log.v("aashari-tag", "getContourEdgePoints");

        MatOfPoint2f point2f = finder.findRectangle(RectFinder.bitmapToMat(tempBitmap));
        List<PointF> result = new ArrayList<>();
        if (point2f != null) {
            List<Point> points = Arrays.asList(point2f.toArray());
            for (int i = 0; i < points.size(); i++) {
                result.add(new PointF(((float) points.get(i).x), ((float) points.get(i).y)));
            }
        }

        return result;

    }

    public Bitmap getCroppedImage(Map<Integer, PointF> points, Bitmap selectedImageBitmap, ImageView imageView) {
        if (selectedImageBitmap!=null){
            float xRatio = (float) selectedImageBitmap.getWidth() / imageView.getWidth();
            float yRatio = (float) selectedImageBitmap.getHeight() / imageView.getHeight();

            float x1 = (points.get(0).x) * xRatio;
            float x2 = (points.get(1).x) * xRatio;
            float x3 = (points.get(2).x) * xRatio;
            float x4 = (points.get(3).x) * xRatio;
            float y1 = (points.get(0).y) * yRatio;
            float y2 = (points.get(1).y) * yRatio;
            float y3 = (points.get(2).y) * yRatio;
            float y4 = (points.get(3).y) * yRatio;

            return nativeClass.getScannedBitmap(selectedImageBitmap, x1, y1, x2, y2, x3, y3, x4, y4);
        }else {
            return null;
        }


    }
}
