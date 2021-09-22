package com.gpaddyv1.queenscanner.document;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.github.clans.fab.FloatingActionButton;
import com.gpaddy.hungdh.DynamicAdapter;
import com.gpaddy.hungdh.OnChangeDynamic;
import com.gpaddy.hungdh.OnLongClickItem;
import com.gpaddy.hungdh.base.BaseActivity;
import com.gpaddy.hungdh.util.Const;
import com.gpaddy.hungdh.util.ImageUtils;
import com.gpaddyv1.queenscanner.Config.AdsTask;
import com.gpaddyv1.queenscanner.activities.SimpleDocumentScannerActivity;
import com.joshuabutton.queenscanner.handle.HandleActivity;
import com.todobom.queenscanner.R;

import org.askerov.dynamicgrid.DynamicGridView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.joshuabutton.queenscanner.PresenterScanner.FOLDER_NAME;

public class DocumentActivity extends BaseActivity implements com.joshuabutton.queenscanner.document.DocumentContract.DocumentView, OnLongClickItem, OnChangeDynamic {
    private com.joshuabutton.queenscanner.document.DocumentContract.IDocumentPresenter presenter;
    private com.joshuabutton.queenscanner.document.DocumentAdapter adapter;
    private DynamicAdapter dynamicAdapter;
    @BindView(R.id.rcView)
    RecyclerView rcView;
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.imgMenu)
    ImageView imgMenu;
    @BindView(R.id.edtName)
    EditText editName;
    @BindView(R.id.imgDone)
    ImageView imgDone;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.dynamic_grid)
    DynamicGridView gridView;
    @BindView(R.id.tv_suggest)
    TextView tvSuggest;

    @BindView(R.id.menu_item_camera)
    FloatingActionButton fabCamera;
    @BindView(R.id.menu_item_gallery)
    FloatingActionButton fabGallery;
    private String folder;
    private static final int TYPE_NEW_CREATE = 1;
    private int type = 0;
    public static final int REQUEST_IMPORT = 100;
    private String pathCamera = "";
    private int REQUEST_CAMERA = 102;
    private int REQUEST_CAMERA_PERMISSION = 201;
    boolean isSave = false;
    private AdsTask adsTask;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_document;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initData() {
        adsTask = new AdsTask(this);
        folder = getIntent().getStringExtra("folder");
        type = getSharedPreferences("BVH", MODE_PRIVATE).getInt("type", 1);

        adapter.loadData(presenter.getListDocument(folder));
        dynamicAdapter = new DynamicAdapter(this, presenter.getListDocument(folder), 2);
        dynamicAdapter.setPresenter(presenter);
        dynamicAdapter.setOnLongClickItem(this);
        dynamicAdapter.setOnChangeDynamic(this);

        gridView.setAdapter(dynamicAdapter);
        File file = new File(folder);
        if (type == TYPE_NEW_CREATE) {
            editName.setVisibility(View.VISIBLE);
            imgDone.setVisibility(View.VISIBLE);
            tvName.setVisibility(View.GONE);
            imgMenu.setVisibility(View.GONE);
            editName.setText(file.getName());
            setNameFile(file.getName());
            editName.setSelection(editName.getText().toString().length());
        } else {
            imgDone.setVisibility(View.GONE);
            editName.setVisibility(View.GONE);
            tvName.setVisibility(View.VISIBLE);
            imgMenu.setVisibility(View.VISIBLE);
            tvName.setText(file.getName());
        }


    }

    @Override
    protected void initView() {
        getSupportActionBar().hide();

        presenter = new com.joshuabutton.queenscanner.document.DocumentPresenter(this, this);
        rcView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new com.joshuabutton.queenscanner.document.DocumentAdapter(this, presenter);
//        rcView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {
        HandleActivity.startHandle(DocumentActivity.this, position, folder);
    }

    @Override
    public String getDefaultName() {
        return folder;
    }

    @Override
    public void setFolderName(String folderName) {
        this.folder = folderName;
    }

    @Override
    public void setNameFile(String nameFile) {
        tvName.setText(nameFile);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMPORT && resultCode == RESULT_OK) {
//            Log.e("bvh", data.toString());
//            adapter.loadData(presenter.getListDocument(folder));
            dynamicAdapter = new DynamicAdapter(this, presenter.getListDocument(folder), 2);
            dynamicAdapter.setPresenter(presenter);
            dynamicAdapter.setOnLongClickItem(this);
            dynamicAdapter.setOnChangeDynamic(this);
            gridView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_UP:
                            if (gridView.isEditMode()) {
                                gridView.stopEditMode();
                                tvSuggest.setVisibility(View.GONE);
                            }
                            break;
                    }
                    return true;
                }
            });

            gridView.setAdapter(dynamicAdapter);
        } else if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            pathCamera = getSharedPreferences("BVH", MODE_PRIVATE).getString("path", pathCamera);
            File f = new File(pathCamera);
            if (f.exists()) {
//                Toast.makeText(this, "exist", Toast.LENGTH_SHORT).show();
//                SimpleDocumentScannerActivity.startScanner(DocumentActivity.this, pathCamera, "");
                getSharedPreferences("BVH", MODE_PRIVATE).edit().putInt("type", 0).commit();
                SimpleDocumentScannerActivity.startScanner(DocumentActivity.this, pathCamera, folder);
//                dynamicAdapter = new DynamicAdapter(this, presenter.getListDocument(folder), 2);
//                dynamicAdapter.setPresenter(presenter);
//                dynamicAdapter.setOnLongClickItem(this);
//                dynamicAdapter.setOnChangeDynamic(this);
                finish();
//                gridView.setAdapter(dynamicAdapter);
            } else {
                imgDone.setVisibility(View.GONE);
                imgMenu.setVisibility(View.VISIBLE);
                Toast.makeText(this, getString(R.string.file_not_found), Toast.LENGTH_SHORT).show();
            }
        }
