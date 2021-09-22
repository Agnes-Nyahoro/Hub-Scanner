package com.gpaddy.hungdh.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gpaddy.hungdh.camscanner.MyPDFActivity;
import com.gpaddyv1.queenscanner.Config.AdsTask;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.todobom.queenscanner.R;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import static com.gpaddy.hungdh.camscanner.MyPDFActivity.PAGE_SIZE_VALUE;
import static com.gpaddy.hungdh.camscanner.MyPDFActivity.positionOfImageScale;
import static com.gpaddy.hungdh.camscanner.MyPDFActivity.positionOfPageSize;

public class ImageUtils {

    public static Bitmap rotateBitmap(Bitmap original, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);
    }

    public static Mat bitmapToMat(Bitmap bitmap) {
        Mat mat = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8U, new Scalar(4));
        Bitmap bitmap32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bitmap32, mat);
        return mat;
    }

    public static Bitmap matToBitmap(Mat mat) {
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }

    public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) {
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(image_absolute_path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotate(bitmap, 90);

                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotate(bitmap, 180);

                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotate(bitmap, 270);

                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    return flip(bitmap, true, false);

                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    return flip(bitmap, false, true);

                default:
                    return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return bitmap;
        }

    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static void saveBitMap(Bitmap bitmap, String imagePath) {
        OutputStream bufferedOutputStream = null;
        try {
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(imagePath));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, bufferedOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void convertImageToPdf(List<String> imagePaths, String destination, Context context) {
        if (imagePaths != null && imagePaths.size() > 0) {
            new imageToPdf(imagePaths, context).execute(destination);
        }

    }

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private static class imageToPdf extends AsyncTask<String, String, String> {
        private List<String> imagesUri;
        private Image image;
        private Context context;
        private MaterialDialog.Builder builder;
        MaterialDialog dialog;
        String path;

        public imageToPdf(List<String> imagesUri, Context context) {
            this.imagesUri = imagesUri;
            this.context = context;
            builder = new MaterialDialog.Builder(context)
                    .title(context.getString(R.string.please_wait))
                    .content(context.getString(R.string.creating_pdf_des))
                    .cancelable(false)
                    .progress(true, 0);
            dialog = builder.build();
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            path = params[0];
            Log.v("stage 1", "store the pdf in sd card");

            Document document = new Document(PAGE_SIZE_VALUE.get(positionOfPageSize), 38, 38, 50, 38);

            Log.v("stage 2", "Document Created");
            Rectangle documentRect = document.getPageSize();

            try {
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));

                Log.v("Stage 3", "Pdf writer");
                document.open();
                Log.v("Stage 4", "Document opened");

                for (int i = 0; i < imagesUri.size(); i++) {
                    Bitmap bmp = BitmapFactory.decodeFile(imagesUri.get(i));
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 70, stream);

                    image = Image.getInstance(imagesUri.get(i));
                    if (positionOfImageScale == 0) {
                        if (bmp.getWidth() > documentRect.getWidth() || bmp.getHeight() > documentRect.getHeight()) {
                            //bitmap is larger than page,so set bitmap's size similar to the whole page
                            image.scaleToFit(documentRect.getWidth(), documentRect.getHeight());
                        } else {
                            //bitmap is smaller than page, so add bitmap simply.[note: if you want to fill page by stretching image, you may set size similar to page as above]
                            image.scaleToFit(bmp.getWidth(), bmp.getHeight());
                        }
                    } else {
                        image.scaleAbsolute(documentRect.getWidth(), documentRect.getHeight());
                    }

                    Log.v("Stage 6", "Image path adding");
                    image.setAbsolutePosition((documentRect.getWidth() - image.getScaledWidth()) / 2, (documentRect.getHeight() - image.getScaledHeight()) / 2);
                    Log.v("Stage 7", "Image Alignments");
                    image.setBorder(Image.BOX);
                    image.setBorderWidth(15);
                    document.add(image);
                    document.newPage();
                }

                Log.v("Stage 8", "Image adding");
                document.close();
                Log.v("Stage 7", "Document Closed" + path);
            } catch (Exception e) {
                e.printStackTrace();
            }

            document.close();
            imagesUri.clear();

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();

            builder = new MaterialDialog.Builder(context)
                    .title("File name")
                    .content(new File(path).getName())
                    .positiveText("View")
                    .negativeText("Cancel")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            context.startActivity(new Intent(context, MyPDFActivity.class));
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            builder.build().dismiss();
                        }
                    });

            dialog = builder.build();
            dialog.show();

            AdsTask adsTask = new AdsTask(context);
            adsTask.showInterstitialAds();
        }
    }

}
