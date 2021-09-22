package com.todobom.queenscanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.dragselectrecyclerview.DragSelectRecyclerView;
import com.afollestad.dragselectrecyclerview.DragSelectRecyclerViewAdapter;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.todobom.queenscanner.helpers.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.joshuabutton.queenscanner.PresenterScanner.FOLDER_NAME;

//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;

public class GalleryGridActivity extends AppCompatActivity
        implements ClickListener, DragSelectRecyclerViewAdapter.SelectionListener {

    private static final String TAG = "GalleryGridActivity";
    private MenuItem mShare;
    //private MenuItem mTag;
    private MenuItem mDelete;
    private DragSelectRecyclerView recyclerView;
    private AlertDialog.Builder deleteConfirmBuilder;
    private boolean selectionMode = false;
    private ImageLoader mImageLoader;
    private ImageSize mTargetSize;
    private SharedPreferences mSharedPref;

    //Create PDF
    private static final int INTENT_REQUEST_GET_IMAGES = 1;
    private List<String> imagesUri = new ArrayList<>();
    private Image image;
    private String filename;
    private String path;

    //Backup
//    private AutoEmail autoEmail;
//    private AutoNextCloud autoNextCloud;

    @Override
    public void onClick(int index) {
        if (selectionMode) {
            myThumbAdapter.toggleSelected(index);
        } else {
            Intent i = new Intent(this, FullScreenViewActivity.class);
            i.putExtra("position", index);
            this.startActivity(i);
        }
    }

    @Override
    public void onLongClick(int index) {
        if (!selectionMode) {
            setSelectionMode(true);
        }
        recyclerView.setDragSelectActive(true, index);
    }

    private void setSelectionMode(boolean selectionMode) {
        if (mShare != null && mDelete != null) {
            mShare.setVisible(selectionMode);
            //mTag.setVisible(selectionMode);
            mDelete.setVisible(selectionMode);
        }
        this.selectionMode = selectionMode;
    }

    @Override
    public void onDragSelectionChanged(int i) {
        Log.d(TAG, "DragSelectionChanged: " + i);

        setSelectionMode(i > 0);
    }

    public class ThumbAdapter extends DragSelectRecyclerViewAdapter<ThumbAdapter.ThumbViewHolder> {

        private final ClickListener mCallback;

        ArrayList<String> itemList = new ArrayList<>();

        // Constructor takes click listener callback
        protected ThumbAdapter(GalleryGridActivity activity, ArrayList<String> files) {
            super();
            mCallback = activity;

            for (String file : files) {
                add(file);
            }

            setSelectionListener(activity);

        }

        void add(String path) {
            itemList.add(path);
        }

        @Override
        public ThumbViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false);
            return new ThumbViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ThumbViewHolder holder, int position) {
            super.onBindViewHolder(holder, position); // this line is important!

            String filename = itemList.get(position);

            if (!filename.equals(holder.filename)) {

                // remove previous image
                holder.image.setImageBitmap(null);

                // Load image, decode it to Bitmap and return Bitmap to callback
                mImageLoader.displayImage("file:///" + filename, holder.image, mTargetSize);

                // holder.image.setImageBitmap(decodeSampledBitmapFromUri(filename, 220, 220));

                holder.filename = filename;
            }

            if (isIndexSelected(position)) {
                holder.image.setColorFilter(Color.argb(140, 0, 255, 0));
            } else {
                holder.image.setColorFilter(Color.argb(0, 0, 0, 0));
            }
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public ArrayList<String> getSelectedFiles() {

            ArrayList<String> selection = new ArrayList<>();

            for (Integer i : getSelectedIndices()) {
                selection.add(itemList.get(i));
            }

            return selection;
        }


        public class ThumbViewHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener, View.OnLongClickListener {

            public final ImageView image;
            public String filename;

            public ThumbViewHolder(View itemView) {
                super(itemView);
                this.image = (ImageView) itemView.findViewById(R.id.gallery_image);
                this.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                // this.image.setPadding(8, 8, 8, 8);
                this.itemView.setOnClickListener(this);
                this.itemView.setOnLongClickListener(this);
            }

            @Override
            public void onClick(View v) {
                // Forwards to the adapter's constructor callback
                if (mCallback != null) mCallback.onClick(getAdapterPosition());
            }

            @Override
            public boolean onLongClick(View v) {
                // Forwards to the adapter's constructor callback
                if (mCallback != null) mCallback.onLongClick(getAdapterPosition());
                return true;
            }
        }

    }

    ThumbAdapter myThumbAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.activity_gallery);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(config);

        mTargetSize = new ImageSize(220, 220); // result Bitmap will be fit to this size

        ArrayList<String> ab = new ArrayList<>();
        myThumbAdapter = new ThumbAdapter(this, ab);
        // new Utils(getApplicationContext()).getFilePaths(););

        recyclerView = (DragSelectRecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(myThumbAdapter);

        deleteConfirmBuilder = new AlertDialog.Builder(this);

        deleteConfirmBuilder.setTitle(getString(R.string.confirm_title));
        deleteConfirmBuilder.setMessage(getString(R.string.confirm_delete_multiple_text));

        deleteConfirmBuilder.setPositiveButton(getString(R.string.answer_yes), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                deleteImage();
                dialog.dismiss();
            }

        });

        deleteConfirmBuilder.setNegativeButton(getString(R.string.answer_no), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

//        initBannerAds();

//        initBackup();
    }

