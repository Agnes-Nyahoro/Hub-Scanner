package com.gpaddy.hungdh.camscanner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.styleimageview.Styler;
import com.todobom.queenscanner.R;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ly.img.android.sdk.models.constant.Directory;
import ly.img.android.sdk.models.state.EditorLoadSettings;
import ly.img.android.sdk.models.state.EditorSaveSettings;
import ly.img.android.sdk.models.state.manager.SettingsList;
import ly.img.android.ui.activities.ImgLyIntent;
import ly.img.android.ui.activities.PhotoEditorBuilder;

import static com.joshuabutton.queenscanner.PresenterScanner.FOLDER_NAME;

/**
 * Created by HUNGDH on 1/16/2017.
 */

public class FilterImageActivity extends Activity implements View.OnClickListener {

    private RelativeLayout rlOriginal, rlGrayMode, rlColorMode;
    private TextView tvOriginal, tvGrayMode, tvColorMode;

    private LinearLayout llGMDetail;
    private RelativeLayout rlGM1, rlGM2, rlGM3, rlGM4;

    private LinearLayout llCMDetail;
    private RelativeLayout rlCM1, rlCM2, rlCM3, rlCM4;

    private ImageView imageView;

    private String pathOri = "";
    private Bitmap original, blackMode;

    private Styler styler;

    private RelativeLayout rlBack, rlEdit, rlOcr, rlEnhance, rlDone;
    private ImageView ivEnhance;

    private LinearLayout llEnhance;
    private SeekBar brightnessBar, contrastBar, saturationBar;

    private final int CAMERA_PREVIEW_RESULT = 1;

    private MyInterstitials myInterstitials;

    //OCR
    private String[] arrLanguagesCode;
    private String[] arrLanguagesLink;
    private String datapath = ""; //path to folder containing language data file

    //Backup
//    private AutoEmail autoEmail;
//    private AutoNextCloud autoNextCloud;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_image);

        getData();