//
    }

    @Override
    protected void onResume() {
//        adapter.loadData(presenter.getListDocument(folder));
        super.onResume();
    }

    @OnClick(R.id.imgBack)
    public void onBack() {
//        startActivity(new Intent(DocumentActivity.this, MainActivity.class));
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        if (!isSave) {
//            isSave = true;
//            SaveData saveData = new SaveData();
//            saveData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        }

    }

    class SaveData extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DocumentActivity.this);
            progressDialog.setMessage("Save file");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            List<Object> arrModel = dynamicAdapter.getItems();
            int po = 0;
            for (int i = 0; i < arrModel.size(); i++) {
                com.joshuabutton.queenscanner.document.DocumentModel documentModel = (com.joshuabutton.queenscanner.document.DocumentModel) arrModel.get(i);
                String path = documentModel.getPath();

                int index = path.lastIndexOf(File.separator);
                String newPath = path.substring(0, index) + File.separator + "A" + System.currentTimeMillis() + po + ".jpg";
                Log.e("bvh", "check path new : " + newPath);
                Log.e("bvh", "check path old : " + newPath);
                po++;

                renameFile(documentModel.getPath(), newPath);
            }
            return null;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();

            dynamicAdapter = new DynamicAdapter(DocumentActivity.this, presenter.getListDocument(folder), 2);
            dynamicAdapter.setPresenter(presenter);
            dynamicAdapter.setOnLongClickItem(DocumentActivity.this);
            dynamicAdapter.setOnChangeDynamic(DocumentActivity.this);
            gridView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_UP:
                            while (gridView.isEditMode()) {
                                gridView.stopEditMode();
                                tvSuggest.setVisibility(View.GONE);
                                if (!isSave) {
                                    isSave = true;
                                    SaveData saveData = new SaveData();
                                    saveData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                }
                            }
                            break;
                    }
                    return true;
                }
            });

            gridView.setAdapter(dynamicAdapter);
            isSave = false;
        }
    }

    public boolean renameFile(String oldPath, String newPath) {
        File oldFile = new File(oldPath);
        File newFile = new File(newPath);
        if (newFile.exists()) {
            Log.e("bvh", "rename: false");
            return false;
        } else {

            if (oldFile.renameTo(newFile)) {
                Log.e("bvh", "rename: true");
                return true;
            } else {
                Log.e("bvh", "rename: true");
                return false;
            }
        }
    }

    @SuppressLint("RestrictedApi")
    @OnClick(R.id.imgMenu)
    public void onMenu() {
        PopupMenu pop = new PopupMenu(this, imgMenu);
        pop.getMenuInflater().inflate(R.menu.menu_doc, pop.getMenu());

        pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                final File file = new File(folder);
                switch (menuItem.getItemId()) {
                    case R.id.menuRename:
                        showDialog();
                        break;
                    case R.id.menuImport:
                        SimpleDocumentScannerActivity.startScanner(DocumentActivity.this, "", folder);
                        finish();
                        break;
                    case R.id.menuOpen:
                        presenter.openWith(getDefaultName() + "/" + tvName.getText().toString() + ".pdf");
                        break;
                    case R.id.menuShare:
                        presenter.shareFile(getDefaultName() + "/" + tvName.getText().toString() + ".pdf");
                        break;
                    case R.id.menuSaveToPDF:
                        String pdfName = file.getName() + System.currentTimeMillis() + ".pdf";
                        List<String> myPath = new ArrayList<>();
                        List<Object> arrModel = dynamicAdapter.getItems();
                        for (Object o : arrModel) {
                            com.joshuabutton.queenscanner.document.DocumentModel documentModel = (com.joshuabutton.queenscanner.document.DocumentModel) o;
                            myPath.add(documentModel.getPath());
                        }
                        ImageUtils.convertImageToPdf(myPath, folder + "/" + pdfName, DocumentActivity.this);
                        break;
                    case R.id.menuDelete:
                        if (file.exists()) {
                            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DocumentActivity.this);
                            builder
                                    .setMessage("Delete this document folder?")
                                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            for (File file1 : file.listFiles())
                                                file1.delete();
                                            file.delete();
                                            Toast.makeText(DocumentActivity.this, "Deleted", Toast.LENGTH_LONG).show();
                                            adapter.notifyDataSetChanged();
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();

                                        }
                                    });
                            final android.app.AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                }
                return true;
            }
        });
        MenuPopupHelper menuHelper = new MenuPopupHelper(this, (MenuBuilder) pop.getMenu(), imgMenu);
        menuHelper.setForceShowIcon(true);
        menuHelper.show();
    }

    @OnClick(R.id.menu_item_gallery)
    public void importFromGallery() {
        getSharedPreferences("BVH", MODE_PRIVATE).edit().putInt("type", 0).commit();
        SimpleDocumentScannerActivity.startScanner(DocumentActivity.this, "", folder);
        finish();
    }

    @OnClick(R.id.menu_item_camera)
    public void importFromCamera() {
        if (ActivityCompat.checkSelfPermission(DocumentActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            callCamera();
        } else {
            ActivityCompat.requestPermissions(DocumentActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    private void callCamera() {
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String folderName = mSharedPref.getString("storage_folder", FOLDER_NAME);
//        File folder = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//                + "/" + folderName);
//        if (!folder.exists()) {
//            folder.mkdirs();
//        }
        pathCamera = this.folder + "/.TEMP_CAMERA.xxx";
        getSharedPreferences("BVH", MODE_PRIVATE).edit().putString("path", pathCamera).commit();
        File f = new File(pathCamera);
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri outputFileUri;
        if (Build.VERSION.SDK_INT < 24)
            outputFileUri = Uri.fromFile(f);
        else {
            outputFileUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", f);
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CAMERA);
        }
    }

    @OnClick(R.id.imgDone)
    public void onDone() {
        String filePdfName = editName.getText().toString();
        presenter.reName(filePdfName);
        Const.hideKeyboardFrom(DocumentActivity.this, editName);
        imgDone.setVisibility(View.GONE);
        editName.setVisibility(View.GONE);
        tvName.setVisibility(View.VISIBLE);
        imgMenu.setVisibility(View.VISIBLE);

    }

    private void showDialog() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_rename, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(mView);

        final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
        userInputDialogEditText.setText(tvName.getText().toString());
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        presenter.reName(userInputDialogEditText.getText().toString());
                    }
                })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void longClickItem(int po) {
        gridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        while (gridView.isEditMode()) {
                            gridView.stopEditMode();
                            tvSuggest.setVisibility(View.GONE);
                            if (!isSave) {
                                isSave = true;
                                SaveData saveData = new SaveData();
                                saveData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                        }
                        break;
                }
                return false;
            }
        });
        tvSuggest.setVisibility(View.VISIBLE);
        gridView.startEditMode(po);
    }

    @Override
    public void onChangeItem() {
//        Toast.makeText(this, "changer", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

