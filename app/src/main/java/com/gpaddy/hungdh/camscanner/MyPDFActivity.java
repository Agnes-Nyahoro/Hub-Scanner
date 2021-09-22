package com.gpaddy.hungdh.camscanner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gpaddyv1.queenscanner.Config.AdsTask;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.todobom.queenscanner.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.joshuabutton.queenscanner.PresenterScanner.FOLDER_NAME;

/**
 * Created by HUNGDH on 12/24/2016.
 */

public class MyPDFActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    ArrayList<String> inFiles;
    FilesAdapter adapter;
    File[] files;
    File folder;

    @BindView(R.id.list)
    ListView listView;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipeView;
    LinearLayout llAds;

    private TextView btnCreatingPDF;
    private static final int INTENT_REQUEST_GET_IMAGES = 1;
    private List<String> imagesUri = new ArrayList<>();
    private Image image;
    private String filename;
    private String path;
    private AdsTask adsTask;

//    private MyInterstitials myInterstitials;

    //Backup
//    private AutoEmail autoEmail;
//    private AutoNextCloud autoNextCloud;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pdf);
        adsTask = new AdsTask(this);
        llAds = findViewById(R.id.ll_ads);
        adsTask.loadBannerAdsFacebook(llAds);
        initActionBar();

        ButterKnife.bind(this);

        //Create/Open folder
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String folderName = mSharedPref.getString("storage_folder", FOLDER_NAME);
        folder = new File(Environment.getExternalStorageDirectory() + "/" + folderName + "/");
        if (!folder.exists()) {
            folder.mkdir();
        }

        // Initialize variables
        inFiles = new ArrayList<>();
        files = folder.listFiles();
        adapter = new FilesAdapter(this, inFiles);
        listView.setAdapter(adapter);
        swipeView.setOnRefreshListener(this);

        // Populate data into listView
        populateListView();

        btnCreatingPDF = findViewById(R.id.btnCreatingPDF);
        btnCreatingPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageToCreatePDF();
            }
        });

//        initBackup();

//        myInterstitials = new MyInterstitials(this);
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);
    }

