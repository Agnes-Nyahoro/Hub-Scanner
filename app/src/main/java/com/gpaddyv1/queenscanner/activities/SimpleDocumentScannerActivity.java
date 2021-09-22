package com.gpaddyv1.queenscanner.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.preference.PreferenceManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gpaddy.hungdh.camscanner.FilterImageActivity;
import com.gpaddy.hungdh.libary.PolygonView;
import com.gpaddyv1.queenscanner.document.DocumentActivity;
import com.gpaddyv1.queenscanner.process.view.ProcessImageActivity;
import com.joshuabutton.queenscanner.IPresenterScanner;
import com.joshuabutton.queenscanner.IViewScanner;
import com.joshuabutton.queenscanner.OpenCVCallback;
import com.joshuabutton.queenscanner.PresenterScanner;
import com.todobom.queenscanner.R;

import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import timber.log.Timber;

import static com.joshuabutton.queenscanner.PresenterScanner.FOLDER_NAME;

public class SimpleDocumentScannerActivity extends AppCompatActivity implements IViewScanner, View.OnClickListener {

    public static final String KEY_DOCUMENT = "key_document";

    //    QuadrilateralSelectionImageView mSelectionImageView;
    ImageView mButton;
    private ImageView imageView;
    private PolygonView polygonView;
    Bitmap mBitmap;
    Bitmap mResult;
    private String folderPath;
    MaterialDialog mResultDialog;
    OpenCVCallback mOpenCVLoaderCallback;
    private FrameLayout holderImageCrop;
    private IPresenterScanner presenterScanner;
    private static final int MAX_HEIGHT = 500;

    private int PICK_IMAGE_REQUEST = 1;
    private static final int EDIT_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_document_scanner);

        initActionBar();

        initView();


//        mSelectionImageView = (QuadrilateralSelectionImageView) findViewById(R.id.polygonView);

        mButton = findViewById(R.id.btnImageEnhance);
        folderPath = getIntent().getStringExtra("folderPath");
//        mResultDialog = new MaterialDialog.Builder(this)
//                .title("Result")
//                .positiveText("Process")
//                .negativeText("Cancel")
//                .customView(R.layout.dialog_simple_document_scan_result, false)
//                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
//                        // TODO Saving
//                        storeImage();
//                        mResult = null;
//                    }
//                })
//                .onNegative(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
//                        mResult = null;
//                    }
//                })
//                .build();

        mButton.setOnClickListener(this);

        mOpenCVLoaderCallback = new OpenCVCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS: {
                        break;
                    }

                    default: {
                        super.onManagerConnected(status);
                    }
                }
            }
        };
        initOpenCV();

        presenterScanner.getDataFromIntent(getIntent().getStringExtra(KEY_DOCUMENT));

//        getDataFromIntent();
    }

    private void initView() {
        imageView = (ImageView) findViewById(R.id.imageView);
        polygonView = (PolygonView) findViewById(R.id.polygonView);
        holderImageCrop = (FrameLayout) findViewById(R.id.holderImageCrop);
        presenterScanner = new PresenterScanner(this, this);
    }

    private void storeImage() {
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String folderName = mSharedPref.getString("storage_folder", FOLDER_NAME);
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()
                + "/" + folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String fileName = folder.getAbsolutePath() + "/.TEMP_ORIGINAL.xxx";

        File picture = new File(fileName);

        try {
            FileOutputStream fos = new FileOutputStream(picture);
            mResult.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();

            //Toast.makeText(this, "Save image at: " + fileName, Toast.LENGTH_SHORT).show();
            Intent i = new Intent(SimpleDocumentScannerActivity.this, FilterImageActivity.class);
            i.putExtra("imageOriginal", fileName);
            startActivity(i);
        } catch (FileNotFoundException e) {
            Log.d("hungdhLog", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("hungdhLog", "Error accessing file: " + e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initOpenCV();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_simple_menu, menu);
        return true;
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        if (id == R.id.action_gallery) {
            chooseImage();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                mButton.setVisibility(View.VISIBLE);
                Bitmap bitmap = presenterScanner.onResult(data.getData());
                imageView.setImageBitmap(bitmap);
                presenterScanner.onResult(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == DocumentActivity.REQUEST_IMPORT && resultCode == RESULT_OK) {
            mButton.setVisibility(View.VISIBLE);
            setResult(RESULT_OK);
            finish();
        }
    }

    private void initOpenCV() {
        if (!OpenCVLoader.initDebug()) {
            Timber.d("Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mOpenCVLoaderCallback);
        } else {
            Timber.d("OpenCV library found inside package. Using it!");
            mOpenCVLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    private Bitmap scaledBitmap(Bitmap bitmap, int width, int height) {
        Log.v("aashari-tag", "scaledBitmap");
        Log.v("aashari-tag", width + " " + height);
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, width, height), Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }

    @Override
    public void onResult(final Bitmap selectedImageBitmap) {
        holderImageCrop.post(new Runnable() {
            @Override
            public void run() {
                Bitmap scaledBitmap = scaledBitmap(selectedImageBitmap, holderImageCrop.getWidth(), holderImageCrop.getHeight());
                imageView.setImageBitmap(scaledBitmap);
                presenterScanner.setBitMapSelected(selectedImageBitmap);
                Bitmap tempBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                Map<Integer, PointF> pointFs = presenterScanner.getEdgePoint(tempBitmap, polygonView);

                polygonView.setPoints(pointFs);
                polygonView.setVisibility(View.VISIBLE);

                int padding = (int) getResources().getDimension(R.dimen.scanPadding);

                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(tempBitmap.getWidth() + 2 * padding, tempBitmap.getHeight() + 2 * padding);
                layoutParams.gravity = Gravity.CENTER;

                polygonView.setLayoutParams(layoutParams);
            }
        });

    }

    @Override
    public void chooseImage() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnImageEnhance:
                presenterScanner.onCropBitMap(polygonView.getPoints(), presenterScanner.getBitMapSelected(), imageView);
                finish();
                break;
        }
    }

    @Override
    public void editImage(String path) {
        ProcessImageActivity.startProcess(SimpleDocumentScannerActivity.this, folderPath);
        finish();
    }

    public static void startScanner(Context context, String pathImage, String folderPath) {
        Intent intent = new Intent(context, SimpleDocumentScannerActivity.class);
        intent.putExtra(SimpleDocumentScannerActivity.KEY_DOCUMENT, pathImage);
        intent.putExtra("folderPath", folderPath);
        if (context instanceof DocumentActivity) {
            ((DocumentActivity) context).startActivityForResult(intent, DocumentActivity.REQUEST_IMPORT);
        } else {
            context.startActivity(intent);
        }

    }

}