//    private void initBackup() {
//        autoEmail = new AutoEmail(this);
//        autoNextCloud = new AutoNextCloud(this);
//    }

//    private void initBannerAds() {
//        final AdView mAdView = (AdView) findViewById(R.id.adView);
//
//        if (mAdView != null) {
//            mAdView.setAdListener(new AdListener() {
//                @Override
//                public void onAdFailedToLoad(int i) {
//                    super.onAdFailedToLoad(i);
//                    mAdView.setVisibility(View.GONE);
//                }
//
//                @Override
//                public void onAdLoaded() {
//                    super.onAdLoaded();
//                    mAdView.setVisibility(View.VISIBLE);
//                }
//            });
//
//            AdRequest adRequest = new AdRequest.Builder().build();
//            mAdView.loadAd(adRequest);
//        }
//    }

    private void reloadAdapter() {
        recyclerView.setAdapter(null);

        // ArrayList<String> ab = new ArrayList<>();
        myThumbAdapter = new ThumbAdapter(this, new Utils(getApplicationContext()).getFilePaths());

        recyclerView.setAdapter(myThumbAdapter);
        recyclerView.invalidate();

        setSelectionMode(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadAdapter();
    }

    private void deleteImage() {
        for (String filePath : myThumbAdapter.getSelectedFiles()) {
            final File photoFile = new File(filePath);
            if (photoFile.delete()) {
                Utils.removeImageFromGallery(filePath, this);
                Log.d(TAG, "Removed file: " + filePath);
            }
        }
        reloadAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gallery, menu);

        mShare = menu.findItem(R.id.action_share);
        mShare.setVisible(false);

        //mTag = menu.findItem(R.id.action_tag);
        // mTag.setVisible(false);

        mDelete = menu.findItem(R.id.action_delete);
        mDelete.setVisible(false);

        invalidateOptionsMenu();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_share:
                shareImages();
                return true;
            /*case R.id.action_tag:
                break;*/
            case R.id.action_delete:
                deleteConfirmBuilder.create().show();
                return true;
            case R.id.action_create_pdf:
                getImageToCreatePDF();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void shareImages() {

        final Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.setType("image/jpg");

        ArrayList<Uri> filesUris = new ArrayList<>();

        for (String i : myThumbAdapter.getSelectedFiles()) {
            filesUris.add(Uri.parse("file://" + i));
        }
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, filesUris);

        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_snackbar)));
    }

    private void getImageToCreatePDF() {
        Intent intent = new Intent(GalleryGridActivity.this, ImagePickerActivity.class);
        startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
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
                Toast.makeText(GalleryGridActivity.this, getString(R.string.no_image_selected), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void createPdf() {
        String folderName = mSharedPref.getString("storage_folder", FOLDER_NAME);
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()
                + "/" + folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        path = folder.getAbsolutePath() + "/";

        if (imagesUri.size() > 0) {

            showCustomView();
           /* new MaterialDialog.Builder(GalleryGridActivity.this)
                    .title("Creating PDF")
                    .content("Enter file name")

                    .input("Example : abc", null, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            if (input == null) {
                                Toast.makeText(GalleryGridActivity.this, "Name cannot be blank", Toast.LENGTH_LONG).show();
                            } else {
                                filename = input.toString();
                                new creatingPDF().execute();
                            }
                        }
                    })
                    .show();*/
        }
    }

    private Spinner spinnerPageSize, spinnerImageScale;
    private EditText input_name_pdf;
    private static int positionOfPageSize = 1; // default A4
    private static int positionOfImageScale = 0; // default FIT Width or Height

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
                        new creatingPDF().execute();

                    }
                } catch (Exception e) {
                    Toast.makeText(GalleryGridActivity.this, getString(R.string.error_blank_name), Toast.LENGTH_SHORT).show();
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
        MaterialDialog.Builder builder = new MaterialDialog.Builder(GalleryGridActivity.this)
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

            //Toast.makeText(GalleryGridActivity.this, getString(R.string.creating_pdf_success), Toast.LENGTH_SHORT).show();
        }
    }
}