//    private void initBackup() {
//        autoEmail = new AutoEmail(this);
//        autoNextCloud = new AutoNextCloud(this);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getImageToCreatePDF() {
        Intent intent = new Intent(MyPDFActivity.this, ImagePickerActivity.class);
        startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
    }

    public void populateListView() {
        inFiles = new ArrayList<>();
        files = folder.listFiles();
        if (files == null)
            Toast.makeText(this, getString(R.string.no_pdf), Toast.LENGTH_LONG).show();
        else {
            addFile(folder);
//            for (File file : files) {
//                if (!file.isDirectory() && file.getName().endsWith(".pdf")) {
//                    inFiles.add(file.getPath());
//                    Collections.reverse(inFiles);
//                    Log.v("adding", file.getName());
//                } else if (file.isDirectory()) {
//                    File[] files1 = file.listFiles();
//
//                }
//            }

        }
        Log.v("done", "adding");
        adapter = new FilesAdapter(this, inFiles);
        listView.setAdapter(adapter);
    }

    private void addFile(File file0) {
        File[] files = file0.listFiles();
        for (File file : files) {
            if (!file.isDirectory() && file.getName().endsWith(".pdf")) {
                inFiles.add(file.getPath());
                Log.v("adding", file.getName());
            } else if (file.isDirectory()) {
                addFile(file);
            }
        }

    }

    @Override
    public void onRefresh() {
        Log.v("refresh", "refreshing dta");
        populateListView();
        swipeView.setRefreshing(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_REQUEST_GET_IMAGES && resultCode == Activity.RESULT_OK) {

            ArrayList<Uri> image_uris = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
            if (image_uris.size() > 0) {
                imagesUri = new ArrayList<>();
                for (int i = 0; i < image_uris.size(); i++) {
                    imagesUri.add(image_uris.get(i).getPath());
                }
                createPdf();
            } else {
                Toast.makeText(MyPDFActivity.this, getString(R.string.no_image_selected), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void createPdf() {
        path = folder.getAbsolutePath() + "/";

        if (imagesUri.size() > 0) {
            showCustomView();
        }
    }

    private Spinner spinnerPageSize, spinnerImageScale;
    private EditText input_name_pdf;
    public static int positionOfPageSize = 1; // default A4
    public static int positionOfImageScale = 0; // default FIT Width or Height

    public static ArrayList<String> PAGE_SIZE_NAME = new ArrayList<>(Arrays.asList("A3 (29.7cm x 42.0cm)", "A4 (21.0cm x 29.7cm)", "A5 (14.8cm x 21.0cm)", "B4 (25cm x 35.3cm)", "B5 (17.6cm x 25cm)"
            , "Letter (21.6cm x 27.9cm)", "Tabloid (27.9cm x 43.2cm", "Legal (21,6cm x 35.6cm)", "Executive (18.4cm x 26.7cm)", "Postcard (10.0cm x 14.7cm)", "American Foolscap(21.6cm x 33.0cm)", "Europe Foolscap (22.9cm x 33.0cm)"));

    public static ArrayList<Rectangle> PAGE_SIZE_VALUE = new ArrayList<>(Arrays.asList(PageSize.A3, PageSize.A4, PageSize.A5, PageSize.B4, PageSize.B5, PageSize.LETTER, PageSize.TABLOID, PageSize.LEGAL, PageSize.EXECUTIVE, PageSize.POSTCARD, PageSize.FLSA, PageSize.FLSE));

    public static ArrayList<String> IMAGE_SCALE_NAME = new ArrayList<>(Arrays.asList("Fit width or height", "Fill the entire page"));

    public void showCustomView() {
        MaterialDialog build = new MaterialDialog.Builder(this).title(getString(R.string.creating_pdf)).customView(R.layout.layout_custom_dialog_pdf_option, true).positiveText(getString(R.string.ok)).negativeText(getString(R.string.cancel)).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                try {
                    if (input_name_pdf.getText().toString().trim().length() > 0) {
                        filename = input_name_pdf.getText().toString();

                        //Page Size
                        positionOfPageSize = spinnerPageSize.getSelectedItemPosition();
                        positionOfImageScale = spinnerImageScale.getSelectedItemPosition();

                        // creating PDF
                        if (!new File(path = path + filename + ".pdf").exists()) {
                            new creatingPDF().execute();
                        } else {
                            Toast.makeText(MyPDFActivity.this, "File already exists", Toast.LENGTH_SHORT).show();
                        }

                    }
                } catch (Exception e) {
                    Toast.makeText(MyPDFActivity.this, getString(R.string.error_blank_name), Toast.LENGTH_SHORT);
                }

            }
        }).build();
        this.spinnerPageSize = (Spinner) build.getCustomView().findViewById(R.id.page_size_spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, PAGE_SIZE_NAME);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPageSize.setAdapter(dataAdapter);
        spinnerPageSize.setSelection(5); // Letter is default

        this.spinnerImageScale = (Spinner) build.getCustomView().findViewById(R.id.image_scale_spinner);
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, IMAGE_SCALE_NAME);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinnerImageScale.setAdapter(dataAdapter2);

        this.input_name_pdf = (EditText) build.getCustomView().findViewById(R.id.input_name_pdf);

        build.show();

    }

    public class creatingPDF extends AsyncTask<String, String, String> {
        // Progress dialog
        MaterialDialog.Builder builder = new MaterialDialog.Builder(MyPDFActivity.this)
                .title(getString(R.string.please_wait))
                .content(getString(R.string.creating_pdf_des))
                .cancelable(false)
                .progress(true, 0);
        MaterialDialog dialog = builder.build();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            path = path + filename + ".pdf";
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

//            autoEmail.sendEmailPdf(path);
//            autoNextCloud.uploadFilePdf(path);

            //Toast.makeText(MyPDFActivity.this, getString(R.string.creating_pdf_success), Toast.LENGTH_SHORT).show();
            onRefresh();
            adsTask.showInterstitialAds();
//            myInterstitials.showInterstitial();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        adsTask.showInterstitialAds();
    }
}
