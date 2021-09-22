package com.joshuabutton.queenscanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.gpaddy.hungdh.libary.PolygonView;
import com.gpaddy.hungdh.util.ImageUtils;
import com.gpaddy.hungdh.util.PathUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by Phí Văn Tuấn on 24/11/2018.
 */

public class PresenterScanner implements IPresenterScanner {
    public static String FOLDER_NAME = "/QueenScanner/";
    public static String FOLDER_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + FOLDER_NAME;
    public static String FILE_NAME = FOLDER_PATH + "sign.png";
    private BitMapScanner scanner;
    private IViewScanner iViewScanner;
    private Context context;
    private Bitmap bitmap;
    public static Bitmap bitmapSelected;

    public PresenterScanner(IViewScanner iViewScanner, Context context) {
        this.iViewScanner = iViewScanner;
        this.context = context;
        scanner = new BitMapScanner();
    }

    @Override
    public void onResult(Bitmap bitmap) {
        iViewScanner.onResult(bitmap);
    }

    @Override
    public Bitmap onResult(Uri uri) {
        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream);
            String path = PathUtil.getPath(context, uri);
            if (!TextUtils.isEmpty(path)) {
                selectedBitmap = ImageUtils.modifyOrientation(selectedBitmap, path);
            }
            bitmap = selectedBitmap;
            return selectedBitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<Integer, PointF> getEdgePoint(Bitmap bitmap, PolygonView polygonView) {
        List<PointF> pointFs = scanner.getContourEdgePoints(bitmap);
        Map<Integer, PointF> orderedPoints = polygonView.getOrderedPoints(pointFs);
        if (!polygonView.isValidShape(orderedPoints)) {
            orderedPoints = scanner.getOutlinePoints(bitmap);
            Log.e("valid", "no");
        }
        return orderedPoints;
    }

    @Override
    public void getDataFromIntent(String path) {
        if (path.equals("")) {
            iViewScanner.chooseImage();
        } else {
            try {
                Uri imageUri = Uri.fromFile(new File(path));
                Bitmap mBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                if (!TextUtils.isEmpty(path)) {
                    mBitmap = ImageUtils.modifyOrientation(mBitmap, path);
                }
                iViewScanner.onResult(mBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCropBitMap(Map<Integer, PointF> points, Bitmap selectedImageBitmap, ImageView imageView) {

        File file = new File(FOLDER_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        new handleImage(points, selectedImageBitmap, imageView).execute();
    }

    @Override
    public Bitmap getBitMapSelected() {
        return bitmap;
    }

    @Override
    public void setBitMapSelected(Bitmap bitMapSelected) {
        this.bitmap = bitMapSelected;
    }

    private class handleImage extends AsyncTask<Void, Void, Bitmap> {
        //        private ProgressDialog progress;
        Map<Integer, PointF> points;
        Bitmap selectedImageBitmap;
        ImageView imageView;

        public handleImage(Map<Integer, PointF> points, Bitmap selectedImageBitmap, ImageView imageView) {
            this.points = points;
            this.selectedImageBitmap = selectedImageBitmap;
            this.imageView = imageView;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Bitmap doInBackground(Void... voids) {
            Bitmap bitmap = scanner.getCroppedImage(points, selectedImageBitmap, imageView);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                PresenterScanner.bitmapSelected = bitmap;
                iViewScanner.editImage(FILE_NAME);
            }

        }

    }
}