        initView();

//        initBackup();

//        myInterstitials = new MyInterstitials(this);
    }

    private void getData() {
        try {
            pathOri = getIntent().getStringExtra("imageOriginal");

            original = getBitmapFromFile(pathOri);
            blackMode = convertToBlackMode(original);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap getBitmapFromFile(String path) {
        File imgFile = new File(path);
        if (imgFile.exists()) {
            return BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        } else {
            return null;
        }
    }

    private void initView() {
        imageView = (ImageView) findViewById(R.id.imageView);

        styler = new Styler.Builder(imageView, Styler.Mode.NONE).enableAnimation(500).build();

        rlOriginal = (RelativeLayout) findViewById(R.id.rlOriginal);
        rlOriginal.setOnClickListener(this);
        rlGrayMode = (RelativeLayout) findViewById(R.id.rlGrayMode);
        rlGrayMode.setOnClickListener(this);
        rlColorMode = (RelativeLayout) findViewById(R.id.rlColorMode);
        rlColorMode.setOnClickListener(this);

        tvOriginal = (TextView) findViewById(R.id.tvOriginal);
        tvGrayMode = (TextView) findViewById(R.id.tvGrayMode);
        tvColorMode = (TextView) findViewById(R.id.tvColorMode);

        llGMDetail = (LinearLayout) findViewById(R.id.llGMDetail);
        rlGM1 = (RelativeLayout) findViewById(R.id.rlGM1);
        rlGM1.setOnClickListener(this);
        rlGM2 = (RelativeLayout) findViewById(R.id.rlGM2);
        rlGM2.setOnClickListener(this);
        rlGM3 = (RelativeLayout) findViewById(R.id.rlGM3);
        rlGM3.setOnClickListener(this);
        rlGM4 = (RelativeLayout) findViewById(R.id.rlGM4);
        rlGM4.setOnClickListener(this);

        llCMDetail = (LinearLayout) findViewById(R.id.llCMDetail);
        rlCM1 = (RelativeLayout) findViewById(R.id.rlCM1);
        rlCM1.setOnClickListener(this);
        rlCM2 = (RelativeLayout) findViewById(R.id.rlCM2);
        rlCM2.setOnClickListener(this);
        rlCM3 = (RelativeLayout) findViewById(R.id.rlCM3);
        rlCM3.setOnClickListener(this);
        rlCM4 = (RelativeLayout) findViewById(R.id.rlCM4);
        rlCM4.setOnClickListener(this);

        rlBack = (RelativeLayout) findViewById(R.id.rlBack);
        rlBack.setOnClickListener(this);
        rlEdit = (RelativeLayout) findViewById(R.id.rlEdit);
        rlEdit.setOnClickListener(this);
        rlOcr = (RelativeLayout) findViewById(R.id.rlOcr);
        rlOcr.setOnClickListener(this);
        rlEnhance = (RelativeLayout) findViewById(R.id.rlEnhance);
        rlEnhance.setOnClickListener(this);
        rlDone = (RelativeLayout) findViewById(R.id.rlDone);
        rlDone.setOnClickListener(this);

        ivEnhance = (ImageView) findViewById(R.id.ivEnhance);

        llEnhance = (LinearLayout) findViewById(R.id.llEnhance);
        brightnessBar = (SeekBar) findViewById(R.id.seekbar_brightness);
        contrastBar = (SeekBar) findViewById(R.id.seekbar_contrast);
        saturationBar = (SeekBar) findViewById(R.id.seekbar_saturation);

        brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    styler.setBrightness(i - 255).updateStyle();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        contrastBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    styler.setContrast(i / 100F).updateStyle();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        saturationBar.setProgress((int) (styler.getSaturation() * 100));
        saturationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    styler.setSaturation(i / 100F).updateStyle();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        arrLanguagesCode = getResources().getStringArray(R.array.language_code);
        arrLanguagesLink = getResources().getStringArray(R.array.language_link);

        getOriginal();
    }

//    private void initBackup() {
//        autoEmail = new AutoEmail(this);
//        autoNextCloud = new AutoNextCloud(this);
//    }

    private void setBitmapToImageView(Bitmap bm) {
        if (bm != null) {
            imageView.setImageBitmap(bm);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlOriginal:
                getOriginal();
                break;
            case R.id.rlGrayMode:
                grayModeClick();
                break;
            case R.id.rlColorMode:
                colorModeClick();
                break;

            case R.id.rlGM1:
                setBitmapToImageView(blackMode);
                rlEdit.setVisibility(View.GONE);
                break;
            case R.id.rlGM2:
                rlEdit.setVisibility(View.GONE);
                setBitmapToImageView(original);
                styler.setMode(Styler.Mode.GREY_SCALE).updateStyle();
                break;
            case R.id.rlGM3:
                rlEdit.setVisibility(View.GONE);
                setBitmapToImageView(original);
                styler.setMode(Styler.Mode.SEPIA).updateStyle();
                break;
            case R.id.rlGM4:
                rlEdit.setVisibility(View.GONE);
                setBitmapToImageView(original);
                styler.setMode(Styler.Mode.BLACK_AND_WHITE).updateStyle();
                break;

            case R.id.rlCM1:
                rlEdit.setVisibility(View.GONE);
                setBitmapToImageView(original);
                styler.setMode(Styler.Mode.BRIGHT).updateStyle();
                break;
            case R.id.rlCM2:
                rlEdit.setVisibility(View.GONE);
                setBitmapToImageView(original);
                styler.setMode(Styler.Mode.VINTAGE_PINHOLE).updateStyle();
                break;
            case R.id.rlCM3:
                rlEdit.setVisibility(View.GONE);
                setBitmapToImageView(original);
                styler.setMode(Styler.Mode.KODACHROME).updateStyle();
                break;
            case R.id.rlCM4:
                rlEdit.setVisibility(View.GONE);
                setBitmapToImageView(original);
                styler.setMode(Styler.Mode.TECHNICOLOR).updateStyle();
                break;

            case R.id.rlBack:
                finish();
                break;
            case R.id.rlEdit:
                callEdit();
                break;
            case R.id.rlOcr:
                callOcr();
                break;
            case R.id.rlEnhance:
                callEnhance();
                break;
            case R.id.rlDone:
                done();
                break;
        }
    }

    private void getOriginal() {
        setBitmapToImageView(original);
        tvOriginal.setTextColor(getResources().getColor(R.color.colorAccent));
        tvGrayMode.setTextColor(getResources().getColor(R.color.white));
        tvColorMode.setTextColor(getResources().getColor(R.color.white));

        rlEdit.setVisibility(View.VISIBLE);
        llGMDetail.setVisibility(View.GONE);
        llCMDetail.setVisibility(View.GONE);

        brightnessBar.setProgress(255);
        contrastBar.setProgress(100);
        saturationBar.setProgress(100);
    }

    private void grayModeClick() {
        tvOriginal.setTextColor(getResources().getColor(R.color.white));
        tvGrayMode.setTextColor(getResources().getColor(R.color.colorAccent));
        tvColorMode.setTextColor(getResources().getColor(R.color.white));

        if (llGMDetail.getVisibility() == View.VISIBLE) {
            llGMDetail.setVisibility(View.GONE);
        } else {
            llGMDetail.setVisibility(View.VISIBLE);
            llCMDetail.setVisibility(View.GONE);
        }
    }

    private void colorModeClick() {
        tvOriginal.setTextColor(getResources().getColor(R.color.white));
        tvGrayMode.setTextColor(getResources().getColor(R.color.white));
        tvColorMode.setTextColor(getResources().getColor(R.color.colorAccent));

        if (llCMDetail.getVisibility() == View.VISIBLE) {
            llCMDetail.setVisibility(View.GONE);
        } else {
            llCMDetail.setVisibility(View.VISIBLE);
            llGMDetail.setVisibility(View.GONE);
        }
    }

    private void callEdit() {
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String folderName = mSharedPref.getString("storage_folder", FOLDER_NAME);
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()
                + "/" + folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        SettingsList settingsList = new SettingsList();
        settingsList.getSettingsModel(EditorLoadSettings.class)
                .setImageSourcePath(pathOri, true)
                .getSettingsModel(EditorSaveSettings.class)
                .setExportDir(Directory.DCIM, folderName)
                .setSavePolicy(
                        EditorSaveSettings.SavePolicy.KEEP_SOURCE_AND_CREATE_ALWAYS_OUTPUT
                );
        new PhotoEditorBuilder(this)
                .setSettingsList(settingsList)
                .startActivityForResult(this, CAMERA_PREVIEW_RESULT);
    }

    private void callOcr() {
        new MaterialDialog.Builder(this)
                .title(R.string.language)
                .items(R.array.language_name)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        dialog.dismiss();
                        runOCR(which);
                        return true;
                    }
                })
                .positiveText(R.string.choose)
                .show();
    }

    private void runOCR(int position) {
        datapath = Environment.getExternalStorageDirectory().getAbsolutePath() + FOLDER_NAME;
        File f = new File(datapath);
        if (f.exists() || (!f.exists() && f.mkdir())) {
            checkFile(new File(datapath + "tessdata/"), position);
        }
    }

    private void checkFile(File dir, int position) {
        //directory does not exist, but we can successfully create it
        if (!dir.exists() && dir.mkdir()) {
            copyFiles(position);
        }

        //The directory exists, but there is no data file in it
        if (dir.exists()) {
            String langCode = arrLanguagesCode[position];

            String datafilepath = datapath + "/tessdata/" + langCode + ".traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles(position);
            } else {
                new AsyncOCR().execute(langCode);
            }
        }
    }

    private void copyFiles(int position) {
        new AsyncDownload().execute(arrLanguagesCode[position], arrLanguagesLink[position]);
    }

    private class AsyncDownload extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;

        private String langCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(FilterImageActivity.this);
            dialog.setMessage("Downloading...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            langCode = strings[0];
            String langLink = strings[1];

            try {
                //location we want the file to be at
                String filepath = datapath + "/tessdata/" + langCode + ".traineddata";

                URL url = new URL(langLink);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // this will be useful so that you can show a typical 0-100% progress bar
                int fileLength = connection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(connection.getInputStream());
                OutputStream output = new FileOutputStream(filepath);

                byte data[] = new byte[8192];
                long total = 0;
                int count;
                int i = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    i++;
                    if (i > 5) {
                        // 5 times download to update UI 1 times, to boost speed
                        i = 0;
                        //Notification
                        int progress = (int) (total * 100 / fileLength);
                        Log.d("myLog", "PERCENT: " + progress);
                    }
                    output.write(data, 0, count);

                    if (isCancelled()) {
                        break;
                    }
                }

                output.flush();
                output.close();
                input.close();
                return "OK";
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }

            if (result.equals("OK")) {
                new AsyncOCR().execute(langCode);
            } else {
                Toast.makeText(FilterImageActivity.this, "Download failure!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class AsyncOCR extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(FilterImageActivity.this);
            dialog.setMessage("Analyzing...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            //init image
            int nh = (int) (original.getHeight() * (512.0 / original.getWidth()));
            Bitmap image = Bitmap.createScaledBitmap(original, 512, nh, true);

            //init Tesseract API
            String language = strings[0];

            TessBaseAPI mTess = new TessBaseAPI();
            mTess.init(datapath, language);

            String OCRresult = null;
            mTess.setImage(image);

            OCRresult = mTess.getUTF8Text();
            return OCRresult;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }

            if (result != null) {
                //Toast.makeText(FilterImageActivity.this, result, Toast.LENGTH_SHORT).show();
                showResultOCR(result);
            } else {
                Toast.makeText(FilterImageActivity.this, "Analyze failure!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showResultOCR(final String result) {
        new MaterialDialog.Builder(this)
                .title("Result")
                .content(result)
                .negativeText("Edit")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent i = new Intent(FilterImageActivity.this, KnifeActivity.class);
                        i.putExtra("resultOCR", result);
                        startActivity(i);
                    }
                })
                .positiveText("Copy")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Copied Text", result);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(FilterImageActivity.this, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void callEnhance() {
        if (llEnhance.getVisibility() == View.VISIBLE) {
            llEnhance.setVisibility(View.GONE);
            ivEnhance.setImageDrawable(getResources().getDrawable(R.drawable.ic_fr_enhance));
        } else {
            llEnhance.setVisibility(View.VISIBLE);
            ivEnhance.setImageDrawable(getResources().getDrawable(R.drawable.ic_fr_enhance_press));
        }
    }

    private void done() {
        imageView.destroyDrawingCache();
        imageView.buildDrawingCache();
        Bitmap result = imageView.getDrawingCache();

        if (pathOri.contains(".TEMP_ORIGINAL.xxx")) {
            pathOri = pathOri.replace(".TEMP_ORIGINAL.xxx", "img_" + System.currentTimeMillis() + ".jpg");
        }

        File picture = new File(pathOri);

        try {
            FileOutputStream fos = new FileOutputStream(picture);
            result.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();

            Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(picture);
            scanIntent.setData(contentUri);
            sendBroadcast(scanIntent);
            //Toast.makeText(this, "Save image at: " + pathOri, Toast.LENGTH_SHORT).show();

            Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);

//            autoEmail.sendEmailImage(pathOri);
//            autoNextCloud.uploadFileImage(pathOri);

//            myInterstitials.showInterstitial();
        } catch (FileNotFoundException e) {
            Log.d("hungdhLog", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("hungdhLog", "Error accessing file: " + e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CAMERA_PREVIEW_RESULT) {
            String resultPath =
                    data.getStringExtra(ImgLyIntent.RESULT_IMAGE_PATH);
            String sourcePath =
                    data.getStringExtra(ImgLyIntent.SOURCE_IMAGE_PATH);

            if (resultPath != null) {
                // Scan result file
                pathOri = resultPath;
                original = getBitmapFromFile(pathOri);
                setBitmapToImageView(original);

                blackMode = convertToBlackMode(original);
            }
        }
    }

    private Bitmap convertToBlackMode(Bitmap bitmap) {
        Mat orig = new Mat();
        org.opencv.android.Utils.bitmapToMat(bitmap, orig);
        return applyThreshold(orig);
    }

    private Bitmap applyThreshold(Mat src) {
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(src, src, new Size(5, 5), 0);
        Imgproc.adaptiveThreshold(src, src, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);
        Bitmap bm = Bitmap.createBitmap(src.width(), src.height(), Bitmap.Config.ARGB_8888);
        org.opencv.android.Utils.matToBitmap(src, bm);
        return bm;
    }
}
